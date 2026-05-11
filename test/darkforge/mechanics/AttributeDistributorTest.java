package darkforge.mechanics;

import darkforge.model.Attribute;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class AttributeDistributorTest {

  private EnumMap<Attribute, Integer> makeAttrs(int str, int agl, int log, int per, int ins, int emp) {
    EnumMap<Attribute, Integer> attrs = new EnumMap<>(Attribute.class);
    attrs.put(Attribute.STRENGTH, str);
    attrs.put(Attribute.AGILITY, agl);
    attrs.put(Attribute.LOGIC, log);
    attrs.put(Attribute.PERCEPTION, per);
    attrs.put(Attribute.INSIGHT, ins);
    attrs.put(Attribute.EMPATHY, emp);
    return attrs;
  }

  @Test
  void shouldAcceptValidEvenDistribution() {
    assertDoesNotThrow(() -> AttributeDistributor.validate(makeAttrs(4, 4, 4, 4, 4, 4), Attribute.LOGIC));
  }

  @Test
  void shouldAcceptKeyAttributeAtSix() {
    assertDoesNotThrow(() -> AttributeDistributor.validate(makeAttrs(4, 3, 6, 3, 4, 4), Attribute.LOGIC));
  }

  @Test
  void shouldRejectTotalNotTwentyFour() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> AttributeDistributor.validate(makeAttrs(5, 5, 5, 5, 5, 5), Attribute.LOGIC));
    assertTrue(ex.getMessage().contains("30"));
  }

  @Test
  void shouldRejectAttributeBelowTwo() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> AttributeDistributor.validate(makeAttrs(1, 5, 5, 5, 4, 4), Attribute.LOGIC));
    assertTrue(ex.getMessage().contains("STR"));
  }

  @Test
  void shouldRejectNonKeyAttributeAboveFive() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> AttributeDistributor.validate(makeAttrs(2, 6, 5, 3, 4, 4), Attribute.LOGIC));
    assertTrue(ex.getMessage().contains("AGL"));
  }

  @Test
  void shouldRejectKeyAttributeAboveSix() {
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> AttributeDistributor.validate(makeAttrs(2, 3, 7, 3, 4, 5), Attribute.LOGIC));
    assertTrue(ex.getMessage().contains("LOG"));
  }

  @Test
  void shouldRejectMissingAttribute() {
    EnumMap<Attribute, Integer> partial = new EnumMap<>(Attribute.class);
    partial.put(Attribute.STRENGTH, 4);
    partial.put(Attribute.AGILITY, 4);
    assertThrows(IllegalArgumentException.class, () -> AttributeDistributor.validate(partial, Attribute.LOGIC));
  }

  @Test
  void shouldAcceptMinimumViableDistribution() {
    assertThrows(IllegalArgumentException.class,
        () -> AttributeDistributor.validate(makeAttrs(2, 2, 2, 2, 2, 14), Attribute.EMPATHY));
  }
}
