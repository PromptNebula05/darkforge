package darkforge.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvalidProfessionExceptionTest {

    private static final List<String> VALID = List.of(
            "Scholar", "Enforcer", "Artist", "Esoteric",
            "OddJobber", "Roughneck", "Scoundrel", "Traveler");

    @Test
    void shouldStoreAttemptedProfession() {
        var ex = new InvalidProfessionException("Wizard", VALID);
        assertEquals("Wizard", ex.getAttemptedProfession());
    }

    @Test
    void shouldStoreValidProfessionsList() {
        var ex = new InvalidProfessionException("Wizard", VALID);
        assertEquals(8, ex.getValidProfessions().size());
        assertTrue(ex.getValidProfessions().contains("Scholar"));
    }

    @Test
    void userMessageShouldListValidProfessions() {
        var ex = new InvalidProfessionException("Wizard", VALID);
        String msg = ex.getUserMessage();
        assertTrue(msg.contains("Wizard"));
        assertTrue(msg.contains("Scholar"));
        assertTrue(msg.contains("Enforcer"));
    }

    @Test
    void userMessageShouldNotContainTechnicalJargon() {
        var ex = new InvalidProfessionException("Wizard", VALID);
        assertFalse(ex.getUserMessage().contains("Exception"));
        assertFalse(ex.getUserMessage().contains("InvalidProfession"));
    }

    @Test
    void technicalDetailShouldContainClassName() {
        var ex = new InvalidProfessionException("Wizard", VALID);
        assertTrue(ex.getMessage().contains("InvalidProfessionException"));
    }

    @Test
    void validProfessionsListShouldBeUnmodifiable() {
        var ex = new InvalidProfessionException("Wizard", VALID);
        assertThrows(UnsupportedOperationException.class, () ->
                ex.getValidProfessions().add("Hacked"));
    }
}