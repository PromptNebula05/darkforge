package darkforge.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OriginTest {

  private Talent dummyTalent() {
    return new Talent("TestTalent", "desc", TalentCategory.KNOWLEDGE, 3, 1, "effect");
  }

  private Origin validOrigin(int low, int high) {
    return new Origin("Test Location", dummyTalent(), "Test Faction", "Test Contact", low, high);
  }

  @Test
  void shouldCreateWithValidD66Range() {
    Origin origin = validOrigin(11, 12);
    assertEquals("Test Location", origin.getLocation());
    assertEquals("Test Faction", origin.getAssociatedFaction());
    assertEquals("Test Contact", origin.getContact());
    assertNotNull(origin.getFreeTalent());
  }

  @Test
  void shouldRejectInvalidD66Digits() {
    assertThrows(IllegalArgumentException.class, () -> validOrigin(17, 22),
        "Ones digit > 6 should be rejected");
    assertThrows(IllegalArgumentException.class, () -> validOrigin(70, 72),
        "Tens digit > 6 should be rejected");
    assertThrows(IllegalArgumentException.class, () -> validOrigin(0, 11),
        "Zero should be rejected");
  }

  @Test
  void shouldRejectLowGreaterThanHigh() {
    assertThrows(IllegalArgumentException.class, () -> validOrigin(33, 11));
  }

  @Test
  void shouldMatchD66WithinRange() {
    Origin origin = validOrigin(21, 24);
    assertTrue(origin.matchesD66(21), "Low bound should match");
    assertTrue(origin.matchesD66(22), "Mid value should match");
    assertTrue(origin.matchesD66(24), "High bound should match");
  }

  @Test
  void shouldNotMatchD66OutsideRange() {
    Origin origin = validOrigin(21, 24);
    assertFalse(origin.matchesD66(15), "Below range should not match");
    assertFalse(origin.matchesD66(25), "Above range should not match");
  }
}
