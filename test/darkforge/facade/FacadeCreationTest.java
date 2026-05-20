package darkforge.facade;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeCreation sub-facade.
 * Verifies explorer creation, name generation,
 * and search delegation through the facade layer.
 */
class FacadeCreationTest {

    private final FacadeCreation facade =
            FacadeDarkforge.getTheInstance()
                    .creationAccess();

    @BeforeAll
    static void initGameData() {
        GameDataProvider.getTheInstance().initialize();
    }

    private EnumMap<Attribute, Integer> safeAttrs() {
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) {
            attrs.put(a, 4);
        }
        return attrs;
    }

    private Origin defaultOrigin() {
        return GameDataProvider.getTheInstance()
                .getOrigins().get(0);
    }

    @Test
    void shouldCreateExplorerViaFacade() throws Exception {
        Explorer explorer = facade.createExplorer(
                "Scholar", defaultOrigin(), 0,
                safeAttrs(), new int[]{1, 1, 1, 0},
                "quirk", "keepsake", "appearance",
                "Test Scholar"
        );
        assertNotNull(explorer);
        assertEquals("Test Scholar", explorer.getName());
    }

    @Test
    void shouldRejectUnknownProfessionViaFacade() {
        assertThrows(Exception.class, () ->
                facade.createExplorer(
                        "Wizard", defaultOrigin(), 0,
                        safeAttrs(), new int[]{1, 1, 1, 0},
                        "q", "k", "a", "Test"
                ));
    }

    @Test
    void shouldGenerateNameViaFacade() {
        String name =
                facade.generateName("Scholar");
        assertNotNull(name);
        assertFalse(name.isBlank());
    }

    @Test
    void shouldSearchByNameViaFacade() throws Exception {
        Explorer explorer = facade.createExplorer(
                "Scholar", defaultOrigin(), 0,
                safeAttrs(), new int[]{1, 1, 1, 0},
                "quirk", "keepsake", "appearance",
                "Cantara Loutreides"
        );
        var results = facade.searchByName(
                List.of(explorer), "Cantara");
        assertFalse(results.isEmpty(),
                "Should find explorer by partial name");
    }

    @Test
    void shouldSuggestMultipleNames() {
        List<String> names =
                facade.suggestNames("Scholar", 5);
        assertNotNull(names);
        assertEquals(5, names.size());
        for (String name : names) {
            assertFalse(name.isBlank());
        }
    }

    @Test
    void shouldSuggestUniqueNames() {
        List<String> names =
                facade.suggestNames("Enforcer", 10);
        long uniqueCount = names.stream()
                .distinct().count();
        assertTrue(uniqueCount >= 5,
                "Most suggested names should be unique");
    }

    @Test
    void shouldSearchByNameWithEmptyList() {
        var results = facade.searchByName(
                List.of(), "Nonexistent");
        assertTrue(results.isEmpty(),
                "Empty list should yield no results");
    }
}