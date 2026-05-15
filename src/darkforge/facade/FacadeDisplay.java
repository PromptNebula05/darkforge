package darkforge.facade;

import darkforge.display
        .CharacterSheetFormatter;
import darkforge.model.Explorer;

/**
 * Façade for the darkforge.display package.
 * Wraps CharacterSheetFormatter behind simple
 * formatting methods.
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
     * Format a full character sheet with
     * box-drawing dividers.
     */
    public String formatCharacterSheet(
            Explorer explorer) {
        return formatter.formatCharacterSheet(
                explorer);
    }

    /**
     * Format a single-line compact summary card.
     */
    public String formatSummary(
            Explorer explorer) {
        return formatter.toCompactCard(explorer);
    }

    /**
     * Highlight a search term in text with
     * >>markers<< for console display.
     */
    public String formatWithHighlight(
            String text, String term) {
        return formatter.highlightTerm(
                text, term);
    }

    /**
     * Format a full character sheet for the
     * given Explorer, then highlight the
     * search term with >>markers<<.
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