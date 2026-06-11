package darkforge.concurrency;

import java.util.concurrent.atomic
        .AtomicLong;

/**
 * Lock-free aggregator for parallel dice
 * batches. Each AtomicLong field is hit once
 * per batch (recordBatch), not once per roll —
 * batches do their own internal sums, so
 * contention is bounded by batch count,
 * not roll count.
 *
 * Why AtomicLong over synchronized blocks:
 *   - Counter updates are the hottest line
 *     in the simulator. The Module 5 reading
 *     explicitly recommends AtomicLong over
 *     synchronized blocks for high-contention
 *     counters.
 *   - Lock-free aggregation removes a
 *     serializing monitor that would otherwise
 *     gate every batch completion.
 */
public class ProbabilityTable {

    // =========================================
    // Fields
    // =========================================

    private final int poolSize;
    private final boolean pushed;
    private final AtomicLong rollsCompleted
            = new AtomicLong(0);
    private final AtomicLong poolSuccesses
            = new AtomicLong(0);
    private final AtomicLong totalSixes
            = new AtomicLong(0);
    private final AtomicLong baneOnes
            = new AtomicLong(0);

    // =========================================
    // Constructor
    // =========================================

    public ProbabilityTable(int poolSize,
                            boolean pushed) {
        if (poolSize < 1 || poolSize > 20) {
            throw new IllegalArgumentException(
                    "Pool size must be 1-20,"
                            + " got " + poolSize);
        }
        this.poolSize = poolSize;
        this.pushed   = pushed;
    }

    // =========================================
    // Mutation
    // =========================================

    /**
     * Atomically merge a batch result into
     * the running totals. Safe to call from
     * any thread; AtomicLong.addAndGet is
     * lock-free on supported platforms.
     */
    public void recordBatch(
            BatchResult batch) {
        rollsCompleted.addAndGet(
                batch.getRollsCompleted());
        poolSuccesses.addAndGet(
                batch.getPoolSuccesses());
        totalSixes.addAndGet(
                batch.getTotalSixes());
        baneOnes.addAndGet(
                batch.getBaneOnes());
    }

    // =========================================
    // Snapshot view
    // =========================================

    /**
     * Snapshot current totals as a single
     * BatchResult. Reads are not coherent
     * across all four AtomicLongs (no global
     * lock), but for monotonic counters the
     * worst case is a slightly-stale value
     * mid-run, which is acceptable for live
     * progress display.
     */
    public BatchResult snapshot() {
        return new BatchResult(
                rollsCompleted.get(),
                poolSuccesses.get(),
                totalSixes.get(),
                baneOnes.get());
    }

    // =========================================
    // Getters
    // =========================================

    public int getPoolSize() {
        return poolSize;
    }

    public boolean isPushed() {
        return pushed;
    }

    public long getRollsCompleted() {
        return rollsCompleted.get();
    }

    // =========================================
    // Derived rates
    // =========================================

    /**
     * P(at least one 6 in the pool). This is
     * the rulebook "success rate" — what the
     * convergence test asserts against.
     */
    public double getSuccessRate() {
        long rolls = rollsCompleted.get();
        if (rolls == 0) {
            return 0.0;
        }
        return (double) poolSuccesses.get()
                / (double) rolls;
    }

    public double getAverageSixes() {
        long rolls = rollsCompleted.get();
        if (rolls == 0) {
            return 0.0;
        }
        return (double) totalSixes.get()
                / (double) rolls;
    }

    public double getBaneRate() {
        long rolls = rollsCompleted.get();
        if (rolls == 0) {
            return 0.0;
        }
        return (double) baneOnes.get()
                / (double) rolls;
    }
}