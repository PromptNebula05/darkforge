package darkforge.mechanics;

public interface Rollable {
  /**
   * Perform a single roll and return the primary result.
   * For DicePool: returns number of successes (6s).
   * For D66Table: returns the D66 index (11-66).
   * For D6Table: returns a value 1-6.
   */
  int roll();

  /**
   * Perform multiple independent rolls.
   * 
   * @param count number of rolls to perform
   * @return array of results, one per roll
   */
  int[] rollMultiple(int count);
}
