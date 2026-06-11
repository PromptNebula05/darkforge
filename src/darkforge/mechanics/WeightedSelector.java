package darkforge.mechanics;

import java.util.*;

/**
 * Probability-weighted random selection implementing Selectable<T>.
 * Third Selectable implementation alongside D6Table and D66Table:
 * same interface, fundamentally different selection mechanic
 * (probability distribution vs dice lookup).
 *
 * Does NOT implement Rollable — intentional.
 * Rollable generates a dice value; WeightedSelector samples
 * from a probability distribution. They share Selectable<T>
 * but differ in how the selection index is produced.
 */
public class WeightedSelector<T>
        implements Selectable<T> {

    private final Map<T, Double> weights;
    private final List<T> items;
    private final double totalWeight;
    private final Random defaultRng;

    // =========================================
    // Constructor
    // =========================================

    public WeightedSelector(
            Map<T, Double> weightedItems) {
        this.weights = new LinkedHashMap<>(
                weightedItems);
        this.items = new ArrayList<>(
                weights.keySet());
        this.totalWeight = weights.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        this.defaultRng = new Random();
    }

    // =========================================
    // Selectable<T>
    // =========================================

    @Override
    public T select(int value) {
        double target = (value / 100.0)
                * totalWeight;
        return selectByWeight(target);
    }

    @Override
    public T selectRandom() {
        return selectRandom(defaultRng);
    }

    @Override
    public T selectRandom(Random rng) {
        double target = rng.nextDouble()
                * totalWeight;
        return selectByWeight(target);
    }

    @Override
    public int size() { return items.size(); }

    @Override
    public Collection<T> allValues() {
        return Collections
                .unmodifiableCollection(
                        weights.keySet());
    }

    // =========================================
    // Internal
    // =========================================

    private T selectByWeight(double target) {
        double cumulative = 0.0;
        for (Map.Entry<T, Double> entry
                : weights.entrySet()) {
            cumulative += entry.getValue();
            if (target <= cumulative)
                return entry.getKey();
        }
        return items.get(items.size() - 1);
    }
}