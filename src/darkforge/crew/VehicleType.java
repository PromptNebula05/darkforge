package darkforge.crew;

/**
 * The six vehicle types from Coriolis: The Great Dark.
 * Rovers (Ch. 6): Rhino, Crocodile, Sphinx.
 * Shuttles (Ch. 11): Grasshopper, Owl, Manta.
 */
public enum VehicleType {

    // Rovers (Ch. 6)
    RHINO("Rhino", Category.ROVER, 3, 2, 9, 6, 4,
            7, 5, 1500, "Wheeled"),
    CROCODILE("Crocodile", Category.ROVER, 2, 2, 11, 7, 3,
            6, 8, 2000, "Tracked"),
    SPHINX("Sphinx", Category.ROVER, 4, 3, 7, 5, 2,
            5, 6, 1000, "Hover"),

    // Shuttles (Ch. 11)
    GRASSHOPPER("Grasshopper", Category.SHUTTLE, 3, 4, 14, 6, 0,
            7, 6, 3000, "Graviton"),
    OWL("Owl", Category.SHUTTLE, 2, 3, 16, 7, 0,
            5, 8, 4000, "Graviton"),
    MANTA("Manta", Category.SHUTTLE, 4, 4, 12, 5, 0,
            3, 5, 2000, "Graviton");

    // =========================================
    // Nested enum
    // =========================================

    public enum Category { ROVER, SHUTTLE }

    // =========================================
    // Fields
    // =========================================

    private final String displayName;
    private final Category category;
    private final int maneuverability;
    private final int speed;
    private final int hull;
    private final int armor;
    private final int blightProtection;
    private final int slots;
    private final int passengers;
    private final int cargo;
    private final String propulsion;

    // =========================================
    // Constructor
    // =========================================

    VehicleType(String displayName, Category category,
                int maneuverability, int speed,
                int hull, int armor, int blightProtection,
                int slots, int passengers, int cargo,
                String propulsion) {
        this.displayName = displayName;
        this.category = category;
        this.maneuverability = maneuverability;
        this.speed = speed;
        this.hull = hull;
        this.armor = armor;
        this.blightProtection = blightProtection;
        this.slots = slots;
        this.passengers = passengers;
        this.cargo = cargo;
        this.propulsion = propulsion;
    }

    // =========================================
    // Getters
    // =========================================

    public String getDisplayName() { return displayName; }
    public Category getCategory() { return category; }
    public int getManeuverability() { return maneuverability; }
    public int getSpeed() { return speed; }
    public int getHull() { return hull; }
    public int getArmor() { return armor; }
    public int getBlightProtection() { return blightProtection; }
    public int getSlots() { return slots; }
    public int getPassengers() { return passengers; }
    public int getCargo() { return cargo; }
    public String getPropulsion() { return propulsion; }

    public boolean isRover() { return category == Category.ROVER; }
    public boolean isShuttle() { return category == Category.SHUTTLE; }

    // =========================================
    // Display
    // =========================================

    // Format: "Rhino (Rover) — Hull 9 | Armor 6 | Mnv +3 | Wheeled"
    @Override
    public String toString() {
        return String.format(
                "%s (%s) — Hull %d | Armor %d | Mnv %+d | %s",
                displayName, category, hull, armor,
                maneuverability, propulsion);
    }
}