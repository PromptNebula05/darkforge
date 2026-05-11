package darkforge.mechanics;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DicePoolTest {

  @Test
  void shouldReturnZeroSuccessesForZeroDice() {
    DicePool pool = new DicePool(0, 0, new Random(42));
    assertEquals(0, pool.roll());
  }

  @Test
  void shouldCountSuccessesCorrectly() {
    Random rng = new Random(42);
    DicePool pool = new DicePool(5, 2, rng);
    int successes = pool.roll();
    assertTrue(successes >= 0);
    assertTrue(successes <= 7);
  }

  @Test
  void shouldPreventDoublePush() {
    DicePool pool = new DicePool(3, 2, new Random(42));
    pool.roll();
    pool.push();
    assertThrows(IllegalStateException.class, pool::push);
  }

  @Test
  void shouldPreventPushBeforeRoll() {
    DicePool pool = new DicePool(3, 2, new Random(42));
    assertThrows(IllegalStateException.class, pool::push);
  }

  @Test
  void shouldRejectNegativeBaseDice() {
    assertThrows(IllegalArgumentException.class, () -> new DicePool(-1, 0, new Random()));
  }

  @Test
  void shouldRejectNegativeGearDice() {
    assertThrows(IllegalArgumentException.class, () -> new DicePool(0, -1, new Random()));
  }

  @Test
  void shouldReturnCorrectNumberOfMultipleRolls() {
    DicePool pool = new DicePool(3, 2, new Random(42));
    int[] results = pool.rollMultiple(100);
    assertEquals(100, results.length);
  }

  @Test
  void shouldNotReRollGearDiceOnPush() {
    Random rng = new Random(12345);
    DicePool pool = new DicePool(3, 2, rng);
    pool.roll();
    int[] gearBefore = pool.getLastGearResults();
    pool.push();
    int[] gearAfter = pool.getLastGearResults();
    assertArrayEquals(gearBefore, gearAfter, "Gear dice should not change on push");
  }

  @Test
  void shouldResetPushStateOnNewRoll() {
    DicePool pool = new DicePool(3, 2, new Random(42));
    pool.roll();
    pool.push();
    pool.roll();
    assertDoesNotThrow(pool::push);
  }
}
