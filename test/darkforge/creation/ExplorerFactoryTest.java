package darkforge.creation;

import darkforge.model.*;
import darkforge.model.profession.*;
import darkforge.mechanics.D66Table;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ExplorerFactoryTest {

  private final ExplorerFactory factory = new ExplorerFactory();

  // ── Core Creation Tests ────────────────────────────────────────

  private EnumMap<Attribute, Integer> validScholarAttrs() {
    EnumMap<Attribute, Integer> attrs = new EnumMap<>(Attribute.class);
    attrs.put(Attribute.STRENGTH, 2);
    attrs.put(Attribute.AGILITY, 4);
    attrs.put(Attribute.LOGIC, 6);
    attrs.put(Attribute.PERCEPTION, 3);
    attrs.put(Attribute.INSIGHT, 4);
    attrs.put(Attribute.EMPATHY, 5);
    return attrs;
  }

  private EnumMap<Attribute, Integer> safeAttrs() {
    EnumMap<Attribute, Integer> attrs = new EnumMap<>(Attribute.class);
    for (Attribute attr : Attribute.values()) {
      attrs.put(attr, 4);
    }
    return attrs;
  }

  @Test
  void shouldCreateAllEightProfessions() {
    String[] professions = {
        "Scholar", "Enforcer", "Artist", "Esoteric",
        "Odd Jobber", "Roughneck", "Scoundrel", "Traveler"
    };
    for (String profession : professions) {
      Explorer explorer = factory.createExplorer(
          profession, "Test " + profession, 1, 1,
          safeAttrs(), new int[] { 1, 1, 1, 0 },
          "quirk", "keepsake", "appearance");
      assertNotNull(explorer, profession + " should be created");
      assertEquals("Test " + profession, explorer.getName());
      assertNotNull(explorer.getKeyAttribute(), profession + " should have a key attribute");
      assertNotNull(explorer.getOrigin(), profession + " should have an origin");
      assertNotNull(explorer.getSpecialty(), profession + " should have a specialty");
      assertFalse(explorer.getTalents().isEmpty(), profession + " should have talents");
    }
  }

  @Test
  void shouldRejectUnknownProfession() {
    assertThrows(IllegalArgumentException.class, () -> factory.createExplorer(
        "Wizard", "Test", 1, 1,
        validScholarAttrs(), new int[] { 1, 1, 1, 0 },
        "q", "k", "a"));
  }

  @Test
  void shouldRejectInvalidAttributes() {
    EnumMap<Attribute, Integer> badAttrs = new EnumMap<>(Attribute.class);
    badAttrs.put(Attribute.STRENGTH, 5);
    badAttrs.put(Attribute.AGILITY, 5);
    badAttrs.put(Attribute.LOGIC, 5);
    badAttrs.put(Attribute.PERCEPTION, 5);
    badAttrs.put(Attribute.INSIGHT, 5);
    badAttrs.put(Attribute.EMPATHY, 5);
    assertThrows(IllegalArgumentException.class, () -> factory.createExplorer(
        "Scholar", "Test", 1, 1,
        badAttrs, new int[] { 1, 1, 1, 0 },
        "q", "k", "a"));
  }

  @Test
  void shouldRejectWrongTalentPointTotal() {
    assertThrows(IllegalArgumentException.class, () -> factory.createExplorer(
        "Scholar", "Test", 1, 1,
        validScholarAttrs(), new int[] { 2, 2, 2, 0 },
        "q", "k", "a"));
  }

  @Test
  void shouldRejectInvalidOriginIndex() {
    assertThrows(IllegalArgumentException.class, () -> factory.createExplorer(
        "Scholar", "Test", 99, 1,
        validScholarAttrs(), new int[] { 1, 1, 1, 0 },
        "q", "k", "a"));
  }

  @Test
  void shouldRejectInvalidSpecialtyIndex() {
    assertThrows(IllegalArgumentException.class, () -> factory.createExplorer(
        "Scholar", "Test", 1, 99,
        validScholarAttrs(), new int[] { 1, 1, 1, 0 },
        "q", "k", "a"));
  }

  // ── D66 Quirks Table ──────────────────────────────────────────────

  @Test
  void quirkTableShouldHave36Entries() {
    assertEquals(36, ExplorerFactory.getQuirksTable().size());
  }

  @Test
  void quirkTableShouldReturnNonBlankForAllValidKeys() {
    D66Table<String> table = ExplorerFactory.getQuirksTable();
    for (int key : D66Table.getValidKeys()) {
      String quirk = table.getResult(key);
      assertNotNull(quirk, "Quirk for D66=" + key + " should not be null");
      assertFalse(quirk.isBlank(), "Quirk for D66=" + key + " should not be blank");
    }
  }

  @Test
  void getQuirkByD66ShouldMatchTableLookup() {
    assertEquals(
        ExplorerFactory.getQuirksTable().getResult(43),
        ExplorerFactory.getQuirkByD66(43));
  }

  @Test
  void getQuirkByD66ShouldRejectInvalidKey() {
    assertThrows(IllegalArgumentException.class, () -> ExplorerFactory.getQuirkByD66(77));
  }

  // ── D66 Keepsakes Table ───────────────────────────────────────────

  @Test
  void keepsakeTableShouldHave36Entries() {
    assertEquals(36, ExplorerFactory.getKeepsakesTable().size());
  }

  @Test
  void keepsakeTableShouldReturnNonBlankForAllValidKeys() {
    D66Table<String> table = ExplorerFactory.getKeepsakesTable();
    for (int key : D66Table.getValidKeys()) {
      String keepsake = table.getResult(key);
      assertNotNull(keepsake, "Keepsake for D66=" + key + " should not be null");
      assertFalse(keepsake.isBlank(), "Keepsake for D66=" + key + " should not be blank");
    }
  }

  @Test
  void getKeepsakeByD66ShouldMatchTableLookup() {
    assertEquals(
        ExplorerFactory.getKeepsakesTable().getResult(25),
        ExplorerFactory.getKeepsakeByD66(25));
  }

  // ── D66 Appearances Table ─────────────────────────────────────────

  @Test
  void appearanceTableShouldHave36Entries() {
    assertEquals(36, ExplorerFactory.getAppearancesTable().size());
  }

  @Test
  void appearanceTableShouldReturnNonBlankForAllValidKeys() {
    D66Table<String> table = ExplorerFactory.getAppearancesTable();
    for (int key : D66Table.getValidKeys()) {
      String appearance = table.getResult(key);
      assertNotNull(appearance, "Appearance for D66=" + key + " should not be null");
      assertFalse(appearance.isBlank(), "Appearance for D66=" + key + " should not be blank");
    }
  }

  @Test
  void getAppearanceByD66ShouldMatchTableLookup() {
    assertEquals(
        ExplorerFactory.getAppearancesTable().getResult(31),
        ExplorerFactory.getAppearanceByD66(31));
  }

  // ── D66 Explorer Reason Table ─────────────────────────────────────

  @Test
  void explorerReasonTableShouldHave36Entries() {
    assertEquals(36, ExplorerFactory.getExplorerReasonTable().size());
  }

  @Test
  void explorerReasonTableShouldReturnNonBlankForAllValidKeys() {
    D66Table<String> table = ExplorerFactory.getExplorerReasonTable();
    for (int key : D66Table.getValidKeys()) {
      String reason = table.getResult(key);
      assertNotNull(reason, "Reason for D66=" + key + " should not be null");
      assertFalse(reason.isBlank(), "Reason for D66=" + key + " should not be blank");
    }
  }

  @Test
  void getExplorerReasonByD66ShouldMatchTableLookup() {
    assertEquals(
        ExplorerFactory.getExplorerReasonTable().getResult(55),
        ExplorerFactory.getExplorerReasonByD66(55));
  }

  // ── Origins Table ─────────────────────────────────────────────────

  @Test
  void defaultOriginsShouldCoverFullD66Range() {
    Set<Integer> validKeys = D66Table.getValidKeys();
    for (int key : validKeys) {
      assertDoesNotThrow(() -> ExplorerFactory.getOriginByD66(key),
          "D66 value " + key + " should match an origin");
    }
  }

  @Test
  void defaultOriginsShouldHave13Entries() {
    assertEquals(13, ExplorerFactory.getDefaultOrigins().size());
  }

  @Test
  void getOriginByD66ShouldRejectInvalidValue() {
    assertThrows(IllegalArgumentException.class,
        () -> ExplorerFactory.getOriginByD66(77));
  }

  @Test
  void eachOriginShouldHaveNonNullFreeTalent() {
    for (Origin origin : ExplorerFactory.getDefaultOrigins()) {
      assertNotNull(origin.getFreeTalent(),
          "Origin '" + origin.getLocation() + "' should have a free talent");
    }
  }

  // ── Weapon-Talent-Aware Factory Overload ──────────────────────────

  @Test
  void shouldCreateEnforcerWithCustomWeaponTalent() {
    Explorer explorer = factory.createExplorer(
        "Enforcer", "Kaan Verros", 1, 1,
        safeAttrs(), new int[] { 1, 1, 1, 0 },
        "quirk", "keepsake", "appearance", "Pistoleer");
    assertNotNull(explorer);
    assertTrue(explorer instanceof Enforcer);
    Enforcer enforcer = (Enforcer) explorer;
    assertEquals("Pistoleer", enforcer.getChosenWeaponTalent().getName());
  }

  @Test
  void shouldCreateEnforcerWithDefaultWeaponWhenNull() {
    Explorer explorer = factory.createExplorer(
        "Enforcer", "Test", 1, 1,
        safeAttrs(), new int[] { 1, 1, 1, 0 },
        "quirk", "keepsake", "appearance", null);
    Enforcer enforcer = (Enforcer) explorer;
    assertEquals("Sharpshooter", enforcer.getChosenWeaponTalent().getName());
  }

  // ── Explorer Flavor Fields ────────────────────────────────────────

  @Test
  void factoryShouldPopulateAllFlavorFields() {
    Explorer explorer = factory.createExplorer(
        "Scholar", "Test", 1, 1,
        safeAttrs(), new int[] { 1, 1, 1, 0 },
        "My quirk", "My keepsake", "My appearance");
    assertEquals("My quirk", explorer.getQuirk());
    assertEquals("My keepsake", explorer.getKeepsake());
    assertEquals("My appearance", explorer.getAppearance());
    assertNotNull(explorer.getResolvedContact(), "Contact should be resolved via D6 roll");
    assertNotNull(explorer.getResolvedFaction(), "Faction should be resolved");
    assertNotNull(explorer.getExplorerReason(), "Explorer reason should be set via D66 roll");
  }

  @Test
  void factoryShouldRollQuirkWhenNull() {
    Explorer explorer = factory.createExplorer(
        "Scholar", "Test", 1, 1,
        safeAttrs(), new int[] { 1, 1, 1, 0 },
        null, null, null);
    assertNotNull(explorer.getQuirk(), "Null quirk should trigger D66 roll");
    assertFalse(explorer.getQuirk().isBlank());
    assertNotNull(explorer.getKeepsake(), "Null keepsake should trigger D66 roll");
    assertNotNull(explorer.getAppearance(), "Null appearance should trigger D66 roll");
  }
}
