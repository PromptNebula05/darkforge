package darkforge.display;

public interface Displayable {
    /** Full formatted output suitable for console display. */
    String toFormattedString();

    /** Compact one-line summary (e.g., for list views or crew rosters). */
    String toSummary();
}