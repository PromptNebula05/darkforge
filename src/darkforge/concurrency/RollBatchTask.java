package darkforge.concurrency;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * One batch of Coriolis dice-pool rolls,
 * returned as an immutable BatchResult.
 * Submitted to DiceSimulator's ExecutorService
 * and harvested via Future.
 *
 * Coriolis: The Great Dark dice mechanic
 * (rulebook, "Rolling Dice" / "Pushing"):
 *
 *   1. Roll N d6 where N = poolSize.
 *   2. Each 6 = one success.
 *   3. If pushed: re-roll any die that is
 *      neither a 6 nor a 1. Sixes lock as
 *      successes; ones lock as banes — they
 *      do NOT get re-rolled.
 *   4. After the push, 6s on re-rolled dice
 *      add successes; 1s on re-rolled dice
 *      are banes (Base die 1s → Hope damage;
 *      Gear die 1s → gear damage). The
 *      simulator tracks raw bane count and
 *      leaves Hope/gear attribution to the
 *      caller.
 *
 * Thread safety: each RollBatchTask owns its
 * own java.util.Random instance, seeded from
 * (taskSeed, batchIndex) by DiceSimulator. No
 * Random is shared across threads.
 *
 * Why not inject DicePool? DicePool is the
 * single-roll abstraction from Iteration 1
 * and does not expose a seedable RNG hook.
 * Implementing the push rule directly here
 * (a) avoids thread-safety questions on a
 * shared collaborator, (b) makes batches
 * deterministic for a given seed, enabling
 * the parallel-vs-sequential equivalence
 * test in Phase 3. RollBatchTaskTest asserts
 * statistical equivalence with DicePool.
 */
public final class RollBatchTask
        implements Callable<BatchResult> {

    // =========================================
    // Fields
    // =========================================

    private final int poolSize;
    private final boolean pushed;
    private final long rollsInBatch;
    private final long seed;

    // =========================================
    // Constructor
    // =========================================

    public RollBatchTask(int poolSize,
                         boolean pushed,
                         long rollsInBatch, long seed) {
        if (poolSize < 1 || poolSize > 20) {
            throw new IllegalArgumentException(
                    "Pool size must be 1-20,"
                            + " got " + poolSize);
        }
        if (rollsInBatch < 0) {
            throw new IllegalArgumentException(
                    "rollsInBatch cannot be"
                            + " negative");
        }
        this.poolSize     = poolSize;
        this.pushed       = pushed;
        this.rollsInBatch = rollsInBatch;
        this.seed         = seed;
    }

    // =========================================
    // Callable contract
    // =========================================

    @Override
    public BatchResult call() {
        Random rng = new Random(seed);
        long successes = 0L;
        long sixes     = 0L;
        long banes     = 0L;
        int[] dice = new int[poolSize];

        for (long i = 0;
             i < rollsInBatch;
             i++) {
            // Initial roll
            int rollSixes = 0;
            for (int d = 0;
                 d < poolSize;
                 d++) {
                dice[d] = rng.nextInt(6) + 1;
                if (dice[d] == 6) {
                    rollSixes++;
                }
            }

            // Push: re-roll any die that
            // is not a 6 and not a 1.
            if (pushed) {
                for (int d = 0;
                     d < poolSize;
                     d++) {
                    if (dice[d] != 6
                            && dice[d] != 1) {
                        int reroll =
                                rng.nextInt(6) + 1;
                        dice[d] = reroll;
                        if (reroll == 6) {
                            rollSixes++;
                        } else if (
                                reroll == 1) {
                            banes++;
                        }
                    }
                }
            }

            sixes += rollSixes;
            if (rollSixes > 0) {
                successes++;
            }
        }

        return new BatchResult(
                rollsInBatch, successes,
                sixes, banes);
    }
}