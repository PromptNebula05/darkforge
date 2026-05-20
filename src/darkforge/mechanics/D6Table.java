package darkforge.mechanics;

import java.util.*;

/**
 * A 6-entry lookup table indexed by D6 roll (1–6).
 * Now implements both Rollable and Selectable<T>.
 * Existing Rollable methods are unchanged.
 */
public class D6Table<T>
        implements Rollable, Selectable<T> {

  private final Map<Integer, T> entries;
  private final Random rng;
  private int lastRollValue;

  // =========================================
  // Constructors
  // =========================================

  public D6Table(Map<Integer, T> entries,
                 Random rng) {
    for (int i = 1; i <= 6; i++)
      if (!entries.containsKey(i))
        throw new IllegalArgumentException(
                "D6 table missing key: " + i);
    this.entries = new HashMap<>(entries);
    this.rng = rng;
    this.lastRollValue = 0;
  }

  public D6Table(Map<Integer, T> entries) {
    this(entries, new Random());
  }

  // =========================================
  // Rollable (unchanged)
  // =========================================

  @Override
  public int roll() {
    lastRollValue = rng.nextInt(6) + 1;
    return lastRollValue;
  }

  @Override
  public int[] rollMultiple(int count) {
    int[] results = new int[count];
    for (int i = 0; i < count; i++)
      results[i] = roll();
    return results;
  }

  public T getResult(int value) {
    if (value < 1 || value > 6)
      throw new IllegalArgumentException(
              "Invalid D6 value: " + value
                      + " (must be 1-6)");
    return entries.get(value);
  }

  public int getLastRollValue() {
    return lastRollValue;
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
    int value = externalRng.nextInt(6) + 1;
    return getResult(value);
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