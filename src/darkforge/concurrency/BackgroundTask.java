package darkforge.concurrency;

import java.util.concurrent
        .CancellationException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/**
 * Generic SwingWorker adapter for off-EDT
 * work in DARKFORGE GUI panels. Subclasses
 * override compute() to do the actual work;
 * doInBackground() is final and delegates.
 *
 * Lifecycle:
 *   1. Subclass overrides compute() — runs on
 *      a worker thread, NOT the EDT.
 *   2. done() runs on the EDT after compute()
 *      completes; it dispatches result or
 *      error to the registered handlers.
 *
 * Why SwingWorker over raw Thread plus
 * SwingUtilities.invokeLater:
 *   - SwingWorker formalizes the worker/EDT
 *     boundary and the publish/process/done
 *     lifecycle.
 *   - get() in done() unwraps any exception
 *     thrown by compute(), giving a single
 *     error-handler hook.
 *
 * Usage example (Phase 2):
 *   new BackgroundTask<BenchmarkResult>() {
 *       @Override
 *       protected BenchmarkResult compute() {
 *           return benchmark.run();
 *       }
 *   }
 *   .onResult(r -> resultsTable.update(r))
 *   .onError(e  -> showError(e))
 *   .execute();
 */
public abstract class BackgroundTask<T>
        extends SwingWorker<T, Integer> {

    // =========================================
    // Fields
    // =========================================

    private Consumer<T> resultHandler
            = r -> { };
    private Consumer<Throwable> errorHandler
            = e -> e.printStackTrace();

    // =========================================
    // Subclass contract
    // =========================================

    /**
     * Override with the off-EDT work.
     * Do NOT touch Swing components here.
     */
    protected abstract T compute()
            throws Exception;

    // =========================================
    // SwingWorker contract (final)
    // =========================================

    @Override
    protected final T doInBackground()
            throws Exception {
        return compute();
    }

    @Override
    protected final void done() {
        try {
            T result = get();
            resultHandler.accept(result);
        } catch (CancellationException ce) {
            // Silently swallow cancellation.
        } catch (Exception e) {
            Throwable cause =
                    (e.getCause() != null)
                            ? e.getCause() : e;
            errorHandler.accept(cause);
        }
    }

    // =========================================
    // Fluent configuration
    // =========================================

    public BackgroundTask<T> onResult(
            Consumer<T> handler) {
        if (handler != null) {
            this.resultHandler = handler;
        }
        return this;
    }

    public BackgroundTask<T> onError(
            Consumer<Throwable> handler) {
        if (handler != null) {
            this.errorHandler = handler;
        }
        return this;
    }
}