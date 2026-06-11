package darkforge.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the abstract {@link DarkForgeException}
 * base class. Verifies the dual-message contract
 * (player-facing userMessage vs developer-facing
 * technicalDetail), the cause-chain behavior of the
 * three-argument constructor, and the unchecked-exception
 * contract that lets the hierarchy propagate through
 * Callable, Future, and SwingWorker without
 * checked-exception wrapping.
 *
 * Uses a minimal {@code TestException} subclass because
 * DarkForgeException is abstract and cannot be instantiated
 * directly.
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

    /**
     * INTENT:        getUserMessage() returns the player-facing
     *                string passed to the constructor verbatim.
     * PRECONDITION:  TestException is constructed with a
     *                non-null userMessage.
     * POSTCONDITION: getUserMessage() returns that exact string.
     */
    @Test
    void shouldReturnUserMessage() {
        TestException ex = new TestException(
                "Something went wrong.",
                "InternalError: code=42");
        assertEquals("Something went wrong.", ex.getUserMessage());
    }

    /**
     * INTENT:        Throwable.getMessage() exposes the
     *                developer-facing technicalDetail (not the
     *                userMessage) so stack traces carry the
     *                diagnostic string.
     * PRECONDITION:  TestException is constructed with distinct
     *                userMessage and technicalDetail strings.
     * POSTCONDITION: getMessage() returns the technicalDetail.
     */
    @Test
    void shouldReturnTechnicalDetailViaGetMessage() {
        TestException ex = new TestException(
                "Friendly message",
                "TechnicalDetail: field=value");
        assertEquals("TechnicalDetail: field=value", ex.getMessage());
    }

    /**
     * INTENT:        getTechnicalDetail() exposes the
     *                developer-facing string explicitly,
     *                independent of Throwable.getMessage().
     * PRECONDITION:  TestException is constructed with a
     *                non-null technicalDetail.
     * POSTCONDITION: getTechnicalDetail() returns that exact
     *                string.
     */
    @Test
    void shouldReturnTechnicalDetailViaGetter() {
        TestException ex = new TestException(
                "Friendly", "Technical");
        assertEquals("Technical", ex.getTechnicalDetail());
    }

    /**
     * INTENT:        The three-argument constructor preserves
     *                the cause so callers can walk the
     *                underlying exception chain.
     * PRECONDITION:  A non-null Throwable is passed as the
     *                cause.
     * POSTCONDITION: getCause() returns the same Throwable
     *                instance.
     */
    @Test
    void shouldReturnCauseWhenProvided() {
        RuntimeException cause = new RuntimeException("root cause");
        TestException ex = new TestException(
                "User msg", "Tech detail", cause);
        assertSame(cause, ex.getCause());
    }

    /**
     * INTENT:        The two-argument constructor leaves the
     *                cause chain empty rather than
     *                auto-wrapping.
     * PRECONDITION:  TestException is constructed without a
     *                cause argument.
     * POSTCONDITION: getCause() returns null.
     */
    @Test
    void shouldReturnNullCauseWhenNotProvided() {
        TestException ex = new TestException(
                "User msg", "Tech detail");
        assertNull(ex.getCause());
    }

    /**
     * INTENT:        DarkForgeException is an unchecked
     *                exception, so subclasses can propagate out
     *                of Callable batches, Future.get() unwrap
     *                sites, and SwingWorker contexts without
     *                forced try/catch wrapping.
     * PRECONDITION:  None.
     * POSTCONDITION: DarkForgeException.class.getSuperclass()
     *                is RuntimeException, and
     *                DarkForgeException is assignable to
     *                RuntimeException.
     */
    @Test
    @DisplayName("DarkForgeException is an unchecked RuntimeException")
    void shouldBeUncheckedRuntimeException() {
        assertEquals(RuntimeException.class,
                DarkForgeException.class.getSuperclass(),
                "DarkForgeException should extend "
                        + "RuntimeException directly");
        assertTrue(
                RuntimeException.class.isAssignableFrom(
                        DarkForgeException.class),
                "DarkForgeException must be a RuntimeException "
                        + "so subclasses can propagate without "
                        + "checked-exception wrapping");
    }

    /**
     * INTENT:        userMessage and technicalDetail are stored
     *                and exposed as independent strings, never
     *                collapsed into a single field.
     * PRECONDITION:  TestException is constructed with distinct
     *                userMessage and technicalDetail values.
     * POSTCONDITION: getUserMessage() and getMessage() return
     *                different strings.
     */
    @Test
    void shouldKeepUserAndTechnicalMessagesSeparate() {
        TestException ex = new TestException(
                "Player-friendly message",
                "developer.debug(): value=-1");
        assertNotEquals(ex.getUserMessage(), ex.getMessage(),
                "User message and getMessage() should differ");
    }
}
