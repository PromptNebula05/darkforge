package darkforge.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Verifies RollBatchTask implements the
 * Coriolis: The Great Dark push rule correctly
 * and is deterministic for a given seed.
 *
 * The push rule (rulebook):
 *   - Initial roll of N d6.
 *   - On a push: re-roll any die not showing
 *     a 6 or a 1. Sixes lock as successes;
 *     ones lock as banes (do NOT re-roll).
 */
class RollBatchTaskTest {

    private static final long SEED = 42L;

    @Test
    void zeroRollsProducesEmptyResult() {
        RollBatchTask task = new RollBatchTask(
                3, false, 0L, SEED);
        BatchResult r = task.call();
        assertEquals(0L, r.getRollsCompleted());
        assertEquals(0L, r.getPoolSuccesses());
        assertEquals(0L, r.getTotalSixes());
        assertEquals(0L, r.getBaneOnes());
    }

    @Test
    void sameSeedProducesIdenticalResult() {
        // Determinism is what makes the
        // parallel/sequential equivalence test
        // possible. Pin it here first.
        RollBatchTask a = new RollBatchTask(
                5, true, 10_000L, SEED);
        RollBatchTask b = new RollBatchTask(
                5, true, 10_000L, SEED);
        BatchResult ra = a.call();
        BatchResult rb = b.call();
        assertEquals(
                ra.getRollsCompleted(),
                rb.getRollsCompleted());
        assertEquals(
                ra.getPoolSuccesses(),
                rb.getPoolSuccesses());
        assertEquals(
                ra.getTotalSixes(),
                rb.getTotalSixes());
        assertEquals(
                ra.getBaneOnes(),
                rb.getBaneOnes());
    }

    @Test
    void unpushedHasNoBanes() {
        // 1s only count as banes on PUSHED
        // re-rolls. Unpushed rolls cannot
        // produce banes regardless of seed.
        RollBatchTask task = new RollBatchTask(
                10, false, 50_000L, SEED);
        BatchResult r = task.call();
        assertEquals(0L, r.getBaneOnes(),
                "Unpushed rolls must produce no"
                        + " banes (1s on initial roll"
                        + " do not lock as banes).");
    }

    @Test
    void pushedProducesMoreSuccessesThanUnpushed() {
        // Sanity check on the push rule: at
        // large N, pushed must beat unpushed.
        long n = 100_000L;
        BatchResult unpushed = new RollBatchTask(
                3, false, n, SEED).call();
        BatchResult pushed = new RollBatchTask(
                3, true, n, SEED).call();
        assertTrue(
                pushed.getPoolSuccesses()
                        > unpushed.getPoolSuccesses(),
                "Pushed rolls should yield more"
                        + " successes than unpushed at"
                        + " N=" + n);
    }

    @Test
    void countersStayInternallyConsistent() {
        // poolSuccesses can never exceed total
        // rolls completed; totalSixes >=
        // poolSuccesses (every success has at
        // least one six).
        BatchResult r = new RollBatchTask(
                7, true, 50_000L, SEED).call();
        assertTrue(r.getPoolSuccesses()
                <= r.getRollsCompleted());
        assertTrue(r.getTotalSixes()
                >= r.getPoolSuccesses());
    }
}
