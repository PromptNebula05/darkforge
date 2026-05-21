package darkforge.crew;

import darkforge.collection.Inventory;
import darkforge.model.Equipment;
import darkforge.model.GameEntity;

/**
 * Rover or shuttle from Coriolis:
 * The Great Dark (Ch. 6, Ch. 11).
 * Stat block derived from VehicleType;
 * upgrades managed via Inventory<Equipment>
 * with slot-based capacity.
 */
public class Vehicle extends GameEntity {

    private final VehicleType type;
    private final Inventory<Equipment> upgrades;
    private final String paintColor;
    private int currentHull;

    // =========================================
    // Constructor
    // =========================================

    public Vehicle(String name,
                   VehicleType type,
                   String paintColor) {
        super(name, type.getDisplayName()
                + " " + type.getCategory());
        this.type = type;
        this.paintColor = paintColor;
        this.currentHull = type.getHull();
        this.upgrades = new Inventory<>(
                name, type.getSlots());
    }

    // =========================================
    // Static factories
    // =========================================

    public static Vehicle createRhino(
            String name, String paintColor) {
        return new Vehicle(name,
                VehicleType.RHINO, paintColor);
    }

    public static Vehicle createCrocodile(
            String name, String paintColor) {
        return new Vehicle(name,
                VehicleType.CROCODILE, paintColor);
    }

    public static Vehicle createSphinx(
            String name, String paintColor) {
        return new Vehicle(name,
                VehicleType.SPHINX, paintColor);
    }

    public static Vehicle createGrasshopper(
            String name, String paintColor) {
        return new Vehicle(name,
                VehicleType.GRASSHOPPER, paintColor);
    }

    public static Vehicle createOwl(
            String name, String paintColor) {
        return new Vehicle(name,
                VehicleType.OWL, paintColor);
    }

    public static Vehicle createManta(
            String name, String paintColor) {
        return new Vehicle(name,
                VehicleType.MANTA, paintColor);
    }

    // =========================================
    // Upgrades (Inventory<Equipment>)
    // =========================================

    public void installUpgrade(
            Equipment upgrade) {
        if (!upgrades.add(upgrade)) {
            throw new IllegalStateException(
                    "No upgrade slots remaining"
                            + " on " + name
                            + " (" + type.getSlots()
                            + " max)");
        }
    }

    public boolean removeUpgrade(
            Equipment upgrade) {
        return upgrades.remove(upgrade);
    }

    public Inventory<Equipment> getUpgrades() {
        return upgrades;
    }

    public int getRemainingSlots() {
        return upgrades.remainingCapacity();
    }

    // =========================================
    // Hull tracking
    // =========================================

    public int getCurrentHull() {
        return currentHull;
    }

    public void takeDamage(int damage) {
        currentHull = Math.max(0,
                currentHull - damage);
    }

    public void repair(int points) {
        currentHull = Math.min(
                type.getHull(),
                currentHull + points);
    }

    public boolean isDestroyed() {
        return currentHull <= 0;
    }

    // =========================================
    // Getters
    // =========================================

    public VehicleType getType() {
        return type;
    }

    public String getPaintColor() {
        return paintColor;
    }

    public int getManeuverability() {
        return type.getManeuverability();
    }

    public int getSpeed() {
        return type.getSpeed();
    }

    public int getMaxHull() {
        return type.getHull();
    }

    public int getArmor() {
        return type.getArmor();
    }

    public int getBlightProtection() {
        return type.getBlightProtection();
    }

    public int getPassengers() {
        return type.getPassengers();
    }

    public int getCargo() {
        return type.getCargo();
    }

    public String getPropulsion() {
        return type.getPropulsion();
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                "=== %s [%s] ===%n",
                name, type.getDisplayName()));
        sb.append(String.format(
                "Type: %s | Paint: %s%n",
                type.getCategory(), paintColor));
        sb.append(String.format(
                "Propulsion: %s%n",
                type.getPropulsion()));
        sb.append(String.format(
                "Mnv %+d | Spd %d | Hull %d/%d"
                        + " | Armor %d%n",
                type.getManeuverability(),
                type.getSpeed(),
                currentHull, type.getHull(),
                type.getArmor()));
        sb.append(String.format(
                "Blight Prot: %d | Passengers:"
                        + " %d | Cargo: %d%n",
                type.getBlightProtection(),
                type.getPassengers(),
                type.getCargo()));
        sb.append(String.format(
                "Slots: %d/%d used%n",
                upgrades.size(),
                type.getSlots()));

        if (!upgrades.isEmpty()) {
            sb.append("Upgrades:\n");
            for (Equipment eq : upgrades) {
                sb.append("  \u2022 ")
                        .append(eq.display())
                        .append("\n");
            }
        }

        return sb.toString();
    }
}