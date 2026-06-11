package darkforge.creation;

import darkforge.data.GameDataProvider;
import darkforge.exception.*;
import darkforge.model.*;
import darkforge.model.profession.*;
import darkforge.mechanics.D66Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Updated for Iteration 2: exception types changed from
 * IllegalArgumentException to domain exceptions.
 * Tests now verify structured exception data.
 */
class ExplorerFactoryTest {

  private final ExplorerFactory factory = new ExplorerFactory();

  @BeforeEach
  void setUp() {
    GameDataProvider.getTheInstance().initialize();
  }

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

  /**
   * Build a talent points array sized to the profession's
   * actual key talent count (from the Explorer subclass,
   * not ProfessionData), distributing up to 3 points.
   */
  private int[] talentPointsFor(String profession)
          throws InvalidProfessionException {
    Explorer temp = factory.createProfessionInstance(profession);
    int count = temp.getKeyTalents().size();
    int[] pts = new int[count];
    for (int i = 0; i < Math.min(count, 3); i++) {
      pts[i] = 1;
    }
    return pts;
  }

  // ── Core Creation Tests ────────────────────────────────────────

  @Test
  void shouldCreateAllEightProfessions() throws DarkForgeException {
    String[] professions = {
            "Scholar", "Enforcer", "Artist", "Esoteric",
            "Odd Jobber", "Roughneck", "Scoundrel", "Traveler"
    };
    Origin origin = GameDataProvider.getTheInstance()
            .getOrigins().get(0);
    for (String profession : professions) {
      Explorer explorer = factory.createExplorer(
              profession, origin, 0,
              safeAttrs(), talentPointsFor(profession),
              "quirk", "keepsake", "appearance",
              "Test " + profession
      );
      assertNotNull(explorer, profession + " should be created");
      assertEquals("Test " + profession, explorer.getName());
      assertNotNull(explorer.getKeyAttribute(),
              profession + " should have a key attribute");
      assertNotNull(explorer.getOrigin(),
              profession + " should have an origin");
      assertNotNull(explorer.getSpecialty(),
              profession + " should have a specialty");
      assertFalse(explorer.getTalents().isEmpty(),
              profession + " should have talents");
    }
  }

  @Test
  void shouldRejectUnknownProfession() {
    Origin origin = GameDataProvider.getTheInstance()
            .getOrigins().get(0);
    var ex = assertThrows(InvalidProfessionException.class, () ->
            factory.createExplorer(
                    "Wizard", origin, 0,
                    validScholarAttrs(), new int[]{1, 1, 1},
                    "q", "k", "a", "Test"
            ));
    assertEquals("Wizard", ex.getAttemptedProfession());
    assertTrue(ex.getValidProfessions().contains("Scholar"));
  }

  @Test
  void shouldRejectInvalidAttributes() throws InvalidProfessionException {
    Origin origin = GameDataProvider.getTheInstance()
            .getOrigins().get(0);
    EnumMap<Attribute, Integer> badAttrs = new EnumMap<>(Attribute.class);
    badAttrs.put(Attribute.STRENGTH, 5);
    badAttrs.put(Attribute.AGILITY, 5);
    badAttrs.put(Attribute.LOGIC, 5);
    badAttrs.put(Attribute.PERCEPTION, 5);
    badAttrs.put(Attribute.INSIGHT, 5);
    badAttrs.put(Attribute.EMPATHY, 5);
    assertThrows(InvalidAttributeDistributionException.class, () ->
            factory.createExplorer(
                    "Scholar", origin, 0,
                    badAttrs, talentPointsFor("Scholar"),
                    "q", "k", "a", "Test"
            ));
  }

  @Test
  void shouldRejectWrongTalentPointTotal() throws InvalidProfessionException {
    Origin origin = GameDataProvider.getTheInstance()
            .getOrigins().get(0);
    Explorer temp = factory.createProfessionInstance("Scholar");
    int count = temp.getKeyTalents().size();
    int[] badPoints = new int[count];
    for (int i = 0; i < count; i++) {
      badPoints[i] = 2;
    }
    assertThrows(IncompatibleTalentException.class, () ->
            factory.createExplorer(
                    "Scholar", origin, 0,
                    validScholarAttrs(), badPoints,
                    "q", "k", "a", "Test"
            ));
  }

  // ── D66 Tables (via GameDataProvider) ──────────────────────────

  @Test
  void quirksShouldBeAvailable() {
    var quirks = GameDataProvider.getTheInstance().getQuirks();
    assertNotNull(quirks);
    assertFalse(quirks.isEmpty(),
            "Quirks list should not be empty");
  }

  @Test
  void keepsakesShouldBeAvailable() {
    var keepsakes = GameDataProvider.getTheInstance().getKeepsakes();
    assertNotNull(keepsakes);
    assertFalse(keepsakes.isEmpty(),
            "Keepsakes list should not be empty");
  }

  @Test
  void appearancesShouldBeAvailable() {
    var appearances = GameDataProvider.getTheInstance().getAppearances();
    assertNotNull(appearances);
    assertFalse(appearances.isEmpty(),
            "Appearances list should not be empty");
  }

  @Test
  void explorerReasonsShouldBeAvailable() {
    var reasons = GameDataProvider.getTheInstance().getExplorerReasons();
    assertNotNull(reasons);
    assertFalse(reasons.isEmpty(),
            "Explorer reasons list should not be empty");
  }

  // ── Origins (via GameDataProvider) ─────────────────────────────

  @Test
  void originsShouldCoverFullD66Range() {
    Set<Integer> validKeys = D66Table.getValidKeys();
    GameDataProvider data = GameDataProvider.getTheInstance();
    for (int key : validKeys) {
      assertDoesNotThrow(
              () -> data.getOriginByD66(key),
              "D66 value " + key + " should match an origin");
    }
  }

  @Test
  void originsShouldHave13Entries() {
    assertEquals(13,
            GameDataProvider.getTheInstance()
                    .getOrigins().size());
  }

  @Test
  void getOriginByD66ShouldRejectInvalidValue() {
    assertThrows(IllegalArgumentException.class,
            () -> GameDataProvider.getTheInstance()
                    .getOriginByD66(77));
  }

  @Test
  void eachOriginShouldHaveNonNullFreeTalent() {
    for (Origin origin : GameDataProvider.getTheInstance()
            .getOrigins()) {
      assertNotNull(origin.getFreeTalent(),
              "Origin '" + origin.getLocation()
                      + "' should have a free talent");
    }
  }

  // ── Explorer Flavor Fields ────────────────────────────────────────

  @Test
  void factoryShouldPopulateAllFlavorFields()
          throws DarkForgeException {
    Origin origin = GameDataProvider.getTheInstance()
            .getOrigins().get(0);
    Explorer explorer = factory.createExplorer(
            "Scholar", origin, 0,
            safeAttrs(), talentPointsFor("Scholar"),
            "My quirk", "My keepsake", "My appearance",
            "Test"
    );
    assertEquals("My quirk", explorer.getQuirk());
    assertEquals("My keepsake", explorer.getKeepsake());
    assertEquals("My appearance", explorer.getAppearance());
    assertNotNull(explorer.getResolvedContact(),
            "Contact should be resolved via D6 roll");
    assertNotNull(explorer.getResolvedFaction(),
            "Faction should be resolved");
    assertNotNull(explorer.getExplorerReason(),
            "Explorer reason should be set via D66 roll");
  }

  @Test
  void factoryShouldRollQuirkWhenNull()
          throws DarkForgeException {
    Origin origin = GameDataProvider.getTheInstance()
            .getOrigins().get(0);
    Explorer explorer = factory.createExplorer(
            "Scholar", origin, 0,
            safeAttrs(), talentPointsFor("Scholar"),
            null, null, null, "Test"
    );
    assertNotNull(explorer.getQuirk(),
            "Null quirk should trigger D66 roll");
    assertFalse(explorer.getQuirk().isBlank());
    assertNotNull(explorer.getKeepsake(),
            "Null keepsake should trigger D66 roll");
    assertNotNull(explorer.getAppearance(),
            "Null appearance should trigger D66 roll");
  }
}