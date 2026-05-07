package darkforge.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

    @Test
    void shouldHaveExactlySixAttributes() {
        assertEquals(6, Attribute.values().length,
                "Coriolis defines exactly 6 attributes - guard against accidental addition/removal");
    }

    @Test
    void eachAttributeShouldHaveCorrectAbbreviations() {
        assertAll("abbreviations must match Coriolis rulebook (Ch. 2",
                () -> assertEquals("STR", Attribute.STRENGTH.getAbbreviation()),
                () -> assertEquals("AGL", Attribute.AGILITY.getAbbreviation()),
                () -> assertEquals("LOG", Attribute.LOGIC.getAbbreviation()),
                () -> assertEquals("PER", Attribute.PERCEPTION.getAbbreviation()),
                () -> assertEquals("INS", Attribute.INSIGHT.getAbbreviation()),
                () -> assertEquals("EMP", Attribute.EMPATHY.getAbbreviation())
        );
    }

    @Test
    void valueOfShouldRoundTripForAllConstants() {
        for (Attribute attr : Attribute.values()) {
            assertEquals(attr, Attribute.valueOf(attr.name()),
                    "valueOf(name()) should return the same constant for " + attr);
        }
    }
}