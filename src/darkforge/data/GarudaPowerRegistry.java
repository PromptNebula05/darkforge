package darkforge.data;

import darkforge.collection.Registry;
import darkforge.crew.BirdType;
import darkforge.crew.GarudaPower;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Type-indexed registry of all 18 Garuda powers
 * from Coriolis: The Great Dark (Ch. 5).
 * Wraps Registry<BirdType, GarudaPower> with
 * convenience methods for basic/advanced lookup.
 *
 * Constructed by GameDataProvider from
 * garuda-powers.json.
 */
public class GarudaPowerRegistry {

    private final Registry<BirdType, GarudaPower>
            registry;
    private final List<GarudaPower> allPowers;

    // =========================================
    // Constructor
    // =========================================

    public GarudaPowerRegistry(
            List<GarudaPower> powers) {
        this.registry = new Registry<>();
        this.allPowers =
                new ArrayList<>(powers);

        for (GarudaPower power : powers) {
            if (power.getNativeTypes().isEmpty()) {
                // Universal: index under all types
                for (BirdType type
                        : BirdType.values()) {
                    registry.register(
                            type, power);
                }
            } else {
                for (BirdType type
                        : power.getNativeTypes()) {
                    registry.register(
                            type, power);
                }
            }
        }
    }

    // =========================================
    // Basic powers
    // =========================================

    public List<GarudaPower> getBasicPowers() {
        return allPowers.stream()
                .filter(GarudaPower::isBasic)
                .collect(Collectors.toList());
    }

    // =========================================
    // Advanced powers
    // =========================================

    public List<GarudaPower>
    getAllAdvancedPowers() {
        return allPowers.stream()
                .filter(p -> !p.isBasic())
                .collect(Collectors.toList());
    }

    /**
     * Advanced powers available to a specific
     * Bird type: type-exclusive + universals.
     */
    public List<GarudaPower>
    getAdvancedPowersFor(BirdType type) {
        return registry.getByKey(type).stream()
                .filter(p -> !p.isBasic())
                .collect(Collectors.toList());
    }

    // =========================================
    // Name lookup
    // =========================================

    public GarudaPower getPowerByName(
            String name) {
        return allPowers.stream()
                .filter(p -> p.getName()
                        .equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    // =========================================
    // Accessors
    // =========================================

    public List<GarudaPower> getAllPowers() {
        return Collections.unmodifiableList(
                allPowers);
    }

    public Registry<BirdType, GarudaPower>
    getRegistry() {
        return registry;
    }

    public int size() {
        return allPowers.size();
    }
}