package darkforge.database;

import static org.junit.jupiter.api.Assertions.*;

import darkforge.exception.DatabaseException;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies DatabaseManager: the hybrid bootstrap creates
 * and verifies crew/explorer/crew_membership, is
 * idempotent, and connect/close behave. Most tests use an
 * in-memory database; one @TempDir test proves the schema
 * survives a real reconnect to a file database.
 */
class DatabaseManagerTest {

    private DatabaseManager manager;

    @AfterEach
    void tearDown() {
        if (manager != null && manager.isConnected()) {
            manager.close();
        }
    }

    @Test
    void connectBootstrapsAndVerifiesAllTables() {
        manager = new DatabaseManager(
                "jdbc:sqlite::memory:");
        manager.connect();
        assertTrue(manager.isConnected());
        assertTrue(manager.verifyTables());
        assertTrue(manager.tableExists("crew"));
        assertTrue(manager.tableExists("explorer"));
        assertTrue(
                manager.tableExists("crew_membership"));
    }

    @Test
    void bootstrapIsIdempotent() {
        manager = new DatabaseManager(
                "jdbc:sqlite::memory:");
        manager.connect();
        // Re-applying the DDL must not throw: every
        // statement is CREATE TABLE IF NOT EXISTS.
        manager.bootstrapSchema();
        manager.bootstrapSchema();
        assertTrue(manager.verifyTables());
    }

    @Test
    void closeReleasesConnection() {
        manager = new DatabaseManager(
                "jdbc:sqlite::memory:");
        manager.connect();
        manager.close();
        assertFalse(manager.isConnected());
        // Using the manager after close must fail loudly.
        assertThrows(DatabaseException.class,
                manager::getConnection);
    }

    @Test
    void schemaPersistsAcrossConnections(
            @TempDir Path dir) {
        String url = "jdbc:sqlite:"
                + dir.resolve("roundtrip.db");

        DatabaseManager first =
                new DatabaseManager(url);
        first.connect();
        assertTrue(first.verifyTables());
        first.close();

        // Re-opening the same file finds the tables
        // already present (IF NOT EXISTS = no-op).
        DatabaseManager second =
                new DatabaseManager(url);
        second.connect();
        assertTrue(second.verifyTables());
        second.close();
    }
}
