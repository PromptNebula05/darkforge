package darkforge.database;

import static org.junit.jupiter.api.Assertions.*;

import darkforge.crew.Crew;
import darkforge.exception.DatabaseException;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CRUD + UNIQUE-name enforcement for CrewDao. bird_name is
 * persisted as a plain, nullable column.
 */
class CrewDaoTest {

    private DatabaseManager manager;
    private CrewDao dao;

    @BeforeEach
    void setUp() {
        manager = new DatabaseManager(
                "jdbc:sqlite::memory:");
        manager.connect();
        dao = new CrewDao(manager.getConnection());
    }

    @AfterEach
    void tearDown() {
        manager.close();
    }

    @Test
    void insertThenFindByNameRoundTrip() {
        long id = dao.insert(new Crew("Wayfarers"));
        assertTrue(id > 0);

        CrewDao.CrewRecord rec =
                dao.findByName("Wayfarers");
        assertNotNull(rec);
        assertEquals("Wayfarers", rec.name());
        assertEquals(id, rec.crewId());
        // No bird set -> bird_name is null.
        assertNull(rec.birdName());
    }

    @Test
    void duplicateCrewNameRejectedByUnique() {
        dao.insert(new Crew("Wayfarers"));
        assertThrows(DatabaseException.class,
                () -> dao.insert(new Crew("Wayfarers")));
    }

    @Test
    void findAllOrdersByName() {
        dao.insert(new Crew("Zephyr"));
        dao.insert(new Crew("Anvil"));
        List<CrewDao.CrewRecord> all = dao.findAll();
        assertEquals(2, all.size());
        assertEquals("Anvil", all.get(0).name());
        assertEquals("Zephyr", all.get(1).name());
    }

    @Test
    void deleteByNameRemovesCrew() {
        dao.insert(new Crew("Wayfarers"));
        assertTrue(dao.deleteByName("Wayfarers"));
        assertNull(dao.findByName("Wayfarers"));
        assertFalse(dao.deleteByName("Wayfarers"));
    }
}
