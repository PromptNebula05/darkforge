package darkforge.exception;

/**
 * Thrown when a save file contains corrupted or malformed data.
 * Bridges the exception hierarchy with the file I/O system.
 * CorruptionType enum enables the CLI to choose recovery strategies.
 */
public class CharacterCorruptionException
        extends DarkForgeException {

    public enum CorruptionType {
        MISSING_FIELD,
        INVALID_VALUE,
        MALFORMED_FORMAT,
        VERSION_MISMATCH
    }

    private final String filePath;
    private final CorruptionType corruptionType;
    private final String fieldName;

    public CharacterCorruptionException(
            String filePath,
            CorruptionType corruptionType,
            String fieldName, String detail) {
        super(
                buildUserMessage(filePath, corruptionType,
                        fieldName, detail),
                buildTechnicalDetail(filePath,
                        corruptionType, fieldName, detail)
        );
        this.filePath = filePath;
        this.corruptionType = corruptionType;
        this.fieldName = fieldName;
    }

    public CharacterCorruptionException(
            String filePath,
            CorruptionType corruptionType,
            String fieldName, String detail,
            Throwable cause) {
        super(
                buildUserMessage(filePath, corruptionType,
                        fieldName, detail),
                buildTechnicalDetail(filePath,
                        corruptionType, fieldName, detail),
                cause
        );
        this.filePath = filePath;
        this.corruptionType = corruptionType;
        this.fieldName = fieldName;
    }

    public String getFilePath() { return filePath; }
    public CorruptionType getCorruptionType() {
        return corruptionType;
    }
    public String getFieldName() { return fieldName; }

    private static String buildUserMessage(
            String filePath, CorruptionType type,
            String fieldName, String detail) {
        String fileName = filePath.contains("/")
                ? filePath.substring(
                filePath.lastIndexOf('/') + 1)
                : filePath;
        return switch (type) {
            case MISSING_FIELD -> String.format(
                    "Save file '%s' is missing required "
                            + "field: %s", fileName, fieldName);
            case INVALID_VALUE -> String.format(
                    "Save file '%s' has invalid data in "
                            + "'%s': %s", fileName, fieldName,
                    detail);
            case MALFORMED_FORMAT -> String.format(
                    "Save file '%s' is not valid JSON "
                            + "and cannot be read.", fileName);
            case VERSION_MISMATCH -> String.format(
                    "Save file '%s' was created by a "
                            + "newer version of DARKFORGE and "
                            + "cannot be loaded.", fileName);
        };
    }

    private static String buildTechnicalDetail(
            String filePath, CorruptionType type,
            String fieldName, String detail) {
        return String.format(
                "CharacterCorruptionException: "
                        + "file=%s, type=%s, field=%s, detail=%s",
                filePath, type, fieldName, detail);
    }
}