package darkforge.mechanics;

import java.util.Random;

public class DicePool implements Rollable {

  private final int baseDice;
  private final int gearDice;
  private final Random rng;
  private int[] lastBaseResults;
  private int[] lastGearResults;
  private boolean pushed;
  private boolean hasRolled;
  private int pushHopeCost;

  public DicePool(int baseDice, int gearDice, Random rng) {
    if (baseDice < 0)
      throw new IllegalArgumentException("Base dice cannot be negative, got " + baseDice);
    if (gearDice < 0)
      throw new IllegalArgumentException("Gear dice cannot be negative, got " + gearDice);
    this.baseDice = baseDice;
    this.gearDice = gearDice;
    this.rng = rng;
    this.lastBaseResults = new int[0];
    this.lastGearResults = new int[0];
    this.pushed = false;
    this.hasRolled = false;
    this.pushHopeCost = 0;
  }

  public DicePool(int baseDice, int gearDice) {
    this(baseDice, gearDice, new Random());
  }

  @Override
  public int roll() {
    lastBaseResults = rollDice(baseDice);
    lastGearResults = rollDice(gearDice);
    hasRolled = true;
    pushed = false;
    pushHopeCost = 0;
    return countSuccesses();
  }

  @Override
  public int[] rollMultiple(int count) {
    int[] results = new int[count];
    for (int i = 0; i < count; i++)
      results[i] = roll();
    return results;
  }

  public int push() {
    if (!hasRolled)
      throw new IllegalStateException("Cannot push before rolling");
    if (pushed)
      throw new IllegalStateException("Cannot push more than once");
    pushed = true;
    pushHopeCost = 0;
    for (int i = 0; i < lastBaseResults.length; i++) {
      if (lastBaseResults[i] != 6) {
        lastBaseResults[i] = rng.nextInt(6) + 1;
        if (lastBaseResults[i] == 1)
          pushHopeCost++;
      }
    }
    return countSuccesses();
  }

  private int countSuccesses() {
    int successes = 0;
    for (int die : lastBaseResults)
      if (die == 6)
        successes++;
    for (int die : lastGearResults)
      if (die == 6)
        successes++;
    return successes;
  }

  private int[] rollDice(int n) {
    int[] results = new int[n];
    for (int i = 0; i < n; i++)
      results[i] = rng.nextInt(6) + 1;
    return results;
  }

  public int[] getLastBaseResults() {
    return lastBaseResults.clone();
  }

  public int[] getLastGearResults() {
    return lastGearResults.clone();
  }

  public boolean isPushed() {
    return pushed;
  }

  public boolean hasRolled() {
    return hasRolled;
  }

  public int getPushHopeCost() {
    return pushHopeCost;
  }

  public int getBaseDice() {
    return baseDice;
  }

  public int getGearDice() {
    return gearDice;
  }

  public int getTotalDice() {
    return baseDice + gearDice;
  }
}
