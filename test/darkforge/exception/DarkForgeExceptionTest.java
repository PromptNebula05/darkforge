package darkforge.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the abstract DarkForgeException base class.
 * Uses a minimal TestException subclass since DarkForgeException
 * cannot be instantiated directly.
 */
class DarkForgeExceptionTest {

    /** Minimal concrete subclass for testing. */
    private static class TestException extends DarkForgeException {
        TestException(String userMessage, String technicalDetail) {
            super(userMessage, technicalDetail);
        }
        TestException(String userMessage, String technicalDetail,
                      Throwable cause) {
            super(userMessage, technicalDetail, cause);
        }
    }

    @Test
    void shouldReturnUserMessage() {
        TestException ex = new TestException(
                "Something went wrong.",
                "InternalError: code=42");
        assertEquals("Something went wrong.", ex.getUserMessage());
    }

    @Test
    void shouldReturnTechnicalDetailViaGetMessage() {
        TestException ex = new TestException(
                "Friendly message",
                "TechnicalDetail: field=value");
        assertEquals("TechnicalDetail: field=value", ex.getMessage());
    }

    @Test
    void shouldReturnTechnicalDetailViaGetter() {
        TestException ex = new TestException(
                "Friendly", "Technical");
        assertEquals("Technical", ex.getTechnicalDetail());
    }

    @Test
    void shouldReturnCauseWhenProvided() {
        RuntimeException cause = new RuntimeException("root cause");
        TestException ex = new TestException(
                "User msg", "Tech detail", cause);
        assertSame(cause, ex.getCause());
    }

    @Test
    void shouldReturnNullCauseWhenNotProvided() {
        TestException ex = new TestException(
                "User msg", "Tech detail");
        assertNull(ex.getCause());
    }

    @Test
    void shouldBeCheckedExceptionNotRuntime() {
        TestException ex = new TestException("msg", "detail");
        assertInstanceOf(Exception.class, ex);
        assertFalse(ex instanceof RuntimeException,
                "DarkForgeException should be checked (not RuntimeException)");
    }

    @Test
    void shouldKeepUserAndTechnicalMessagesSeparate() {
        TestException ex = new TestException(
                "Player-friendly message",
                "developer.debug(): value=-1");
        assertNotEquals(ex.getUserMessage(), ex.getMessage(),
                "User message and getMessage() should differ");
    }
}