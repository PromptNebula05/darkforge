package darkforge.model;

/**
 * Abstract base class for all items in the
 * DARKFORGE equipment catalog. Extends
 * GameEntity to integrate with existing
 * Inventory<T extends GameEntity> infrastructure.
 *
 * Every item has:
 * - weight (raw numeric from rulebook WT col)
 * - cost (in rukh for personal items, CP for
 *   vehicle modules — see subclass Javadoc)
 * - category (matches Ch. 6 table grouping)
 * - techLevel (O/G/H from rulebook Tech col)
 * - restricted flag (* in rulebook notation)
 *
 * Serializable is inherited from GameEntity.
 */
public abstract class Item extends GameEntity {

    private static final long
            serialVersionUID = 2L;

    // =========================================
    // Fields
    // =========================================

    private final double weight;
    private final int cost;
    private final String category;
    private final TechLevel techLevel;
    private final boolean restricted;

    // =========================================
    // Constructor
    // =========================================

    protected Item(String name,
                   String description,
                   double weight, int cost,
                   String category,
                   TechLevel techLevel,
                   boolean restricted) {
        super(name, description);
        if (cost < 0) {
            throw new IllegalArgumentException(
                    "Cost cannot be negative,"
                            + " got " + cost);
        }
        this.weight = weight;
        this.cost = cost;
        this.category =
                (category != null)
                        ? category : "General";
        this.techLevel =
                (techLevel != null)
                        ? techLevel
                        : TechLevel.ORDINARY;
        this.restricted = restricted;
    }

    // =========================================
    // Abstract
    // =========================================

    /**
     * Returns a type discriminator string
     * for serialization and catalog filtering.
     * e.g. "weapon", "armor", "module", "cargo"
     */
    public abstract String getItemType();

    // =========================================
    // Getters
    // =========================================

    public double getWeight() {
        return weight;
    }

    public int getCost() {
        return cost;
    }

    public String getCategory() {
        return category;
    }

    public TechLevel getTechLevel() {
        return techLevel;
    }

    public boolean isRestricted() {
        return restricted;
    }

    // =========================================
    // Utility
    // =========================================

    /**
     * Formats the tech level + restricted
     * flag as it appears in rulebook tables.
     * e.g. "O", "G*", "H"
     */
    public String getTechString() {
        return techLevel.getCode()
                + (restricted ? "*" : "");
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        return String.format(
                "%s [%s] — %d cost",
                name, getTechString(), cost);
    }
}