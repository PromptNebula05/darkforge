package darkforge.database;

import static org.junit.jupiter.api.Assertions.*;

import darkforge.exception.DatabaseException;
import darkforge.model.Attribute;
import darkforge.model.Explorer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Round-trip and reconstruction tests for ExplorerDao.
 *
 * profession is stored as the raw display name with NO
 * CHECK constraint, so an invalid profession is rejected
 * at RELOAD time by ExplorerFactory, not at insert. The
 * attribute CHECK (BETWEEN 2 AND 6) is still enforced by
 * the database and is exercised with a raw insert (the
 * domain model would otherwise reject the value first).
 */
class ExplorerDaoTest {

    private DatabaseManager manager;
    private ExplorerDao dao;

    @BeforeEach
    void setUp() {
        manager = new DatabaseManager(
                "jdbc:sqlite::memory:");
        manager.connect();
        dao = new ExplorerDao(manager.getConnection());
    }

    @AfterEach
    void tearDown() {
        manager.close();
    }

    @Test
    void insertThenReadReconstructsSubclassAndAttributes()
            throws Exception {
        // STR, AGL, LOG, PER, INS, EMP
        Explorer scholar = TestExplorers.of(
                "Scholar", "Vada", 3, 4, 5, 2, 3, 4);
        long id = dao.insert(scholar);
        assertTrue(id > 0);

        Explorer loaded = dao.findById(id);
        assertNotNull(loaded);
        assertEquals("Vada", loaded.getName());
        assertEquals("Scholar",
                loaded.getProfessionName());
        assertEquals(3, loaded.getAttribute(
                Attribute.STRENGTH));
        assertEquals(4, loaded.getAttribute(
                Attribute.AGILITY));
        assertEquals(5, loaded.getAttribute(
                Attribute.LOGIC));
        assertEquals(2, loaded.getAttribute(
                Attribute.PERCEPTION));
        assertEquals(3, loaded.getAttribute(
                Attribute.INSIGHT));
        assertEquals(4, loaded.getAttribute(
                Attribute.EMPATHY));
        // Derived stats are recomputed, never stored.
        assertEquals(7, loaded.getHealth());  // STR+AGL
        assertEquals(9, loaded.getHope());     // LOG+EMP
        assertEquals(5, loaded.getHeart());    // INS+PER
    }

    @Test
    void findAllOrdersByProfessionThenName()
            throws Exception {
        dao.insert(TestExplorers.of(
                "Traveler", "Zara", 3, 3, 3, 3, 3, 3));
        dao.insert(TestExplorers.of(
                "Artist", "Bex", 3, 3, 3, 3, 3, 3));
        dao.insert(TestExplorers.of(
                "Artist", "Ada", 3, 3, 3, 3, 3, 3));

        List<Explorer> all = dao.findAll();
        assertEquals(3, all.size());
        // profession ASC, then name ASC
        assertEquals("Ada", all.get(0).getName());
        assertEquals("Bex", all.get(1).getName());
        assertEquals("Zara", all.get(2).getName());
    }

    @Test
    void outOfRangeAttributeRejectedByCheck() {
        // Bypass the domain model with a raw insert to
        // prove the DB CHECK (BETWEEN 2 AND 6) is live.
        Connection conn = manager.getConnection();
        String sql = "INSERT INTO explorer (name, "
                + "profession, strength, agility, logic, "
                + "perception, insight, empathy) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setString(1, "Overcharged");
            ps.setString(2, "Scholar");
            ps.setInt(3, 9);  // out of range (> 6)
            ps.setInt(4, 3);
            ps.setInt(5, 3);
            ps.setInt(6, 3);
            ps.setInt(7, 3);
            ps.setInt(8, 3);
            assertThrows(java.sql.SQLException.class,
                    ps::executeUpdate);
        } catch (java.sql.SQLException e) {
            fail("prepareStatement should not fail: "
                    + e.getMessage());
        }
    }

    @Test
    void unknownProfessionRejectedOnReload()
            throws Exception {
        // profession has no CHECK, so a bad value inserts
        // fine but must fail when ExplorerDao tries to
        // reconstruct it via ExplorerFactory.
        Connection conn = manager.getConnection();
        String sql = "INSERT INTO explorer (name, "
                + "profession, strength, agility, logic, "
                + "perception, insight, empathy) "
                + "VALUES (?, ?, 3, 3, 3, 3, 3, 3)";
        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {
            ps.setString(1, "Mystery");
            ps.setString(2, "Wizard");  // not a profession
            ps.executeUpdate();
        }
        assertThrows(DatabaseException.class,
                () -> dao.findAll());
    }
}
