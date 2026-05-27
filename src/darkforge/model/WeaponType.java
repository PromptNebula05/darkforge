package darkforge.model;

/**
 * Weapon categories from Coriolis.
 * Maps to the weapon table groupings in
 * the rulebook.
 */
public enum WeaponType {

    RANGED_RIFLE("Ranged (Rifle)"),
    RANGED_PISTOL("Ranged (Pistol)"),
    RANGED_SPECIAL("Ranged (Special)"),
    CLOSE_COMBAT("Close Combat"),
    EXPLOSIVE("Explosive"),
    HEIRLOOM("Heirloom"),
    ROVER_WEAPON("Rover Weapon"),
    SHUTTLE_WEAPON("Shuttle Weapon");

    // =========================================
    // Fields
    // =========================================

    private final String displayName;

    // =========================================
    // Constructor
    // =========================================

    WeaponType(String displayName) {
        this.displayName = displayName;
    }

    // =========================================
    // Getters
    // =========================================

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPersonal() {
        return this != ROVER_WEAPON
                && this != SHUTTLE_WEAPON;
    }

    public boolean isVehicleMounted() {
        return this == ROVER_WEAPON
                || this == SHUTTLE_WEAPON;
    }

    @Override
    public String toString() {
        return displayName;
    }
}