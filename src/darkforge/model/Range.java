package darkforge.model;

/**
 * Engagement ranges from Coriolis.
 * Weapons specify one or more ranges they
 * can be used at (e.g. Short–Medium).
 */
public enum Range {

    ENGAGED("Engaged", 0),
    SHORT("Short", 1),
    MEDIUM("Medium", 2),
    LONG("Long", 3),
    EXTREME("Extreme", 4);

    // =========================================
    // Fields
    // =========================================

    private final String displayName;
    private final int order;

    // =========================================
    // Constructor
    // =========================================

    Range(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    // =========================================
    // Getters
    // =========================================

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    // =========================================
    // Utility
    // =========================================

    /**
     * Format a range span string.
     * E.g. formatSpan(SHORT, LONG) →
     * "Short–Long"
     */
    public static String formatSpan(
            Range min, Range max) {
        if (min == max) {
            return min.displayName;
        }
        return min.displayName + "–"
                + max.displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}