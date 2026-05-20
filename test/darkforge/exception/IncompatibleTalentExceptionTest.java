package darkforge.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IncompatibleTalentExceptionTest {

    @Test
    void shouldStoreTalentAndProfessionNames() {
        var ex = new IncompatibleTalentException(
                "Laser Eyes", "Scholar",
                "Not a valid Scholar talent",
                List.of("Investigator", "Librarian", "Smart", "Teratology"));

        assertEquals("Laser Eyes", ex.getTalentName());
        assertEquals("Scholar", ex.getProfessionName());
        assertEquals("Not a valid Scholar talent", ex.getReason());
    }

    @Test
    void userMessageShouldIncludeTalentNameAndAvailableList() {
        var ex = new IncompatibleTalentException(
                "Blade Fighter", "Scholar",
                "Combat talents not available for Scholar",
                List.of("Investigator", "Librarian"));

        assertTrue(ex.getUserMessage().contains("Blade Fighter"));
        assertTrue(ex.getUserMessage().contains("Investigator"));
        assertTrue(ex.getUserMessage().contains("Librarian"));
    }

    @Test
    void shouldReturnAvailableTalentsList() {
        List<String> available = List.of("A", "B", "C");
        var ex = new IncompatibleTalentException(
                "X", "Y", "reason", available);

        assertEquals(3, ex.getAvailableTalents().size());
        assertEquals(available, ex.getAvailableTalents());
    }

    @Test
    void availableTalentsListShouldBeUnmodifiable() {
        var ex = new IncompatibleTalentException(
                "X", "Y", "reason", List.of("A", "B"));

        assertThrows(UnsupportedOperationException.class, () ->
                ex.getAvailableTalents().add("Hacked"));
    }

    @Test
    void technicalDetailShouldContainClassName() {
        var ex = new IncompatibleTalentException(
                "X", "Y", "reason", List.of("A"));
        assertTrue(ex.getMessage().contains("IncompatibleTalentException"));
    }
}