package darkforge.exception;

import darkforge.exception.CharacterCorruptionException.CorruptionType;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterCorruptionExceptionTest {

    @Test
    void missingFieldShouldIncludeFieldNameInMessage() {
        var ex = new CharacterCorruptionException(
                "/saves/cantara.darkforge.json",
                CorruptionType.MISSING_FIELD,
                "profession", "Required field not found");

        assertTrue(ex.getUserMessage().contains("profession"));
        assertTrue(ex.getUserMessage().contains("cantara.darkforge.json"));
        assertEquals(CorruptionType.MISSING_FIELD, ex.getCorruptionType());
        assertEquals("profession", ex.getFieldName());
    }

    @Test
    void invalidValueShouldIncludeDetailInMessage() {
        var ex = new CharacterCorruptionException(
                "/saves/test.darkforge.json",
                CorruptionType.INVALID_VALUE,
                "attributes.STRENGTH",
                "value -1, must be 2-6");

        assertTrue(ex.getUserMessage().contains("attributes.STRENGTH"));
        assertTrue(ex.getUserMessage().contains("-1"));
    }

    @Test
    void malformedFormatShouldMentionJSON() {
        var ex = new CharacterCorruptionException(
                "bad_file.json",
                CorruptionType.MALFORMED_FORMAT,
                "root", "Parse error");

        assertTrue(ex.getUserMessage().contains("JSON"));
    }

    @Test
    void versionMismatchShouldSuggestUpdating() {
        var ex = new CharacterCorruptionException(
                "future.darkforge.json",
                CorruptionType.VERSION_MISMATCH,
                "version", "Expected 2.0, got 99.0");

        assertTrue(ex.getUserMessage().contains("newer version"));
    }

    @Test
    void shouldExtractFilenameFromFullPath() {
        var ex = new CharacterCorruptionException(
                "/home/user/saves/cantara.darkforge.json",
                CorruptionType.MISSING_FIELD,
                "name", "detail");

        assertTrue(ex.getUserMessage().contains("cantara.darkforge.json"),
                "Should show just filename, not full path");
        assertFalse(ex.getUserMessage().contains("/home/user"),
                "Should not show directory path");
    }

    @Test
    void shouldStoreFilePath() {
        var ex = new CharacterCorruptionException(
                "/saves/test.json",
                CorruptionType.MISSING_FIELD,
                "field", "detail");
        assertEquals("/saves/test.json", ex.getFilePath());
    }

    @Test
    void constructorWithCauseShouldPreserveCause() {
        JSONException cause = new JSONException("bad json");
        var ex = new CharacterCorruptionException(
                "file.json",
                CorruptionType.MALFORMED_FORMAT,
                "root", "Parse error", cause);

        assertSame(cause, ex.getCause());
        assertInstanceOf(JSONException.class, ex.getCause());
    }

    @Test
    void constructorWithoutCauseShouldHaveNullCause() {
        var ex = new CharacterCorruptionException(
                "file.json",
                CorruptionType.MISSING_FIELD,
                "field", "detail");
        assertNull(ex.getCause());
    }

    @Test
    void technicalDetailShouldContainClassName() {
        var ex = new CharacterCorruptionException(
                "file.json",
                CorruptionType.INVALID_VALUE,
                "field", "detail");
        assertTrue(ex.getMessage().contains("CharacterCorruptionException"));
    }

    @Test
    void allCorruptionTypesShouldBeTestable() {
        for (CorruptionType type : CorruptionType.values()) {
            var ex = new CharacterCorruptionException(
                    "test.json", type, "field", "detail");
            assertNotNull(ex.getUserMessage(),
                    "getUserMessage() should not be null for " + type);
            assertNotNull(ex.getMessage(),
                    "getMessage() should not be null for " + type);
        }
    }
}