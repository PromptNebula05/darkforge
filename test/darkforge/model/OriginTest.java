package darkforge.model;

import darkforge.mechanics.D6Table;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class OriginTest {

  private Talent dummyTalent() {
    return new Talent("TestTalent", "desc", TalentCategory.KNOWLEDGE, 3, 1, "effect");
  }

  /**
   * Helper: builds a valid D6 contacts map with three paired entries (1-2, 3-4,
   * 5-6).
   */
  private Map<Integer, String> dummyContacts() {
    return Map.of(
        1, "Contact A", 2, "Contact A",
        3, "Contact B", 4, "Contact B",
        5, "Contact C", 6, "Contact C");
  }

  private Origin validOrigin(int low, int high) {
    return new Origin("Test Location", dummyTalent(), "Test Faction",
        dummyContacts(), low, high);
  }

  @Test
  void shouldCreateWithValidD66Range() {
    Origin origin = validOrigin(11, 12);
    assertEquals("Test Location", origin.getLocation());
    assertEquals("Test Faction", origin.getAssociatedFaction());
    assertFalse(origin.hasVariableFaction());
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

  @Test
  void shouldReturnContactByD6Value() {
    Origin origin = validOrigin(11, 12);
    assertEquals("Contact A", origin.getContact(1));
    assertEquals("Contact A", origin.getContact(2));
    assertEquals("Contact B", origin.getContact(3));
    assertEquals("Contact C", origin.getContact(5));
  }

  @Test
  void shouldRejectInvalidD6ContactValue() {
    Origin origin = validOrigin(11, 12);
    assertThrows(IllegalArgumentException.class, () -> origin.getContact(0));
    assertThrows(IllegalArgumentException.class, () -> origin.getContact(7));
  }

  @Test
  void shouldRejectIncompleteContactsMap() {
    Map<Integer, String> incomplete = Map.of(1, "A", 2, "A", 3, "B");
    assertThrows(IllegalArgumentException.class,
        () -> new Origin("Test", dummyTalent(), "Faction", incomplete, 11, 11));
  }

  @Test
  void shouldSupportVariableFactionViaD6Table() {
    D6Table<String> factionTable = new D6Table<>(Map.of(
        1, "Faction A", 2, "Faction A", 3, "Faction B",
        4, "Faction B", 5, "Faction C", 6, "Faction C"));
    Origin origin = new Origin("Variable Place", dummyTalent(), factionTable,
        dummyContacts(), 11, 11);
    assertTrue(origin.hasVariableFaction());
    assertThrows(IllegalStateException.class, origin::getAssociatedFaction,
        "Fixed faction accessor should throw for variable-faction origins");
    assertNotNull(origin.getFaction(1), "getFaction(d6) should return a value");
  }
}
