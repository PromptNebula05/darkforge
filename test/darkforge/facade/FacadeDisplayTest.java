package darkforge.facade;

import darkforge.model.Explorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeDisplay sub-facade.
 * Verifies character sheet formatting and summary
 * generation through the facade layer.
 */
class FacadeDisplayTest {

    private FacadeDisplay facade;
    private Explorer testExplorer;

    @BeforeEach
    void setUp() throws Exception {
        FacadeDarkforge root =
                FacadeDarkforge.getTheInstance();
        facade = root.displayAccess();
        testExplorer = root.creationAccess()
                .createExplorer(
                        "Scholar", "Test Scholar", 1, 1,
                        new int[]{4, 4, 4, 4, 4, 4},
                        new int[]{1, 1, 1, 0},
                        "quirk", "keepsake", "appearance"
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
                highlighted.contains(">>") &&
                        highlighted.contains("<<"),
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
        assertTrue(sheet.contains("STR")
                        || sheet.contains("STRENGTH"),
                "Sheet should contain attribute labels");
    }
}