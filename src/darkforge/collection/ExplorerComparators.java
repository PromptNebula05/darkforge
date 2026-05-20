package darkforge.collection;

import darkforge.crew.CrewRole;
import darkforge.model.Attribute;
import darkforge.model.Explorer;

import java.util.Comparator;

/**
 * Static factory for composable Explorer comparators.
 * All sorting comparators compose via .thenComparing().
 */
public final class ExplorerComparators {

    private ExplorerComparators() {}

    // =========================================
    // Attribute-based
    // =========================================

    /** Sort by specific attribute (descending). */
    public static Comparator<Explorer>
    byAttribute(Attribute attr) {
        return Comparator.comparingInt(
                        (Explorer e) -> e.getAttribute(attr))
                .reversed();
    }

    // =========================================
    // Derived stats (descending)
    // =========================================

    public static Comparator<Explorer>
    byHealth() {
        return Comparator.comparingInt(
                Explorer::getHealth).reversed();
    }

    public static Comparator<Explorer>
    byHope() {
        return Comparator.comparingInt(
                Explorer::getHope).reversed();
    }

    public static Comparator<Explorer>
    byHeart() {
        return Comparator.comparingInt(
                Explorer::getHeart).reversed();
    }

    // =========================================
    // Identity-based
    // =========================================

    /** Alphabetical by profession class name. */
    public static Comparator<Explorer>
    byProfession() {
        return Comparator.comparing(
                Explorer::getProfessionName);
    }

    /** Alphabetical by explorer name. */
    public static Comparator<Explorer>
    byName() {
        return Comparator.comparing(
                Explorer::getName);
    }

    // =========================================
    // Crew role fitness (descending)
    // =========================================

    /** Sort by fitness for a specific crew role. */
    public static Comparator<Explorer>
    byRoleFitness(CrewRole role) {
        return Comparator.comparingInt(
                        role::getRoleFitness)
                .reversed();
    }
}