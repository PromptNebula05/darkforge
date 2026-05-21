package darkforge.crew;

import darkforge.model.GameEntity;
import java.util.Collections;
import java.util.Set;

/**
 * A Garuda power from Coriolis: The Great Dark (Ch. 5).
 * Powers are either basic (available to all Birds) or
 * advanced (type-specific or universal).
 */
public class GarudaPower extends GameEntity {

    private final String effect;
    private final boolean isBasic;
    private final Set<BirdType> nativeTypes;
    private final int energyCost;

    // =========================================
    // Constructor
    // =========================================

    public GarudaPower(String name,
                       String description, String effect,
                       boolean isBasic,
                       Set<BirdType> nativeTypes,
                       int energyCost) {
        super(name, description);
        this.effect = effect;
        this.isBasic = isBasic;
        this.nativeTypes = nativeTypes.isEmpty()
                ? Collections.emptySet()
                : Collections.unmodifiableSet(
                nativeTypes);
        this.energyCost = energyCost;
    }

    // =========================================
    // Eligibility
    // =========================================

    /**
     * True if nativeTypes is empty (universal)
     * or contains the given type.
     */
    public boolean isNativeFor(BirdType type) {
        return nativeTypes.isEmpty()
                || nativeTypes.contains(type);
    }

    /**
     * Training cost: 5 CP if native to the
     * Bird's type, 10 CP if not.
     */
    public int getTrainingCost(
            BirdType birdType) {
        return isNativeFor(birdType) ? 5 : 10;
    }

    // =========================================
    // Getters
    // =========================================

    public String getEffect() {
        return effect;
    }

    public boolean isBasic() {
        return isBasic;
    }

    public Set<BirdType> getNativeTypes() {
        return nativeTypes;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        String typeStr = nativeTypes.isEmpty()
                ? "Universal"
                : nativeTypes.toString();
        return String.format(
                "%s [%s] \u2014 %s (Energy: %d)",
                getName(), typeStr, effect,
                energyCost);
    }
}