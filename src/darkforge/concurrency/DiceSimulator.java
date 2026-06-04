package darkforge.concurrency;

import darkforge.exception
        .ConcurrencyException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Coordinator for parallel Coriolis dice-pool
 * simulations. Owns the ExecutorService,
 * splits a total roll count into batches,
 * submits RollBatchTasks via invokeAll, and
 * merges Futures into a ProbabilityTable.
 *
 * Why ExecutorService + Callable + AtomicLong:
 *   - Dice-pool simulation is embarrassingly
 *     parallel; statistical convergence
 *     requires large N. A bounded fixed-size
 *     pool gives predictable resource use
 *     and clean shutdown via awaitTermination.
 *   - Callable<BatchResult> returns aggregate
 *     counts per batch; Runnable would force
 *     shared mutable state on the hot path.
 *   - AtomicLong aggregation removes a
 *     serializing monitor that would otherwise
 *     gate every batch completion.
 *
 * Alternatives considered:
 *   - Raw Thread + synchronized counter:
 *     rejected. Lock contention on the success
 *     counter; harder shutdown semantics.
 *   - parallelStream over ForkJoinPool.common:
 *     rejected. No control over pool size or
 *     lifecycle; weaker showcase of Module 5
 *     vocabulary (ExecutorService, Callable,
 *     Future, invokeAll, awaitTermination).
 *
 * Lifecycle: call shutdown() at application
 * exit. runParallel and runSequential are
 * safe to call repeatedly until shutdown.
 */
public class DiceSimulator {

    // =========================================
    // Constants
    // =========================================

    private static final long
            DEFAULT_BATCH_SIZE = 50_000L;

    private static final long
            SHUTDOWN_TIMEOUT_SECONDS = 10L;

    // Knuth-style mixer for per-batch seed
    // derivation (golden ratio in fixed point).
    private static final long
            SEED_MIXER = 0x9E3779B97F4A7C15L;

    // =========================================
    // Fields
    // =========================================

    private final ExecutorService executor;
    private final int threadCount;
    private final long batchSize;

    // =========================================
    // Constructors
    // =========================================

    public DiceSimulator() {
        this(Runtime.getRuntime()
                        .availableProcessors(),
                DEFAULT_BATCH_SIZE);
    }

    public DiceSimulator(int threadCount,
                         long batchSize) {
        if (threadCount < 1) {
            throw new IllegalArgumentException(
                    "threadCount must be >= 1");
        }
        if (batchSize < 1) {
            throw new IllegalArgumentException(
                    "batchSize must be >= 1");
        }
        this.threadCount = threadCount;
        this.batchSize   = batchSize;
        this.executor    = Executors
                .newFixedThreadPool(threadCount);
    }

    // =========================================
    // Public API
    // =========================================

    /**
     * Run totalRolls rolls in parallel.
     * Returns the populated ProbabilityTable.
     */
    public ProbabilityTable runParallel(
            int poolSize, boolean pushed,
            long totalRolls, long seed) {
        ProbabilityTable table =
                new ProbabilityTable(
                        poolSize, pushed);
        List<RollBatchTask> tasks =
                buildBatches(poolSize, pushed,
                        totalRolls, seed);
        try {
            List<Future<BatchResult>> futures
                    = executor.invokeAll(tasks);
            for (Future<BatchResult> f
                    : futures) {
                table.recordBatch(f.get());
            }
        } catch (InterruptedException ie) {
            Thread.currentThread()
                    .interrupt();
            throw new ConcurrencyException(
                    "Simulation interrupted",
                    ie);
        } catch (ExecutionException ee) {
            throw new ConcurrencyException(
                    "Batch task failed: "
                            + ee.getMessage(),
                    ee);
        }
        return table;
    }

    /**
     * Run totalRolls rolls on the calling
     * thread, batch-for-batch identical to
     * runParallel (same seed → same batch set).
     * Used for parallel-vs-sequential
     * equivalence tests and speedup comparison.
     */
    public ProbabilityTable runSequential(
            int poolSize, boolean pushed,
            long totalRolls, long seed) {
        ProbabilityTable table =
                new ProbabilityTable(
                        poolSize, pushed);
        List<RollBatchTask> tasks =
                buildBatches(poolSize, pushed,
                        totalRolls, seed);
        for (RollBatchTask task : tasks) {
            table.recordBatch(task.call());
        }
        return table;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(
                    SHUTDOWN_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executor.shutdownNow();
            Thread.currentThread()
                    .interrupt();
        }
    }

    // =========================================
    // Getters
    // =========================================

    public int getThreadCount() {
        return threadCount;
    }

    public long getBatchSize() {
        return batchSize;
    }

    // =========================================
    // Helpers
    // =========================================

    /**
     * Split totalRolls into batches of size
     * batchSize (last batch may be smaller).
     * Per-batch seed = (seed XOR index * mixer)
     * so the parallel and sequential runs see
     * the same batch set, which is what makes
     * parallel-vs-sequential equivalence
     * testable.
     */
    private List<RollBatchTask> buildBatches(
            int poolSize, boolean pushed,
            long totalRolls, long seed) {
        if (totalRolls < 0) {
            throw new IllegalArgumentException(
                    "totalRolls cannot be"
                            + " negative");
        }
        List<RollBatchTask> tasks =
                new ArrayList<>();
        long remaining = totalRolls;
        long index = 0L;
        while (remaining > 0) {
            long thisBatch = Math.min(
                    batchSize, remaining);
            long batchSeed = seed
                    ^ (index * SEED_MIXER);
            tasks.add(new RollBatchTask(
                    poolSize, pushed,
                    thisBatch, batchSeed));
            remaining -= thisBatch;
            index++;
        }
        return tasks;
    }
}