package darkforge.mechanics;

import darkforge.exception.InvalidAttributeDistributionException;
import darkforge.model.Attribute;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class AttributeDistributorTest {

  private EnumMap<Attribute, Integer> makeAttrs(
          int str, int agl, int log,
          int per, int ins, int emp) {
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
  void shouldAcceptValidEvenDistribution()
          throws InvalidAttributeDistributionException {
    assertDoesNotThrow(() ->
            AttributeDistributor.validate(
                    makeAttrs(4, 4, 4, 4, 4, 4), Attribute.LOGIC));
  }

  @Test
  void shouldAcceptKeyAttributeAtSix()
          throws InvalidAttributeDistributionException {
    assertDoesNotThrow(() ->
            AttributeDistributor.validate(
                    makeAttrs(4, 3, 6, 3, 4, 4), Attribute.LOGIC));
  }

  @Test
  void shouldRejectTotalNotTwentyFour() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(5, 5, 5, 5, 5, 5), Attribute.LOGIC));
    assertEquals(24, ex.getExpectedTotal());
    assertEquals(30, ex.getActualTotal());
    assertTrue(ex.getUserMessage().contains("24"));
    assertTrue(ex.getUserMessage().contains("30"));
  }

  @Test
  void shouldRejectAttributeBelowTwo() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(1, 5, 5, 5, 4, 4), Attribute.LOGIC));
    assertTrue(ex.getViolations().containsKey(Attribute.STRENGTH),
            "Violations should include STRENGTH");
  }

  @Test
  void shouldRejectNonKeyAttributeAboveFive() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(2, 6, 5, 3, 4, 4), Attribute.LOGIC));
    assertTrue(ex.getViolations().containsKey(Attribute.AGILITY),
            "Violations should include AGILITY");
  }

  @Test
  void shouldRejectKeyAttributeAboveSix() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(2, 3, 7, 3, 4, 5), Attribute.LOGIC));
    assertTrue(ex.getViolations().containsKey(Attribute.LOGIC),
            "Violations should include key attribute LOGIC");
  }

  @Test
  void shouldRejectMissingAttribute() {
    EnumMap<Attribute, Integer> partial = new EnumMap<>(Attribute.class);
    partial.put(Attribute.STRENGTH, 4);
    partial.put(Attribute.AGILITY, 4);
    assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(partial, Attribute.LOGIC));
  }

  @Test
  void shouldCollectMultipleViolations() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(1, 6, 5, 4, 4, 4), Attribute.LOGIC));
    assertTrue(ex.getViolations().size() >= 2,
            "Should collect both STRENGTH (<2) and AGILITY (>5 non-key)");
  }

  @Test
  void shouldRejectExtremeValues() {
    assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(2, 2, 2, 2, 2, 14), Attribute.EMPATHY));
  }

  @Test
  void userMessageShouldBeFriendlyForTotalMismatch() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(5, 5, 5, 5, 5, 5), Attribute.LOGIC));
    assertTrue(ex.getUserMessage().contains("redistribute"),
            "User message should suggest redistribution");
    assertFalse(ex.getUserMessage().contains("AttributeDistributor"),
            "User message should not contain class names");
  }

  @Test
  void technicalDetailShouldContainClassName() {
    var ex = assertThrows(
            InvalidAttributeDistributionException.class, () ->
                    AttributeDistributor.validate(
                            makeAttrs(5, 5, 5, 5, 5, 5), Attribute.LOGIC));
    assertTrue(ex.getMessage().contains("AttributeDistributor"),
            "Technical detail should reference the class");
  }
}