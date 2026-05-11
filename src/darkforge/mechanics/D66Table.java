package darkforge.mechanics;

import java.util.*;

public class D66Table<T> implements Rollable {

  private static final Set<Integer> VALID_D66_KEYS = new HashSet<>();
  static {
    for (int tens = 1; tens <= 6; tens++)
      for (int ones = 1; ones <= 6; ones++)
        VALID_D66_KEYS.add(tens * 10 + ones);
  }

  private final Map<Integer, T> entries;
  private final Random rng;
  private int lastRollValue;

  public D66Table(Map<Integer, T> entries, Random rng) {
    for (int key : VALID_D66_KEYS)
      if (!entries.containsKey(key))
        throw new IllegalArgumentException("D66 table missing key: " + key);
    this.entries = new HashMap<>(entries);
    this.rng = rng;
    this.lastRollValue = 0;
  }

  public D66Table(Map<Integer, T> entries) {
    this(entries, new Random());
  }

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
      throw new IllegalArgumentException("Invalid D66 value: " + d66Value + " (both digits must be 1-6)");
    return entries.get(d66Value);
  }

  public int getLastRollValue() {
    return lastRollValue;
  }

  public int size() {
    return entries.size();
  }

  public static Set<Integer> getValidKeys() {
    return Collections.unmodifiableSet(VALID_D66_KEYS);
  }
}
