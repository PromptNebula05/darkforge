package darkforge.database;

import darkforge.exception.DatabaseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Owns the single jdbc:sqlite Connection for DARKFORGE
 * and runs the hybrid schema bootstrap.
 *
 * Hybrid bootstrap (Module 6 best practice -- the table
 * structure is scriptable; DML is app-driven at runtime):
 *   1. Open the connection (creates darkforge.db if absent).
 *   2. PRAGMA foreign_keys = ON. SQLite only enforces FKs
 *      per-connection when this is set.
 *   3. Apply the shipped /schema.sql via JDBC DDL. Every
 *      statement is CREATE TABLE IF NOT EXISTS, so running
 *      against an existing database is a no-op.
 *   4. Verify the three expected tables exist in
 *      sqlite_master.
 *
 * All failures are wrapped in DatabaseException (mirrors the
 * ConcurrencyException pattern) so the CLI's existing
 * DarkForgeException handler can present a player-facing
 * message.
 *
 * Phase 1 scope: connectivity + schema only. The DAO layer,
 * the FacadeDatabase subfacade, and CLI option [15] arrive
 * in Phase 2.
 */
public class DatabaseManager {

    public static final String DEFAULT_DB_URL =
            "jdbc:sqlite:darkforge.db";

    private static final String SCHEMA_RESOURCE =
            "/schema.sql";

    private static final List<String> EXPECTED_TABLES =
            Arrays.asList("crew", "explorer", "crew_membership");

    private final String jdbcUrl;
    private Connection connection;

    // =========================================
    // Constructors
    // =========================================

    public DatabaseManager() {
        this(DEFAULT_DB_URL);
    }

    public DatabaseManager(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    // =========================================
    // Lifecycle
    // =========================================

    /**
     * Open the connection and run the hybrid bootstrap.
     * Idempotent: a second call returns the live connection.
     */
    public Connection connect() {
        if (connection != null) {
            return connection;
        }
        try {
            connection =
                    DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Could not open SQLite database at "
                            + jdbcUrl, e);
        }
        enableForeignKeys();
        bootstrapSchema();
        if (!verifyTables()) {
            throw new DatabaseException(
                    "Schema verification failed after"
                            + " bootstrap");
        }
        return connection;
    }

    public void close() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to close database connection",
                    e);
        } finally {
            connection = null;
        }
    }

    // =========================================
    // Schema bootstrap
    // =========================================

    private void enableForeignKeys() {
        try (Statement st =
                     connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to enable foreign key"
                            + " enforcement", e);
        }
    }

    /**
     * Apply /schema.sql. All statements are
     * CREATE TABLE IF NOT EXISTS, so this is safe to
     * re-run against an existing database.
     */
    public void bootstrapSchema() {
        requireConnection();
        String script = readSchemaScript();
        try (Statement st =
                     connection.createStatement()) {
            for (String statement
                    : splitStatements(script)) {
                st.execute(statement);
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to apply schema.sql", e);
        }
    }

    /**
     * True only when all three expected tables exist
     * in sqlite_master.
     */
    public boolean verifyTables() {
        requireConnection();
        for (String table : EXPECTED_TABLES) {
            if (!tableExists(table)) {
                return false;
            }
        }
        return true;
    }

    public boolean tableExists(String tableName) {
        requireConnection();
        String sql =
                "SELECT name FROM sqlite_master "
                        + "WHERE type = 'table' AND name = ?";
        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Failed to query sqlite_master for '"
                            + tableName + "'", e);
        }
    }

    // =========================================
    // Accessors
    // =========================================

    public Connection getConnection() {
        requireConnection();
        return connection;
    }

    public boolean isConnected() {
        return connection != null;
    }

    // =========================================
    // Helpers
    // =========================================

    private void requireConnection() {
        if (connection == null) {
            throw new DatabaseException(
                    "Database not connected -- call"
                            + " connect() first");
        }
    }

    private String readSchemaScript() {
        try (InputStream in = getClass()
                .getResourceAsStream(SCHEMA_RESOURCE)) {
            if (in == null) {
                throw new DatabaseException(
                        "schema.sql not found on classpath"
                                + " at " + SCHEMA_RESOURCE);
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(in,
                                         StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine())
                        != null) {
                    sb.append(line).append('\n');
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new DatabaseException(
                    "Failed to read schema.sql", e);
        }
    }

    /**
     * Split a SQL script into individual statements on
     * ';', stripping -- line comments and blank lines.
     * Sufficient for schema.sql (no ';' inside string
     * literals).
     */
    private List<String> splitStatements(
            String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String rawLine : script.split("\n")) {
            String line = rawLine;
            int comment = line.indexOf("--");
            if (comment >= 0) {
                line = line.substring(0, comment);
            }
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            current.append(line).append(' ');
            if (line.endsWith(";")) {
                statements.add(
                        current.toString().trim());
                current.setLength(0);
            }
        }
        if (!current.toString().trim().isEmpty()) {
            statements.add(current.toString().trim());
        }
        return statements;
    }
}
