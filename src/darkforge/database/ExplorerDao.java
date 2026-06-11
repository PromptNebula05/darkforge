package darkforge.database;

import darkforge.creation.ExplorerFactory;
import darkforge.exception.DatabaseException;
import darkforge.exception.InvalidProfessionException;
import darkforge.model.Attribute;
import darkforge.model.Explorer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Data Access Object for the explorer table.
 *
 * Storage encoding:
 *   profession -> the raw display name from
 *                 Explorer.getProfessionName(), e.g.
 *                 "Odd Jobber" (no CHECK on profession;
 *                 see schema.sql).
 *   the six attributes -> one INTEGER column each.
 *
 * Reads reconstruct a concrete Explorer subclass via the
 * existing ExplorerFactory, so no persistence logic leaks
 * into the model layer.
 */
public class ExplorerDao {

    private final Connection connection;
    private final ExplorerFactory factory;

    public ExplorerDao(Connection connection) {
        this.connection = connection;
        this.factory = new ExplorerFactory();
    }

    // =========================================
    // Create
    // =========================================

    public long insert(Explorer explorer) {
        String sql =
                "INSERT INTO explorer "
                        + "(name, profession, strength, agility, "
                        + "logic, perception, insight, empathy) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, explorer.getName());
            ps.setString(2,
                    explorer.getProfessionName());
            ps.setInt(3, explorer.getAttribute(
                    Attribute.STRENGTH));
            ps.setInt(4, explorer.getAttribute(
                    Attribute.AGILITY));
            ps.setInt(5, explorer.getAttribute(
                    Attribute.LOGIC));
            ps.setInt(6, explorer.getAttribute(
                    Attribute.PERCEPTION));
            ps.setInt(7, explorer.getAttribute(
                    Attribute.INSIGHT));
            ps.setInt(8, explorer.getAttribute(
                    Attribute.EMPATHY));
            ps.executeUpdate();
            try (ResultSet keys =
                         ps.getGeneratedKeys()) {
                return keys.next()
                        ? keys.getLong(1) : -1L;
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to insert explorer '"
                            + explorer.getName() + "'", e);
        }
    }

    // =========================================
    // Read
    // =========================================

    public Explorer findById(long explorerId) {
        String sql =
                "SELECT name, profession, strength, "
                        + "agility, logic, perception, insight, "
                        + "empathy FROM explorer "
                        + "WHERE explorer_id = ?";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {
            ps.setLong(1, explorerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to load explorer id="
                            + explorerId, e);
        }
    }

    public List<Explorer> findAll() {
        String sql =
                "SELECT name, profession, strength, "
                        + "agility, logic, perception, insight, "
                        + "empathy FROM explorer "
                        + "ORDER BY profession ASC, name ASC";
        List<Explorer> result = new ArrayList<>();
        try (PreparedStatement ps =
                     connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to list explorers", e);
        }
        return result;
    }

    // =========================================
    // Row -> Explorer
    // =========================================

    private Explorer mapRow(ResultSet rs)
            throws SQLException {
        String name = rs.getString("name");
        String profession = rs.getString("profession");
        Explorer explorer;
        try {
            explorer = factory.createProfessionInstance(
                    profession, name);
        } catch (InvalidProfessionException e) {
            throw new DatabaseException(
                    "Stored profession '" + profession
                            + "' is not a known profession", e);
        }
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        attrs.put(Attribute.STRENGTH,
                rs.getInt("strength"));
        attrs.put(Attribute.AGILITY,
                rs.getInt("agility"));
        attrs.put(Attribute.LOGIC,
                rs.getInt("logic"));
        attrs.put(Attribute.PERCEPTION,
                rs.getInt("perception"));
        attrs.put(Attribute.INSIGHT,
                rs.getInt("insight"));
        attrs.put(Attribute.EMPATHY,
                rs.getInt("empathy"));
        explorer.setAttributes(attrs);
        return explorer;
    }
}
