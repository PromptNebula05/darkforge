package darkforge.exception;

/**
 * Thrown when a relational-database operation in
 * darkforge.database fails: JDBC connection error,
 * failed DDL bootstrap, a constraint violation
 * (CHECK / FK / UNIQUE / PK), or schema verification
 * failure.
 *
 * userMessage    -> player-facing (displayed by CLI)
 * technicalDetail -> developer-facing (getMessage())
 *
 * Mirrors ConcurrencyException so it integrates with
 * the existing DarkForgeException handling in the CLI.
 */
public class DatabaseException
        extends DarkForgeException {

    public DatabaseException(
            String message) {
        super("A database operation failed",
                message);
    }

    public DatabaseException(
            String message,
            Throwable cause) {
        super("A database operation failed",
                message, cause);
    }
}
