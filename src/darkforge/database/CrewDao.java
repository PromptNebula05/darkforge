package darkforge.database;

import darkforge.crew.Crew;
import darkforge.exception.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the crew table.
 *
 * bird_name stores only the bird's name (decision: plain
 * column for this iteration). Deleting a crew cascades to
 * its crew_membership rows via the schema's ON DELETE
 * CASCADE foreign keys.
 */
public class CrewDao {

    /** Lightweight read projection of a crew row. */
    public record CrewRecord(
            long crewId, String name, String birdName) {}

    private final Connection connection;

    public CrewDao(Connection connection) {
        this.connection = connection;
    }

    public long insert(Crew crew) {
        String sql =
                "INSERT INTO crew (name, bird_name) "
                        + "VALUES (?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, crew.getName());
            if (crew.getBird() != null) {
                ps.setString(2,
                        crew.getBird().getName());
            } else {
                ps.setString(2, null);
            }
            ps.executeUpdate();
            try (ResultSet keys =
                         ps.getGeneratedKeys()) {
                return keys.next()
                        ? keys.getLong(1) : -1L;
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to insert crew '"
                            + crew.getName() + "'", e);
        }
    }

    public CrewRecord findByName(String name) {
        String sql =
                "SELECT crew_id, name, bird_name "
                        + "FROM crew WHERE name = ?";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to load crew '" + name + "'",
                    e);
        }
    }

    public List<CrewRecord> findAll() {
        String sql =
                "SELECT crew_id, name, bird_name "
                        + "FROM crew ORDER BY name ASC";
        List<CrewRecord> result = new ArrayList<>();
        try (PreparedStatement ps =
                     connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to list crews", e);
        }
        return result;
    }

    public boolean deleteByName(String name) {
        String sql = "DELETE FROM crew WHERE name = ?";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to delete crew '" + name + "'",
                    e);
        }
    }

    private CrewRecord mapRow(ResultSet rs)
            throws SQLException {
        return new CrewRecord(
                rs.getLong("crew_id"),
                rs.getString("name"),
                rs.getString("bird_name"));
    }
}
