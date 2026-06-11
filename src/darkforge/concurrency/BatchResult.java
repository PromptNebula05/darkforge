package darkforge.concurrency;

import java.io.Serializable;

/**
 * Immutable aggregate of one RollBatchTask's
 * output. Returned by every call() and merged
 * into a ProbabilityTable.
 *
 * Counters:
 *   rollsCompleted — total pool rolls in batch.
 *   poolSuccesses  — count of rolls that yielded
 *                    at least one 6 (player
 *                    perspective: "rolls that
 *                    passed").
 *   totalSixes     — total count of dice showing
 *                    a 6 across all rolls in
 *                    the batch.
 *   baneOnes       — count of 1s rolled on PUSHED
 *                    re-rolls only. In Coriolis:
 *                    The Great Dark, 1s on pushed
 *                    Base dice cost Hope; 1s on
 *                    pushed Gear dice damage gear.
 *                    The simulator tracks the raw
 *                    count; caller maps to
 *                    Hope/gear damage as needed.
 */
public final class BatchResult
        implements Serializable {

    private static final long
            serialVersionUID = 1L;

    // =========================================
    // Fields
    // =========================================

    private final long rollsCompleted;
    private final long poolSuccesses;
    private final long totalSixes;
    private final long baneOnes;

    // =========================================
    // Constructor
    // =========================================

    public BatchResult(long rollsCompleted,
                       long poolSuccesses,
                       long totalSixes,
                       long baneOnes) {
        if (rollsCompleted < 0
                || poolSuccesses < 0
                || totalSixes < 0
                || baneOnes < 0) {
            throw new IllegalArgumentException(
                    "BatchResult counts cannot"
                            + " be negative");
        }
        if (poolSuccesses > rollsCompleted) {
            throw new IllegalArgumentException(
                    "poolSuccesses cannot exceed"
                            + " rollsCompleted");
        }
        this.rollsCompleted = rollsCompleted;
        this.poolSuccesses  = poolSuccesses;
        this.totalSixes     = totalSixes;
        this.baneOnes       = baneOnes;
    }

    // =========================================
    // Getters
    // =========================================

    public long getRollsCompleted() {
        return rollsCompleted;
    }

    public long getPoolSuccesses() {
        return poolSuccesses;
    }

    public long getTotalSixes() {
        return totalSixes;
    }

    public long getBaneOnes() {
        return baneOnes;
    }

    // =========================================
    // Merge
    // =========================================

    /**
     * Returns a new BatchResult that is the
     * sum of this and other. Used by the
     * parallel-vs-sequential equivalence test
     * to merge per-batch results outside the
     * AtomicLong aggregator.
     */
    public BatchResult merge(
            BatchResult other) {
        return new BatchResult(
                this.rollsCompleted
                        + other.rollsCompleted,
                this.poolSuccesses
                        + other.poolSuccesses,
                this.totalSixes
                        + other.totalSixes,
                this.baneOnes
                        + other.baneOnes);
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String toString() {
        return String.format(
                "BatchResult[rolls=%d,"
                        + " successes=%d, sixes=%d,"
                        + " banes=%d]",
                rollsCompleted, poolSuccesses,
                totalSixes, baneOnes);
    }
}