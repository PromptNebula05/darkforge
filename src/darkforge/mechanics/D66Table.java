package darkforge.mechanics;

import java.util.*;

/**
 * A 36-entry lookup table indexed by D66 roll (11–66).
 * Now implements both Rollable and Selectable<T>.
 * Existing Rollable methods are unchanged.
 */
public class D66Table<T>
        implements Rollable, Selectable<T> {

  private static final Set<Integer>
          VALID_D66_KEYS = new HashSet<>();
  static {
    for (int tens = 1; tens <= 6; tens++)
      for (int ones = 1; ones <= 6; ones++)
        VALID_D66_KEYS.add(
                tens * 10 + ones);
  }

  private final Map<Integer, T> entries;
  private final Random rng;
  private int lastRollValue;

  // =========================================
  // Constructors
  // =========================================

  public D66Table(Map<Integer, T> entries,
                  Random rng) {
    for (int key : VALID_D66_KEYS)
      if (!entries.containsKey(key))
        throw new IllegalArgumentException(
                "D66 table missing key: "
                        + key);
    this.entries = new HashMap<>(entries);
    this.rng = rng;
    this.lastRollValue = 0;
  }

  public D66Table(Map<Integer, T> entries) {
    this(entries, new Random());
  }

  // =========================================
  // Rollable (unchanged)
  // =========================================

  @Override
  public int roll() {
    int tens = rng.nextInt(6) + 1;
    int ones = rng.nextInt(6) + 1;
    lastRollValue = tens * 10 + ones;
    return lastRollValue;
  }

  @Override
  public int[] rollMultiple(int count) {
    int[] results = new int[count];
    for (int i = 0; i < count; i++)
      results[i] = roll();
    return results;
  }

  public T getResult(int d66Value) {
    if (!VALID_D66_KEYS.contains(d66Value))
      throw new IllegalArgumentException(
              "Invalid D66 value: " + d66Value
                      + " (both digits must be 1-6)");
    return entries.get(d66Value);
  }

  public int getLastRollValue() {
    return lastRollValue;
  }

  public static Set<Integer> getValidKeys() {
    return Collections.unmodifiableSet(
            VALID_D66_KEYS);
  }

  // =========================================
  // Selectable<T>
  // =========================================

  @Override
  public T select(int value) {
    return getResult(value);
  }

  @Override
  public T selectRandom() {
    roll();
    return getResult(lastRollValue);
  }

  @Override
  public T selectRandom(Random externalRng) {
    int tens = externalRng.nextInt(6) + 1;
    int ones = externalRng.nextInt(6) + 1;
    return getResult(tens * 10 + ones);
  }

  @Override
  public int size() {
    return entries.size();
  }

  @Override
  public Collection<T> allValues() {
    return Collections.unmodifiableCollection(
            entries.values());
  }
}