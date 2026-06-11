package darkforge.crew;

import darkforge.collection.*;
import darkforge.model.*;

import java.io.Serial;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Rover or shuttle from Coriolis: The Great Dark (Ch. 6, Ch. 11).
 *
 * Iteration 4 unifies slot accounting across two backing stores:
 *   • legacy {@code upgrades} : Inventory<Equipment>   — written by
 *     {@link #installUpgrade(Equipment)} / {@link #removeUpgrade(Equipment)}
 *     and by CLI/serializer code paths.
 *   • new    {@code modules}  : EquipmentLoadout<VehicleModule> — written
 *     by {@link #equip(VehicleModule)} / {@link #unequip(VehicleModule)}
 *     from the GUI VehicleUpgradePanel and Equippable interface.
 *
 * Both share one slot pool of {@code type.getSlots()}.
 * Cargo lives in its own {@code Inventory<CargoItem>} sized by
 * {@code type.getCargo()} supply points.
 */
public class Vehicle extends GameEntity
        implements InventoryHolder<CargoItem>,
        Equippable<VehicleModule> {

    @Serial
    private static final long serialVersionUID = 11L;

    // =========================================
    // Fields
    // =========================================

    private final VehicleType type;
    private final Inventory<Equipment> upgrades;       // legacy
    private final String paintColor;
    private int currentHull;

    private final Inventory<CargoItem> cargo;
    private final EquipmentLoadout<VehicleModule> modules;

    // =========================================
    // Constructor
    // =========================================

    public Vehicle(String name, VehicleType type, String paintColor) {
        super(name, type.getDisplayName() + " " + type.getCategory());
        this.type = type;
        this.paintColor = paintColor;
        this.currentHull = type.getHull();
        // Legacy inventory keeps its own capacity to preserve historical
        // behavior, but the canonical slot check is getRemainingSlots().
        this.upgrades = new Inventory<>(name, type.getSlots());
        this.cargo = new Inventory<>(name, -1);
        this.modules = new EquipmentLoadout<>(name, type.getSlots());
    }

    // =========================================
    // Static factories
    // =========================================

    public static Vehicle createRhino(String name, String paintColor) {
        return new Vehicle(name, VehicleType.RHINO, paintColor);
    }

    public static Vehicle createCrocodile(String name, String paintColor) {
        return new Vehicle(name, VehicleType.CROCODILE, paintColor);
    }

    public static Vehicle createSphinx(String name, String paintColor) {
        return new Vehicle(name, VehicleType.SPHINX, paintColor);
    }

    public static Vehicle createGrasshopper(String name, String paintColor) {
        return new Vehicle(name, VehicleType.GRASSHOPPER, paintColor);
    }

    public static Vehicle createOwl(String name, String paintColor) {
        return new Vehicle(name, VehicleType.OWL, paintColor);
    }

    public static Vehicle createManta(String name, String paintColor) {
        return new Vehicle(name, VehicleType.MANTA, paintColor);
    }

    // =========================================
    // Upgrades (legacy Inventory<Equipment>)
    // =========================================

    /**
     * Install a legacy Equipment upgrade. Consumes one slot from the
     * shared pool. Throws if the shared pool is full or the legacy
     * inventory cannot accept the item.
     */
    public void installUpgrade(Equipment upgrade) {
        if (getRemainingSlots() <= 0 || !upgrades.add(upgrade)) {
            throw new IllegalStateException(
                    "No upgrade slots remaining on " + name
                            + " (" + type.getSlots() + " max)");
        }
    }

    public boolean removeUpgrade(Equipment upgrade) {
        return upgrades.remove(upgrade);
    }

    public Inventory<Equipment> getUpgrades() {
        return upgrades;
    }

    /**
     * Slots remaining across BOTH backing stores. Legacy Equipment
     * upgrades and new VehicleModule equips share one slot pool sized
     * by {@code type.getSlots()}.
     */
    public int getRemainingSlots() {
        return type.getSlots() - upgrades.size() - modules.getUsedSlots();
    }

    public int getUsedSlots() {
        return upgrades.size() + modules.getUsedSlots();
    }

    // =========================================
    // Hull tracking
    // =========================================

    public int getCurrentHull() { return currentHull; }

    public void takeDamage(int damage) {
        currentHull = Math.max(0, currentHull - damage);
    }

    public void repair(int points) {
        currentHull = Math.min(type.getHull(), currentHull + points);
    }

    public boolean isDestroyed() { return currentHull <= 0; }

    // =========================================
    // Getters
    // =========================================

    public VehicleType getType() { return type; }
    public String getPaintColor() { return paintColor; }
    public int getManeuverability() { return type.getManeuverability(); }
    public int getSpeed() { return type.getSpeed(); }
    public int getMaxHull() { return type.getHull(); }
    public int getArmor() { return type.getArmor(); }
    public int getBlightProtection() { return type.getBlightProtection(); }
    public int getPassengers() { return type.getPassengers(); }
    public int getCargo() { return type.getCargo(); }
    public String getPropulsion() { return type.getPropulsion(); }

    // =========================================
    // InventoryHolder<CargoItem>
    // =========================================

    @Override
    public Inventory<CargoItem> getInventory() {
        return cargo;
    }

    @Override
    public boolean addItem(CargoItem item) {
        int currentSupply = cargo.getAll().stream()
                .mapToInt(CargoItem::getSupplyPoints)
                .sum();
        if (currentSupply + item.getSupplyPoints() > type.getCargo()) {
            return false;
        }
        return cargo.add(item);
    }

    @Override
    public boolean removeItem(CargoItem item) {
        return cargo.remove(item);
    }

    @Override
    public List<CargoItem> searchItems(Predicate<CargoItem> filter) {
        return cargo.getAll().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public List<CargoItem> getAllItems() {
        return cargo.getAll();
    }

    // =========================================
    // Equippable<VehicleModule>
    // =========================================

    @Override
    public boolean equip(VehicleModule module) {
        if (!module.isCompatibleWith(type)) {
            return false;
        }
        // Honor the shared slot pool, not just the loadout's local capacity.
        if (getRemainingSlots() <= 0) {
            return false;
        }
        return modules.equip(module);
    }

    @Override
    public boolean unequip(VehicleModule module) {
        return modules.unequip(module);
    }

    @Override
    public List<VehicleModule> getEquipped() {
        return modules.getEquipped();
    }

    @Override
    public boolean isEquipped(VehicleModule module) {
        return modules.isEquipped(module);
    }

    // =========================================
    // Cargo & module helpers
    // =========================================

    public int getTotalCpCost() {
        return modules.getEquipped().stream()
                .mapToInt(VehicleModule::getCpCost)
                .sum();
    }

    public int getCurrentCargoSupply() {
        return cargo.getAll().stream()
                .mapToInt(CargoItem::getSupplyPoints)
                .sum();
    }

    public int getRemainingCargoCapacity() {
        return type.getCargo() - getCurrentCargoSupply();
    }

    public EquipmentLoadout<VehicleModule> getModules() {
        return modules;
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== %s [%s] ===%n",
                name, type.getDisplayName()));
        sb.append(String.format("Type: %s | Paint: %s%n",
                type.getCategory(), paintColor));
        sb.append(String.format("Propulsion: %s%n",
                type.getPropulsion()));
        sb.append(String.format(
                "Mnv %+d | Spd %d | Hull %d/%d | Armor %d%n",
                type.getManeuverability(), type.getSpeed(),
                currentHull, type.getHull(), type.getArmor()));
        sb.append(String.format(
                "Blight Prot: %d | Passengers: %d | Cargo: %d%n",
                type.getBlightProtection(),
                type.getPassengers(),
                type.getCargo()));
        sb.append(String.format("Slots: %d/%d used%n",
                getUsedSlots(), type.getSlots()));

        // Legacy Equipment upgrades (still the primary source of the
        // "Upgrades" section so existing tests/serializers see it).
        if (!upgrades.isEmpty()) {
            sb.append("Upgrades:\n");
            for (Equipment eq : upgrades) {
                sb.append("  • ").append(eq.display()).append("\n");
            }
        }

        // New typed VehicleModules (GUI / Equippable path).
        if (!modules.getEquipped().isEmpty()) {
            sb.append("Modules:\n");
            for (VehicleModule m : modules.getEquipped()) {
                sb.append("  • ").append(m.display()).append("\n");
            }
        }

        // Cargo manifest.
        if (!cargo.isEmpty()) {
            sb.append(String.format("Cargo (%d/%d supply):%n",
                    getCurrentCargoSupply(), type.getCargo()));
            for (CargoItem c : cargo) {
                sb.append("  • ").append(c.display()).append("\n");
            }
        }

        return sb.toString();
    }
}
