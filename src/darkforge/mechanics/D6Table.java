package darkforge.mechanics;

import java.util.*;

public class D6Table<T> implements Rollable {

  private final Map<Integer, T> entries;
  private final Random rng;
  private int lastRollValue;

  public D6Table(Map<Integer, T> entries, Random rng) {
    for (int i = 1; i <= 6; i++)
      if (!entries.containsKey(i))
        throw new IllegalArgumentException("D6 table missing key: " + i);
    this.entries = new HashMap<>(entries);
    this.rng = rng;
    this.lastRollValue = 0;
  }

  public D6Table(Map<Integer, T> entries) {
    this(entries, new Random());
  }

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
      throw new IllegalArgumentException("Invalid D6 value: " + value + " (must be 1-6)");
    return entries.get(value);
  }

  public int getLastRollValue() {
    return lastRollValue;
  }

  public int size() {
    return entries.size();
  }
}
