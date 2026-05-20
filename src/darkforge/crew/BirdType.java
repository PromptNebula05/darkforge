package darkforge.crew;

/**
 * The three Garuda Bird types from Coriolis: The Great Dark (Ch. 5).
 * Each type trades health for energy and has a unique signature power.
 */
public enum BirdType {

    WARD("Ward", 5, 2, "Raptor's Call"),
    GUIDE("Guide", 4, 3, "Farsight"),
    SPECTER("Specter", 3, 4, "Enshroud");

    // =========================================
    // Fields
    // =========================================

    private final String displayName;
    private final int startingHealth;
    private final int startingEnergy;
    private final String specialPower;

    // =========================================
    // Constructor
    // =========================================

    BirdType(String displayName,
             int startingHealth,
             int startingEnergy,
             String specialPower) {
        this.displayName = displayName;
        this.startingHealth = startingHealth;
        this.startingEnergy = startingEnergy;
        this.specialPower = specialPower;
    }

    // =========================================
    // Getters
    // =========================================

    public String getDisplayName() {
        return displayName;
    }

    public int getStartingHealth() {
        return startingHealth;
    }

    public int getStartingEnergy() {
        return startingEnergy;
    }

    public String getSpecialPower() {
        return specialPower;
    }

    // =========================================
    // Display
    // =========================================

    // Format: "Ward — HP 5 / EP 2 — Raptor's Call"
    @Override
    public String toString() {
        return String.format(
                "%s — HP %d / EP %d — %s",
                displayName, startingHealth,
                startingEnergy, specialPower);
    }
}