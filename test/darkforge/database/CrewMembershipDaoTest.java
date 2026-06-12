package darkforge.database;

import static org.junit.jupiter.api.Assertions.*;

import darkforge.crew.Crew;
import darkforge.crew.CrewRole;
import darkforge.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Join-table behavior for CrewMembershipDao: role
 * round-trips through CrewRole, and the FK / composite-PK
 * / role-CHECK constraints are all enforced.
 */
class CrewMembershipDaoTest {

    private DatabaseManager manager;
    private ExplorerDao explorerDao;
    private CrewDao crewDao;
    private CrewMembershipDao membershipDao;

    @BeforeEach
    void setUp() {
        manager = new DatabaseManager(
                "jdbc:sqlite::memory:");
        manager.connect();
        Connection conn = manager.getConnection();
        explorerDao = new ExplorerDao(conn);
        crewDao = new CrewDao(conn);
        membershipDao = new CrewMembershipDao(conn);
    }

    @AfterEach
    void tearDown() {
        manager.close();
    }

    @Test
    void insertThenFindDecodesRole() throws Exception {
        long crewId =
                crewDao.insert(new Crew("Wayfarers"));
        long explorerId = explorerDao.insert(
                TestExplorers.of("Scholar", "Vada",
                        3, 3, 3, 3, 3, 3));

        membershipDao.insert(
                crewId, explorerId, CrewRole.DELVER);

        List<CrewMembershipDao.MembershipRecord> rows =
                membershipDao.findByCrewId(crewId);
        assertEquals(1, rows.size());
        assertEquals(explorerId,
                rows.get(0).explorerId());
        assertEquals(CrewRole.DELVER,
                rows.get(0).role());
    }

    @Test
    void orphanMembershipRejectedByForeignKey() {
        // No such crew/explorer rows exist; FK
        // enforcement (PRAGMA foreign_keys = ON) must
        // reject this insert.
        assertThrows(DatabaseException.class,
                () -> membershipDao.insert(
                        999L, 999L, CrewRole.SCOUT));
    }

    @Test
    void duplicateMembershipRejectedByCompositePk()
            throws Exception {
        long crewId =
                crewDao.insert(new Crew("Wayfarers"));
        long explorerId = explorerDao.insert(
                TestExplorers.of("Artist", "Ada",
                        3, 3, 3, 3, 3, 3));
        membershipDao.insert(
                crewId, explorerId, CrewRole.GUARD);
        // Same (crew_id, explorer_id) violates the
        // composite primary key.
        assertThrows(DatabaseException.class,
                () -> membershipDao.insert(
                        crewId, explorerId, CrewRole.SCOUT));
    }

    @Test
    void badRoleRejectedByCheck() throws Exception {
        long crewId =
                crewDao.insert(new Crew("Wayfarers"));
        long explorerId = explorerDao.insert(
                TestExplorers.of("Scholar", "Vada",
                        3, 3, 3, 3, 3, 3));
        // Raw insert with an invalid role token -> the
        // role CHECK list rejects it at the DB layer.
        Connection conn = manager.getConnection();
        String sql = "INSERT INTO crew_membership "
                + "(crew_id, explorer_id, role) "
                + "VALUES (?, ?, ?)";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setLong(1, crewId);
            ps.setLong(2, explorerId);
            ps.setString(3, "CAPTAIN");  // not a CrewRole
            assertThrows(java.sql.SQLException.class,
                    ps::executeUpdate);
        }
    }
}
