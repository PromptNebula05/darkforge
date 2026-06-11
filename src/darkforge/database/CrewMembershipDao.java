package darkforge.database;

import darkforge.crew.CrewRole;
import darkforge.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the crew_membership join table.
 * Each row associates an explorer with a crew under a
 * single CrewRole. role is stored as CrewRole.name()
 * (e.g. "DELVER") and rebuilt with CrewRole.valueOf(...).
 */
public class CrewMembershipDao {

    /** One membership row, role already decoded. */
    public record MembershipRecord(
            long explorerId, CrewRole role) {}

    private final Connection connection;

    public CrewMembershipDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(long crewId, long explorerId,
                       CrewRole role) {
        String sql =
                "INSERT INTO crew_membership "
                        + "(crew_id, explorer_id, role) "
                        + "VALUES (?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {
            ps.setLong(1, crewId);
            ps.setLong(2, explorerId);
            ps.setString(3, role.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to insert membership (crew="
                            + crewId + ", explorer="
                            + explorerId + ")", e);
        }
    }

    public List<MembershipRecord> findByCrewId(
            long crewId) {
        String sql =
                "SELECT explorer_id, role "
                        + "FROM crew_membership "
                        + "WHERE crew_id = ?";
        List<MembershipRecord> result =
                new ArrayList<>();
        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {
            ps.setLong(1, crewId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new MembershipRecord(
                            rs.getLong("explorer_id"),
                            CrewRole.valueOf(
                                    rs.getString("role"))));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to load memberships for crew="
                            + crewId, e);
        }
        return result;
    }

    /**
     * One joined roster row: explorer name, crew name,
     * and the decoded role. Backs template §6.7
     * (selection across two or more tables).
     */
    public record RosterEntry(
            String explorer, String crew, CrewRole role) {}

    /**
     * Aggregated per-crew statistics. Backs template
     * §6.9 (selection + aggregation).
     */
    public record CrewStat(
            String crew, int memberCount,
            double avgStrength) {}

    /**
     * Roster view across all three tables: every
     * (explorer, crew, role) triple, ordered by crew
     * then role. Joins crew_membership to explorer and
     * crew on their foreign keys.
     */
    public List<RosterEntry> rosterView() {
        String sql =
                "SELECT e.name AS explorer, "
                        + "c.name AS crew, m.role "
                        + "FROM crew_membership m "
                        + "JOIN explorer e "
                        + "  ON e.explorer_id = m.explorer_id "
                        + "JOIN crew c "
                        + "  ON c.crew_id = m.crew_id "
                        + "ORDER BY c.name ASC, m.role ASC";
        List<RosterEntry> result = new ArrayList<>();
        try (PreparedStatement ps =
                     connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new RosterEntry(
                        rs.getString("explorer"),
                        rs.getString("crew"),
                        CrewRole.valueOf(
                                rs.getString("role"))));
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to build roster view", e);
        }
        return result;
    }

    /**
     * Per-crew aggregation: member headcount and
     * average Strength, largest crews first. GROUP BY
     * over the three-table join; empty crews are
     * dropped via HAVING.
     */
    public List<CrewStat> crewStats() {
        String sql =
                "SELECT c.name AS crew, "
                        + "COUNT(m.explorer_id) AS member_count, "
                        + "AVG(e.strength) AS avg_strength "
                        + "FROM crew c "
                        + "JOIN crew_membership m "
                        + "  ON m.crew_id = c.crew_id "
                        + "JOIN explorer e "
                        + "  ON e.explorer_id = m.explorer_id "
                        + "GROUP BY c.crew_id, c.name "
                        + "HAVING COUNT(m.explorer_id) > 0 "
                        + "ORDER BY member_count DESC, crew ASC";
        List<CrewStat> result = new ArrayList<>();
        try (PreparedStatement ps =
                     connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new CrewStat(
                        rs.getString("crew"),
                        rs.getInt("member_count"),
                        rs.getDouble("avg_strength")));
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to compute crew stats", e);
        }
        return result;
    }
}
