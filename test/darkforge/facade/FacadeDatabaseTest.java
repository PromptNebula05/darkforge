package darkforge.facade;

import static org.junit.jupiter.api.Assertions.*;

import darkforge.crew.Crew;
import darkforge.crew.CrewRole;
import darkforge.creation.ExplorerFactory;
import darkforge.database.DatabaseManager;
import darkforge.exception.DatabaseException;
import darkforge.exception.InvalidProfessionException;
import darkforge.model.Attribute;
import darkforge.model.Explorer;

import java.util.EnumMap;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for the FacadeDatabase subfacade. Uses
 * the package-private FacadeDatabase(DatabaseManager)
 * constructor with an in-memory database so the real
 * eager singleton / on-disk darkforge.db is never touched.
 *
 * Covers transactional saveCrew, ExplorerFactory
 * reconstruction on loadCrew, ON DELETE CASCADE, and
 * rollback when a member has no assigned role.
 */
class FacadeDatabaseTest {

    private static final ExplorerFactory FACTORY =
            new ExplorerFactory();

    private FacadeDatabase database;

    @BeforeEach
    void setUp() {
        database = new FacadeDatabase(
                new DatabaseManager("jdbc:sqlite::memory:"));
    }

    @AfterEach
    void tearDown() {
        database.shutdown();
    }

    @Test
    void saveThenLoadReconstructsMembersAndRoles()
            throws Exception {
        Crew crew = new Crew("Wayfarers");
        Explorer vada = explorer("Scholar", "Vada");
        Explorer bex = explorer("Roughneck", "Bex");
        crew.addMember(vada);
        crew.addMember(bex);
        crew.assignRole(CrewRole.DELVER, vada);
        crew.assignRole(CrewRole.GUARD, bex);

        long crewId = database.saveCrew(crew);
        assertTrue(crewId > 0);

        Crew loaded = database.loadCrew("Wayfarers");
        assertNotNull(loaded);
        assertEquals(2, loaded.getMembers().size());
        assertEquals(
                Set.of(CrewRole.DELVER, CrewRole.GUARD),
                loaded.getRoleAssignments().keySet());
    }

    @Test
    void deleteCrewCascadesMemberships()
            throws Exception {
        Crew crew = new Crew("Wayfarers");
        Explorer vada = explorer("Scholar", "Vada");
        crew.addMember(vada);
        crew.assignRole(CrewRole.DELVER, vada);
        database.saveCrew(crew);

        assertTrue(database.deleteCrew("Wayfarers"));
        assertNull(database.loadCrew("Wayfarers"));
        // Only the crew + its crew_membership rows are
        // cascade-removed; the explorer row survives.
        assertEquals(1,
                database.listExplorers().size());
    }

    @Test
    void saveCrewWithoutRoleRollsBack() throws Exception {
        Crew crew = new Crew("Roleless");
        Explorer vada = explorer("Scholar", "Vada");
        crew.addMember(vada);
        // Intentionally do NOT assign a role.

        assertThrows(DatabaseException.class,
                () -> database.saveCrew(crew));

        // The whole transaction rolled back: neither the
        // crew nor the explorer was persisted.
        assertTrue(database.listCrews().isEmpty());
        assertTrue(database.listExplorers().isEmpty());
    }

    private Explorer explorer(
            String profession, String name)
            throws InvalidProfessionException {
        Explorer e = FACTORY.createProfessionInstance(
                profession, name);
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) {
            attrs.put(a, 3);
        }
        e.setAttributes(attrs);
        return e;
    }
}
