package darkforge.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * For a fixed (seed, totalRolls, poolSize,
 * pushed), runParallel and runSequential must
 * produce exact-equal counter totals.
 *
 * This works because DiceSimulator derives
 * per-batch seeds deterministically from
 * (seed XOR batchIndex * mixer), so both
 * paths see the same batch set in the same
 * order — the parallel path just executes
 * them on multiple threads.
 *
 * If this test fails, either:
 *   (a) the AtomicLong aggregator is losing
 *       updates under contention, or
 *   (b) per-batch seed derivation diverges
 *       between the two paths.
 * Both are blockers.
 */
class DiceSimulatorParallelEquivalenceTest {

    private static final long ROLLS = 200_000L;
    private static final long SEED = 99L;

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
    void parallelEqualsSequentialUnpushed() {
        ProbabilityTable par =
                simulator.runParallel(
                        5, false, ROLLS, SEED);
        ProbabilityTable seq =
                simulator.runSequential(
                        5, false, ROLLS, SEED);
        assertSameTotals(par, seq);
    }

    @Test
    void parallelEqualsSequentialPushed() {
        ProbabilityTable par =
                simulator.runParallel(
                        7, true, ROLLS, SEED);
        ProbabilityTable seq =
                simulator.runSequential(
                        7, true, ROLLS, SEED);
        assertSameTotals(par, seq);
    }

    private void assertSameTotals(
            ProbabilityTable par,
            ProbabilityTable seq) {
        BatchResult p = par.snapshot();
        BatchResult s = seq.snapshot();
        assertEquals(s.getRollsCompleted(),
                p.getRollsCompleted(),
                "rollsCompleted mismatch");
        assertEquals(s.getPoolSuccesses(),
                p.getPoolSuccesses(),
                "poolSuccesses mismatch — possible"
                        + " lost update in AtomicLong"
                        + " aggregation");
        assertEquals(s.getTotalSixes(),
                p.getTotalSixes(),
                "totalSixes mismatch");
        assertEquals(s.getBaneOnes(),
                p.getBaneOnes(),
                "baneOnes mismatch");
    }
}
