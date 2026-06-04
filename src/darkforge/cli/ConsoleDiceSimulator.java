package darkforge.cli;

import darkforge.concurrency
        .ProbabilityTable;
import darkforge.facade
        .FacadeConcurrency;
import darkforge.facade.FacadeDarkforge;
import java.util.Scanner;

/**
 * CLI handler for option [14] Dice
 * Probability Simulator. Prompts for pool
 * size, push toggle, and total rolls; runs
 * the simulation parallel and sequential
 * with the same seed (identical batch set);
 * prints rates + speedup.
 *
 * Verification target: a 3-die pool converges
 * to rulebook values within ±1%:
 *   unpushed → P(success) ≈ 42.1%
 *               (= 1 - (5/6)^3)
 *   pushed   → P(success) ≈ 62.3%
 *               (= 1 - (13/18)^3)
 *
 * The pushed value derives from the
 * Coriolis: The Great Dark push rule:
 * re-roll any die not showing a 6 or a 1.
 *   per-die success after push
 *     = 1/6 + (4/6)(1/6) = 5/18
 *   3-die pool success
 *     = 1 - (1 - 5/18)^3 ≈ 62.3%
 */
public class ConsoleDiceSimulator {

    // =========================================
    // Constants
    // =========================================

    private static final long
            DEFAULT_TOTAL_ROLLS = 1_000_000L;

    // =========================================
    // Entry point
    // =========================================

    public void run(Scanner in) {
        System.out.println();
        System.out.println(
                "=== Dice Probability"
                        + " Simulator ===");
        int poolSize = promptInt(in,
                "Pool size (1-10): ", 1, 10);
        boolean pushed = promptBoolean(in,
                "Pushed? (y/n): ");
        long totalRolls = promptLong(in,
                "Total rolls [default "
                        + DEFAULT_TOTAL_ROLLS
                        + "]: ",
                DEFAULT_TOTAL_ROLLS);
        long seed = System.nanoTime();

        FacadeConcurrency facade =
                FacadeDarkforge.getTheInstance()
                        .concurrencyAccess();

        // Parallel run
        long t0 = System.nanoTime();
        ProbabilityTable parallel =
                facade.runParallel(poolSize,
                        pushed, totalRolls, seed);
        long parallelMs =
                (System.nanoTime() - t0)
                        / 1_000_000L;

        // Sequential run (same seed →
        // identical batch set)
        long t1 = System.nanoTime();
        ProbabilityTable sequential =
                facade.runSequential(poolSize,
                        pushed, totalRolls, seed);
        long sequentialMs =
                (System.nanoTime() - t1)
                        / 1_000_000L;

        printResults(parallel, sequential,
                parallelMs, sequentialMs,
                facade.getThreadCount());
    }

    // =========================================
    // Reporting
    // =========================================

    private void printResults(
            ProbabilityTable parallel,
            ProbabilityTable sequential,
            long parallelMs,
            long sequentialMs,
            int threads) {
        System.out.println();
        System.out.printf(
                "Pool: %d dice, pushed: %s%n",
                parallel.getPoolSize(),
                parallel.isPushed());
        System.out.printf(
                "Rolls: %,d%n",
                parallel.getRollsCompleted());
        System.out.println();
        System.out.printf(
                "  Parallel  (%d threads):"
                        + " %5.2f%% success,"
                        + " avg sixes %.3f,"
                        + " %,d ms%n",
                threads,
                parallel.getSuccessRate()
                        * 100.0,
                parallel.getAverageSixes(),
                parallelMs);
        System.out.printf(
                "  Sequential (1 thread):"
                        + " %5.2f%% success,"
                        + " avg sixes %.3f,"
                        + " %,d ms%n",
                sequential.getSuccessRate()
                        * 100.0,
                sequential.getAverageSixes(),
                sequentialMs);
        if (parallelMs > 0) {
            double speedup =
                    (double) sequentialMs
                            / (double) parallelMs;
            System.out.printf(
                    "  Speedup: %.2fx%n",
                    speedup);
        }
        System.out.printf(
                "  Banes (1s on pushed"
                        + " dice): %,d%n",
                parallel.snapshot()
                        .getBaneOnes());
    }

    // =========================================
    // Prompts
    // =========================================

    private int promptInt(Scanner in,
                          String prompt, int min,
                          int max) {
        while (true) {
            System.out.print(prompt);
            String line = in.nextLine()
                    .trim();
            try {
                int v = Integer.parseInt(
                        line);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (
                    NumberFormatException e) {
                // fall through
            }
            System.out.printf(
                    "Please enter %d-%d.%n",
                    min, max);
        }
    }

    private long promptLong(Scanner in,
                            String prompt,
                            long defaultValue) {
        System.out.print(prompt);
        String line = in.nextLine().trim();
        if (line.isEmpty()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(line);
        } catch (
                NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean promptBoolean(
            Scanner in, String prompt) {
        System.out.print(prompt);
        String line = in.nextLine().trim()
                .toLowerCase();
        return line.startsWith("y");
    }
}