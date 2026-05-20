package darkforge.facade;

import darkforge.display.CharacterSheetFormatter;
import darkforge.model.Explorer;

/**
 * Façade singleton for the darkforge.display
 * package. Provides character sheet formatting,
 * summary generation, and search highlighting.
 */
public class FacadeDisplay {
    private static final FacadeDisplay INSTANCE =
            new FacadeDisplay();

    private final CharacterSheetFormatter
            formatter;

    private FacadeDisplay() {
        this.formatter =
                new CharacterSheetFormatter();
    }

    public static FacadeDisplay getTheInstance() {
        return INSTANCE;
    }

    /**
     * Format a full character sheet for an
     * Explorer.
     */
    public String formatCharacterSheet(
            Explorer explorer) {
        return formatter.formatCharacterSheet(
                explorer);
    }

    /**
     * Format a single-line summary for an
     * Explorer.
     */
    public String formatSummary(
            Explorer explorer) {
        return formatter.toCompactCard(explorer);
    }

    /**
     * Format a character sheet with a search
     * term highlighted using >>markers<<.
     */
    public String formatWithHighlight(
            Explorer explorer, String term) {
        String sheet =
                formatter.formatCharacterSheet(
                        explorer);
        return formatter.highlightTerm(
                sheet, term);
    }
}