package darkforge.model;

/**
 * Weapon grip from Coriolis.
 * Determines whether a weapon is one-handed
 * or two-handed.
 */
public enum Grip {

    ONE_HANDED("1H", "One-handed"),
    TWO_HANDED("2H", "Two-handed");

    // =========================================
    // Fields
    // =========================================

    private final String code;
    private final String displayName;

    // =========================================
    // Constructor
    // =========================================

    Grip(String code,
         String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    // =========================================
    // Getters
    // =========================================

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    // =========================================
    // Parsing
    // =========================================

    public static Grip fromCode(String code) {
        if ("1H".equalsIgnoreCase(code)
                || "1".equals(code)) {
            return ONE_HANDED;
        }
        if ("2H".equalsIgnoreCase(code)
                || "2".equals(code)) {
            return TWO_HANDED;
        }
        throw new IllegalArgumentException(
                "Unknown grip code: " + code);
    }

    @Override
    public String toString() {
        return code;
    }
}