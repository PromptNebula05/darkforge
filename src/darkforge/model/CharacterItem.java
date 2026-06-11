package darkforge.model;

/**
 * Base class for items that characters
 * (Explorers) can carry. Adds weight class,
 * gear bonus (dice added to rolls), and
 * starting-gear flag.
 *
 * General equipment items (tools, kits,
 * climbing gear, scanners, etc.) are direct
 * CharacterItem instances. Weapons and armor
 * are further specialized subclasses.
 *
 * Cost is in rukh (Coriolis currency).
 */
public class CharacterItem extends Item {

    private static final long
            serialVersionUID = 3L;

    // =========================================
    // Fields
    // =========================================

    private final EquipmentWeight weightClass;
    private final int gearBonus;
    private final boolean startingGear;

    // =========================================
    // Constructors
    // =========================================

    public CharacterItem(String name,
                         String description,
                         double weight, int cost,
                         String category,
                         TechLevel techLevel,
                         boolean restricted,
                         EquipmentWeight weightClass,
                         int gearBonus,
                         boolean startingGear) {
        super(name, description, weight,
                cost, category, techLevel,
                restricted);
        if (gearBonus < 0) {
            throw new IllegalArgumentException(
                    "Gear bonus cannot be"
                            + " negative, got "
                            + gearBonus);
        }
        this.weightClass =
                (weightClass != null)
                        ? weightClass
                        : EquipmentWeight.fromWeight(
                        weight);
        this.gearBonus = gearBonus;
        this.startingGear = startingGear;
    }

    // Convenience: non-starting, auto weight
    public CharacterItem(String name,
                         String description,
                         double weight, int cost,
                         String category,
                         TechLevel techLevel,
                         boolean restricted,
                         int gearBonus) {
        this(name, description, weight,
                cost, category, techLevel,
                restricted,
                EquipmentWeight.fromWeight(
                        weight),
                gearBonus, false);
    }

    // =========================================
    // Getters
    // =========================================

    public EquipmentWeight getWeightClass() {
        return weightClass;
    }

    public int getGearBonus() {
        return gearBonus;
    }

    public boolean isStartingGear() {
        return startingGear;
    }

    // =========================================
    // Item type
    // =========================================

    @Override
    public String getItemType() {
        return "equipment";
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        String bonus =
                (gearBonus > 0)
                        ? " (Gear +" + gearBonus
                          + ")"
                        : "";
        String tech =
                (getTechLevel()
                        != TechLevel.ORDINARY)
                        ? " [" + getTechString()
                          + "]"
                        : "";
        return String.format(
                "%s [%s]%s%s — %d rukh",
                name,
                weightClass.getDisplayName(),
                bonus, tech, getCost());
    }
}