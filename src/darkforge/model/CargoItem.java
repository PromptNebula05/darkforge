package darkforge.model;

/**
 * Bulk cargo carried in vehicle holds.
 * Measured in supply points — the Coriolis
 * unit for vehicle cargo capacity
 * (e.g. Rhino = 1500 supply,
 *  Grasshopper = 3000 supply).
 */
public class CargoItem extends Item {

    private static final long
            serialVersionUID = 7L;

    // =========================================
    // Fields
    // =========================================

    private final int supplyPoints;
    private final String cargoType;

    // =========================================
    // Constructor
    // =========================================

    public CargoItem(String name,
                     String description,
                     int supplyPoints,
                     String cargoType,
                     int cost,
                     TechLevel techLevel) {
        super(name, description,
                supplyPoints, cost,
                "Cargo", techLevel, false);
        if (supplyPoints < 0) {
            throw new IllegalArgumentException(
                    "Supply points cannot be"
                            + " negative");
        }
        this.supplyPoints = supplyPoints;
        this.cargoType =
                (cargoType != null)
                        ? cargoType : "general";
    }

    // =========================================
    // Getters
    // =========================================

    public int getSupplyPoints() {
        return supplyPoints;
    }

    public String getCargoType() {
        return cargoType;
    }

    // =========================================
    // Item type
    // =========================================

    @Override
    public String getItemType() {
        return "cargo";
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        return String.format(
                "📦 %s [%d supply | %s]"
                        + " — %d rukh",
                name, supplyPoints,
                cargoType, getCost());
    }
}