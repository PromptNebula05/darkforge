package darkforge.exception;

/**
 * Thrown when the equipment catalog fails
 * to load from JSON resource files.
 * Mirrors GameDataLoadException pattern.
 *
 * userMessage  → player-facing (displayed
 *                by CLI)
 * technicalDetail → developer-facing
 *                   (getMessage())
 */
public class CatalogLoadException
        extends DarkForgeException {

    private final String resourceFile;

    public CatalogLoadException(
            String resourceFile,
            String message) {
        super("Failed to load catalog"
                        + " resource '"
                        + resourceFile + "'",
                message);
        this.resourceFile = resourceFile;
    }

    public CatalogLoadException(
            String resourceFile,
            String message,
            Throwable cause) {
        super("Failed to load catalog"
                        + " resource '"
                        + resourceFile + "'",
                message, cause);
        this.resourceFile = resourceFile;
    }

    public String getResourceFile() {
        return resourceFile;
    }
}