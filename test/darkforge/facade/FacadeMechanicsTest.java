package darkforge.facade;

import darkforge.model.Attribute;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeMechanics sub-facade.
 * Verifies dice rolling delegation and attribute validation
 * pass-through to the mechanics layer.
 */
class FacadeMechanicsTest {

    private final FacadeMechanics facade =
            FacadeDarkforge.getTheInstance()
                    .mechanicsAccess();

    @Test
    void shouldRollDicePool() {
        int[] result = facade.rollDicePool(3, 2);
        assertNotNull(result);
        assertTrue(result.length > 0,
                "3 base + 2 gear should yield dice results");
    }

    @Test
    void shouldRollDicePoolWithZeroDice() {
        int[] result = facade.rollDicePool(0, 0);
        assertNotNull(result);
    }

    @Test
    void shouldRejectNegativeBaseDice() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.rollDicePool(-1, 0));
    }

    @Test
    void shouldRejectNegativeGearDice() {
        assertThrows(IllegalArgumentException.class,
                () -> facade.rollDicePool(0, -1));
    }

    @Test
    void shouldReturnD66InValidRange() {
        int d66 = facade.rollD66();
        int tens = d66 / 10;
        int ones = d66 % 10;
        assertTrue(tens >= 1 && tens <= 6,
                "Tens digit out of range: " + d66);
        assertTrue(ones >= 1 && ones <= 6,
                "Ones digit out of range: " + d66);
    }

    @Test
    void shouldValidateAttributesWithoutException() {
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) {
            attrs.put(a, 4);
        }
        assertDoesNotThrow(() ->
                facade.validateAttributes(
                        attrs, Attribute.LOGIC));
    }

    @Test
    void shouldRejectInvalidAttributeTotal() {
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) {
            attrs.put(a, 5);
        }
        assertThrows(Exception.class, () ->
                facade.validateAttributes(
                        attrs, Attribute.LOGIC));
    }

    @Test
    void shouldParseD66Table() {
        var result = facade.parseD66Table(
                "11  Entry A\n12  Entry B");
        assertEquals(2, result.size());
        assertEquals("Entry A", result.get(11));
    }
}