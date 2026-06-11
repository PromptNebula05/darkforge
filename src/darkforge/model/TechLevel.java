package darkforge.model;

/**
 * Technology level for items in Coriolis:
 * The Great Dark.
 * O = Ordinary (widely available),
 * G = Guild (faction-restricted),
 * H = Heirloom (ancient, unique).
 * Items may also be restricted (*) —
 * available only through faction contacts
 * or black markets.
 */
public enum TechLevel {

    ORDINARY("O", "Ordinary"),
    GUILD("G", "Guild"),
    HEIRLOOM("H", "Heirloom");

    // =========================================
    // Fields
    // =========================================

    private final String code;
    private final String displayName;

    // =========================================
    // Constructor
    // =========================================

    TechLevel(String code,
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

    /**
     * Parse a tech level from its code string.
     * Accepts "O", "G", "H" (case-insensitive)
     * and also codes with sub-guild letters
     * like "G(E)", "G(N)", etc.
     */
    public static TechLevel fromCode(
            String code) {
        if (code == null) {
            throw new IllegalArgumentException(
                    "Tech level code is null");
        }
        String upper = code.trim()
                .toUpperCase();
        if (upper.startsWith("G")) {
            return GUILD;
        }
        for (TechLevel t : values()) {
            if (t.code.equalsIgnoreCase(
                    upper)) {
                return t;
            }
        }
        throw new IllegalArgumentException(
                "Unknown tech level: " + code);
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String toString() {
        return code;
    }
}