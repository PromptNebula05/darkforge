package darkforge.exception;

/**
 * Thrown when a concurrent operation in
 * darkforge.concurrency fails: executor
 * interruption, aborted batch task, shutdown
 * timeout, etc.
 *
 * userMessage    → player-facing (displayed by CLI)
 * technicalDetail → developer-facing (getMessage())
 */
public class ConcurrencyException
        extends DarkForgeException {

    public ConcurrencyException(
            String message) {
        super("A concurrent operation failed",
                message);
    }

    public ConcurrencyException(
            String message,
            Throwable cause) {
        super("A concurrent operation failed",
                message, cause);
    }
}