package darkforge.facade;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeDisplay sub-facade.
 * Verifies character sheet formatting and summary
 * generation through the facade layer.
 */
class FacadeDisplayTest {

    private FacadeDisplay facade;
    private Explorer testExplorer;

    @BeforeAll
    static void initGameData() {
        GameDataProvider.getTheInstance().initialize();
    }

    @BeforeEach
    void setUp() throws Exception {
        FacadeDarkforge root =
                FacadeDarkforge.getTheInstance();
        facade = root.displayAccess();

        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) {
            attrs.put(a, 4);
        }
        Origin origin = GameDataProvider
                .getTheInstance().getOrigins().get(0);

        testExplorer = root.creationAccess()
                .createExplorer(
                        "Scholar", origin, 0,
                        attrs, new int[]{1, 1, 1, 0},
                        "quirk", "keepsake", "appearance",
                        "Test Scholar"
                );
    }

    @Test
    void shouldFormatCharacterSheet() {
        String sheet =
                facade.formatCharacterSheet(
                        testExplorer);
        assertNotNull(sheet);
        assertTrue(sheet.contains("Test Scholar"));
        assertTrue(sheet.contains("Scholar"));
    }

    @Test
    void shouldFormatSummary() {
        String summary =
                facade.formatSummary(testExplorer);
        assertNotNull(summary);
        assertTrue(summary.contains("Test Scholar"));
    }

    @Test
    void shouldFormatWithSearchHighlight() {
        String highlighted =
                facade.formatWithHighlight(
                        testExplorer, "Scholar");
        assertTrue(
                highlighted.contains(">>")
                        && highlighted.contains("<<"),
                "Should contain search-highlight markers");
    }

    @Test
    void shouldRejectNullExplorer() {
        assertThrows(Exception.class, () ->
                facade.formatCharacterSheet(null));
    }

    @Test
    void shouldReturnFormattedSheetWithAttributes() {
        String sheet =
                facade.formatCharacterSheet(
                        testExplorer);
        assertTrue(sheet.contains(
                        Attribute.STRENGTH.getDisplayName()),
                "Sheet should contain attribute labels");
    }
}