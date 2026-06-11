package darkforge.model;

import darkforge.crew.VehicleType;
import java.util.*;

/**
 * An installable vehicle upgrade from Coriolis.
 * Rover upgrades: Ch. 6.
 * Shuttle upgrades: Ch. 11.
 *
 * IMPORTANT: Vehicle upgrade cost is in
 * Crew Points (CP), NOT rukh. This is a
 * fundamentally different currency from
 * personal equipment.
 *
 * Slot cost: 0 = add-on (doesn't consume
 * a vehicle slot), 1+ = consumes that many
 * vehicle upgrade slots.
 */
public class VehicleModule extends Item {

    private static final long
            serialVersionUID = 6L;

    // =========================================
    // Fields
    // =========================================

    private final int slotCost;
    private final int cpCost;
    private final String moduleType;
    private final String effect;
    private final Set<VehicleType>
            compatibleVehicles;
    private final boolean shuttleUpgrade;

    // =========================================
    // Constructor
    // =========================================

    public VehicleModule(String name,
                         String description,
                         int slotCost, int cpCost,
                         String moduleType, String effect,
                         TechLevel techLevel,
                         boolean restricted,
                         Set<VehicleType>
                                 compatibleVehicles,
                         boolean shuttleUpgrade) {
        super(name, description,
                slotCost, cpCost,
                shuttleUpgrade
                        ? "Shuttle Upgrade"
                        : "Rover Upgrade",
                techLevel, restricted);
        this.slotCost = slotCost;
        this.cpCost = cpCost;
        this.moduleType =
                (moduleType != null)
                        ? moduleType : "utility";
        this.effect =
                (effect != null)
                        ? effect : "";
        this.compatibleVehicles =
                (compatibleVehicles != null)
                        ? EnumSet.copyOf(
                        compatibleVehicles)
                        : EnumSet.noneOf(
                        VehicleType.class);
        this.shuttleUpgrade =
                shuttleUpgrade;
    }

    // =========================================
    // Getters
    // =========================================

    public int getSlotCost() {
        return slotCost;
    }

    public int getCpCost() {
        return cpCost;
    }

    public String getModuleType() {
        return moduleType;
    }

    public String getEffect() {
        return effect;
    }

    public Set<VehicleType>
    getCompatibleVehicles() {
        return Collections
                .unmodifiableSet(
                        compatibleVehicles);
    }

    public boolean isShuttleUpgrade() {
        return shuttleUpgrade;
    }

    // =========================================
    // Utility
    // =========================================

    public boolean isAddon() {
        return slotCost == 0;
    }

    public boolean isCompatibleWith(
            VehicleType type) {
        return compatibleVehicles.isEmpty()
                || compatibleVehicles
                .contains(type);
    }

    // =========================================
    // Item type
    // =========================================

    @Override
    public String getItemType() {
        return "module";
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        String slot = isAddon()
                ? "Add-on"
                : "Slot " + slotCost;
        return String.format(
                "⚙ %s [%s | %d CP] %s"
                        + " | %s — %s",
                name, slot, cpCost,
                effect, getTechString(),
                getCategory());
    }
}