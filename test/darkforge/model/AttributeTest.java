package darkforge.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

  @Test
  void shouldHaveExactlySixValues() {
    assertEquals(6, Attribute.values().length);
  }

  @Test
  void eachAttributeShouldHaveNonBlankDisplayNameAndAbbreviation() {
    for (Attribute attr : Attribute.values()) {
      assertFalse(attr.getDisplayName().isBlank(),
          attr + " should have a non-blank display name");
      assertEquals(3, attr.getAbbreviation().length(),
          attr + " abbreviation should be exactly 3 characters");
      assertEquals(attr, Attribute.valueOf(attr.name()),
          "valueOf(name()) should round-trip for " + attr);
    }
  }
}
