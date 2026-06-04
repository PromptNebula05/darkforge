package darkforge.facade;

import darkforge.concurrency.DiceSimulator;
import darkforge.concurrency
        .ProbabilityTable;

/**
 * Façade subfacade for darkforge.concurrency.
 * Mirrors FacadeCatalog: package-private
 * constructor; the single instance is built
 * and held by FacadeDarkforge.initialize().
 * Passthrough methods to the underlying
 * DiceSimulator; lifecycle hook for executor
 * shutdown.
 */
public class FacadeConcurrency {

    // =========================================
    // Construction
    // =========================================
    // Package-private. The single instance is
    // built and held by
    // FacadeDarkforge.initialize() and
    // exposed via concurrencyAccess().

    private final DiceSimulator simulator;

    FacadeConcurrency() {
        this.simulator = new DiceSimulator();
    }

    // =========================================
    // Passthrough
    // =========================================

    public ProbabilityTable runParallel(
            int poolSize, boolean pushed,
            long totalRolls, long seed) {
        return simulator.runParallel(
                poolSize, pushed,
                totalRolls, seed);
    }

    public ProbabilityTable runSequential(
            int poolSize, boolean pushed,
            long totalRolls, long seed) {
        return simulator.runSequential(
                poolSize, pushed,
                totalRolls, seed);
    }

    public int getThreadCount() {
        return simulator.getThreadCount();
    }

    public long getBatchSize() {
        return simulator.getBatchSize();
    }

    // =========================================
    // Lifecycle
    // =========================================

    public void shutdown() {
        simulator.shutdown();
    }
}