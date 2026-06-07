package darkforge.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Asserts that the simulator's measured
 * success rates fall within ±1% of the
 * analytic Coriolis: The Great Dark rulebook
 * probabilities at N = 1,000,000 rolls.
 *
 * Expected values:
 *   unpushed N=3 : 1 - (5/6)^3  ≈ 42.13%
 *   pushed   N=3 : 1 - (13/18)^3 ≈ 62.33%
 *   unpushed N=5 : 1 - (5/6)^5  ≈ 59.81%
 *   pushed   N=5 : 1 - (13/18)^5 ≈ 80.39%
 *
 * Pushed per-die success = 1/6 + (4/6)(1/6)
 *                        = 5/18 ≈ 27.78%.
 * Per-pool success = 1 - (13/18)^N.
 */
class DiceSimulatorConvergenceTest {

    private static final long ROLLS = 1_000_000L;
    private static final long SEED = 12345L;
    private static final double TOLERANCE = 0.01;

    private DiceSimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new DiceSimulator();
    }

    @AfterEach
    void tearDown() {
        simulator.shutdown();
    }

    @Test
    void unpushedThreeDiceMatchesRulebook() {
        double expected =
                1.0 - Math.pow(5.0 / 6.0, 3);
        ProbabilityTable t =
                simulator.runParallel(
                        3, false, ROLLS, SEED);
        assertEquals(expected,
                t.getSuccessRate(), TOLERANCE);
    }

    @Test
    void pushedThreeDiceMatchesRulebook() {
        double expected =
                1.0 - Math.pow(13.0 / 18.0, 3);
        ProbabilityTable t =
                simulator.runParallel(
                        3, true, ROLLS, SEED);
        assertEquals(expected,
                t.getSuccessRate(), TOLERANCE);
    }

    @Test
    void unpushedFiveDiceMatchesRulebook() {
        double expected =
                1.0 - Math.pow(5.0 / 6.0, 5);
        ProbabilityTable t =
                simulator.runParallel(
                        5, false, ROLLS, SEED);
        assertEquals(expected,
                t.getSuccessRate(), TOLERANCE);
    }

    @Test
    void pushedFiveDiceMatchesRulebook() {
        double expected =
                1.0 - Math.pow(13.0 / 18.0, 5);
        ProbabilityTable t =
                simulator.runParallel(
                        5, true, ROLLS, SEED);
        assertEquals(expected,
                t.getSuccessRate(), TOLERANCE);
    }
}
