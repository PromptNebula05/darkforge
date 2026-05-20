package darkforge.mechanics;

import java.util.Collection;
import java.util.Random;

/**
 * Generic interface for selecting one item of type T
 * from a fixed set. Unifies D6Table, D66Table, and
 * WeightedSelector under a common contract.
 */
public interface Selectable<T> {

    // Deterministic lookup by roll value.
    T select(int value);

    // Random selection using internal Random.
    T selectRandom();

    // Random selection with provided RNG (deterministic for testing).
    T selectRandom(Random rng);

    // Number of selectable entries.
    int size();

    // All selectable values.
    Collection<T> allValues();
}