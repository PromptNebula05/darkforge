package darkforge.concurrency;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Hammer test: 8 threads each call
 * recordBatch 100,000 times with a known
 * BatchResult. Final totals must equal the
 * exact arithmetic product, with zero lost
 * updates.
 *
 * This is the direct empirical evidence that
 * the AtomicLong aggregator is thread-safe
 * under contention, which is the §5.2 claim.
 */
class ProbabilityTableConcurrencyTest {

    private static final int THREADS = 8;
    private static final int CALLS_PER_THREAD
            = 100_000;

    @Test
    void noLostUpdatesUnderContention()
            throws InterruptedException {
        ProbabilityTable table =
                new ProbabilityTable(3, true);
        BatchResult unit = new BatchResult(
                10L, 4L, 6L, 1L);

        ExecutorService pool =
                Executors.newFixedThreadPool(
                        THREADS);
        try {
            for (int t = 0; t < THREADS; t++) {
                pool.submit(() -> {
                    for (int i = 0;
                         i < CALLS_PER_THREAD;
                         i++) {
                        table.recordBatch(unit);
                    }
                });
            }
            pool.shutdown();
            assertTrue(pool.awaitTermination(
                            30, TimeUnit.SECONDS),
                    "Hammer threads timed out");
        } finally {
            if (!pool.isTerminated()) {
                pool.shutdownNow();
            }
        }

        long expectedRolls =
                (long) THREADS * CALLS_PER_THREAD
                        * unit.getRollsCompleted();
        long expectedSuccesses =
                (long) THREADS * CALLS_PER_THREAD
                        * unit.getPoolSuccesses();
        long expectedSixes =
                (long) THREADS * CALLS_PER_THREAD
                        * unit.getTotalSixes();
        long expectedBanes =
                (long) THREADS * CALLS_PER_THREAD
                        * unit.getBaneOnes();

        BatchResult got = table.snapshot();
        assertEquals(expectedRolls,
                got.getRollsCompleted(),
                "Lost updates in rollsCompleted");
        assertEquals(expectedSuccesses,
                got.getPoolSuccesses(),
                "Lost updates in poolSuccesses");
        assertEquals(expectedSixes,
                got.getTotalSixes(),
                "Lost updates in totalSixes");
        assertEquals(expectedBanes,
                got.getBaneOnes(),
                "Lost updates in baneOnes");
    }
}
