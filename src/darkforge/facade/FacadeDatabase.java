package darkforge.facade;

import darkforge.crew.Crew;
import darkforge.crew.CrewRole;
import darkforge.database.CrewDao;
import darkforge.database.CrewMembershipDao;
import darkforge.database.DatabaseManager;
import darkforge.database.ExplorerDao;
import darkforge.exception.DatabaseException;
import darkforge.model.Explorer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Subfacade for darkforge.database. Mirrors
 * FacadeConcurrency: package-private constructor; the
 * single instance is built and held by
 * FacadeDarkforge.initialize() and exposed via
 * databaseAccess().
 *
 * Owns one DatabaseManager (and therefore one JDBC
 * Connection) shared by all three DAOs, which lets
 * saveCrew run as a single transaction.
 */
public class FacadeDatabase {

    private final DatabaseManager manager;
    private final ExplorerDao explorerDao;
    private final CrewDao crewDao;
    private final CrewMembershipDao membershipDao;

    // Package-private. Built by FacadeDarkforge.initialize().
    FacadeDatabase() {
        this(new DatabaseManager());
    }

    FacadeDatabase(DatabaseManager manager) {
        this.manager = manager;
        manager.connect();   // open + bootstrap + verify
        Connection conn = manager.getConnection();
        this.explorerDao = new ExplorerDao(conn);
        this.crewDao = new CrewDao(conn);
        this.membershipDao = new CrewMembershipDao(conn);
    }

    // =========================================
    // Write (transactional)
    // =========================================

    /**
     * Persist a crew, its members, and their role
     * memberships as one transaction. Every member must
     * have an assigned role (role is NOT NULL in the
     * schema); otherwise the whole save rolls back.
     *
     * @return the generated crew_id
     */
    public long saveCrew(Crew crew) {
        Connection conn = manager.getConnection();
        try {
            conn.setAutoCommit(false);
            long crewId = crewDao.insert(crew);
            for (Explorer member : crew.getMembers()) {
                CrewRole role =
                        crew.getAssignedRole(member);
                if (role == null) {
                    throw new DatabaseException(
                            "Crew member '"
                                    + member.getName()
                                    + "' has no assigned role;"
                                    + " assign all roles before"
                                    + " saving to the database.");
                }
                long explorerId =
                        explorerDao.insert(member);
                membershipDao.insert(
                        crewId, explorerId, role);
            }
            conn.commit();
            return crewId;
        } catch (DatabaseException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            throw new DatabaseException(
                    "Failed to save crew '"
                            + crew.getName() + "'", e);
        } finally {
            restoreAutoCommit(conn);
        }
    }

    // =========================================
    // Read
    // =========================================

    public List<CrewDao.CrewRecord> listCrews() {
        return crewDao.findAll();
    }

    public List<Explorer> listExplorers() {
        return explorerDao.findAll();
    }

    /** Three-table roster join (template §6.7). */
    public List<CrewMembershipDao.RosterEntry> roster() {
        return membershipDao.rosterView();
    }

    /** Per-crew aggregation (template §6.9). */
    public List<CrewMembershipDao.CrewStat> crewStats() {
        return membershipDao.crewStats();
    }

    /**
     * Reload a crew with its members and roles
     * reconstructed via ExplorerFactory. Note: the Bird
     * is not rebuilt (only bird_name is stored); full
     * Bird/vehicle state remains the serialization
     * layer's responsibility.
     *
     * @return the rebuilt Crew, or null if not found
     */
    public Crew loadCrew(String name) {
        CrewDao.CrewRecord rec =
                crewDao.findByName(name);
        if (rec == null) {
            return null;
        }
        Crew crew = new Crew(rec.name());
        for (CrewMembershipDao.MembershipRecord row
                : membershipDao.findByCrewId(
                rec.crewId())) {
            Explorer member =
                    explorerDao.findById(row.explorerId());
            if (member == null) {
                continue;
            }
            crew.addMember(member);
            crew.assignRole(row.role(), member);
        }
        return crew;
    }

    public boolean deleteCrew(String name) {
        // ON DELETE CASCADE removes crew_membership rows.
        return crewDao.deleteByName(name);
    }

    // =========================================
    // Lifecycle
    // =========================================

    public void shutdown() {
        manager.close();
    }

    // =========================================
    // Helpers
    // =========================================

    private void rollbackQuietly(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // Best-effort rollback; original error wins.
        }
    }

    private void restoreAutoCommit(Connection conn) {
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ignored) {
            // Leave as-is if it cannot be restored.
        }
    }
}
