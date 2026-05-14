package darkforge.exception;

import darkforge.model.Attribute;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvalidAttributeDistributionExceptionTest {

    @Test
    void shouldReportTotalMismatch() {
        Map<Attribute, String> violations = new EnumMap<>(Attribute.class);
        var ex = new InvalidAttributeDistributionException(
                24, 26, violations.isEmpty()
                ? Map.of(Attribute.STRENGTH, "total mismatch")
                : violations);

        assertEquals(24, ex.getExpectedTotal());
        assertEquals(26, ex.getActualTotal());
        assertTrue(ex.getUserMessage().contains("24"));
        assertTrue(ex.getUserMessage().contains("26"));
        assertTrue(ex.getUserMessage().contains("redistribute"),
                "User message should suggest redistribution");
    }

    @Test
    void shouldReportSingleViolation() {
        Map<Attribute, String> violations = Map.of(
                Attribute.LOGIC, "key attribute max is 6, got 7");
        var ex = new InvalidAttributeDistributionException(
                24, 24, violations);

        assertTrue(ex.getViolations().containsKey(Attribute.LOGIC));
        assertEquals(1, ex.getViolations().size());
    }

    @Test
    void shouldReportMultipleViolations() {
        Map<Attribute, String> violations = new EnumMap<>(Attribute.class);
        violations.put(Attribute.STRENGTH, "must be at least 2, got 1");
        violations.put(Attribute.AGILITY, "non-key max is 5, got 6");
        var ex = new InvalidAttributeDistributionException(
                24, 24, violations);

        assertEquals(2, ex.getViolations().size());
        assertTrue(ex.getViolations().containsKey(Attribute.STRENGTH));
        assertTrue(ex.getViolations().containsKey(Attribute.AGILITY));
    }

    @Test
    void userMessageShouldBeFriendly() {
        var ex = new InvalidAttributeDistributionException(
                24, 28, Map.of(Attribute.STRENGTH, "too high"));
        String msg = ex.getUserMessage();
        assertFalse(msg.contains("AttributeDistributor"),
                "User message should not contain class names");
        assertFalse(msg.contains("Exception"),
                "User message should not contain 'Exception'");
    }

    @Test
    void technicalDetailShouldContainClassName() {
        var ex = new InvalidAttributeDistributionException(
                24, 26, Map.of(Attribute.LOGIC, "violation"));
        assertTrue(ex.getMessage().contains("AttributeDistributor"),
                "Technical detail should reference the class");
    }

    @Test
    void violationsMapShouldBeUnmodifiable() {
        Map<Attribute, String> violations = new EnumMap<>(Attribute.class);
        violations.put(Attribute.LOGIC, "violation");
        var ex = new InvalidAttributeDistributionException(24, 24, violations);

        assertThrows(UnsupportedOperationException.class, () ->
                ex.getViolations().put(Attribute.STRENGTH, "hacked"));
    }
}