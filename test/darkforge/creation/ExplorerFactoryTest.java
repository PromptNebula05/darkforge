package darkforge.creation;

import darkforge.model.*;
import darkforge.model.profession.*;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class ExplorerFactoryTest {

  private final ExplorerFactory factory = new ExplorerFactory();

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
}
