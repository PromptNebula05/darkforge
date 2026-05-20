package darkforge.exception;

/**
 * Unchecked exception thrown when a JSON resource
 * file is missing, malformed, or fails validation
 * at startup.
 *
 * Unlike CharacterCorruptionException (checked —
 * user's save file, recoverable), this is a
 * deployment/build error. The application cannot
 * function without game data.
 *
 * Why unchecked: Missing game data is equivalent
 * to a missing .class file — the build/deployment
 * is broken. There is no user-recoverable action.
 */
public class GameDataLoadException
        extends RuntimeException {

    private final String resourceName;
    private final String detail;

    public GameDataLoadException(
            String resourceName, String detail) {
        super(String.format(
                "Failed to load game data '%s': %s",
                resourceName, detail));
        this.resourceName = resourceName;
        this.detail = detail;
    }

    public GameDataLoadException(
            String resourceName, String detail,
            Throwable cause) {
        super(String.format(
                "Failed to load game data '%s': %s",
                resourceName, detail), cause);
        this.resourceName = resourceName;
        this.detail = detail;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getDetail() { return detail; }
}