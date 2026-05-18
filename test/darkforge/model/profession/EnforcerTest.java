package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EnforcerTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnAgilityAsKeyAttribute() {
    assertEquals(Attribute.AGILITY,
            new Enforcer("Test")
                    .getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Enforcer",
            new Enforcer("Test")
                    .getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Enforcer e = new Enforcer("Test");
    assertEquals(4, e.getKeyTalents().size(),
            "Enforcer has 4 key talents: "
                    + "Sharpshooter (default weapon "
                    + "talent), Commander, Evasive, "
                    + "Medic");
    assertEquals(6, e.getSpecialties().size(),
            "Enforcer has 6 specialties "
                    + "per Ch. 2");
    assertEquals(3,
            e.getStartingEquipmentSets().size(),
            "Enforcer has 3 starting "
                    + "equipment sets per Ch. 2");
    for (var set : e.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(),
              "Equipment set should not "
                      + "be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Enforcer e = new Enforcer("Test");
    List<String> names = e.getKeyTalents()
            .stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Sharpshooter"),
            "Key talents should include "
                    + "Sharpshooter (default weapon "
                    + "talent)");
    assertTrue(names.contains("Commander"),
            "Key talents should include "
                    + "Commander");
    assertTrue(names.contains("Evasive"),
            "Key talents should include "
                    + "Evasive");
    assertTrue(names.contains("Medic"),
            "Key talents should include "
                    + "Medic");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Enforcer e = new Enforcer("Test");
    Map<String, TalentCategory> expected =
            Map.of(
                    "Sharpshooter",
                    TalentCategory.COMBAT,
                    "Commander",
                    TalentCategory.RECOVERY,
                    "Evasive",
                    TalentCategory.COMBAT,
                    "Medic",
                    TalentCategory.RECOVERY
            );
    for (Talent t : e.getKeyTalents()) {
      TalentCategory expectedCat =
              expected.get(t.getName());
      assertNotNull(expectedCat,
              "Unexpected key talent: "
                      + t.getName());
      assertEquals(expectedCat,
              t.getCategory(),
              t.getName()
                      + " should have category "
                      + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Enforcer e = new Enforcer("Test");
    List<String> names = e.getSpecialties()
            .stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains(
            "Zapti Constable"));
    assertTrue(names.contains(
            "Guild Militia"));
    assertTrue(names.contains(
            "Fusillard Protector"));
    assertTrue(names.contains(
            "Guild Investigator"));
    assertTrue(names.contains(
            "Coriolite Guard"));
    assertTrue(names.contains(
            "Bounty Hunter"));
  }

  @Test
  void shouldDefaultToSharpshooter() {
    Enforcer e = new Enforcer("Test");
    assertEquals("Sharpshooter",
            e.getChosenWeaponTalent()
                    .getName(),
            "Default weapon talent should "
                    + "be Sharpshooter");
  }

  @Test
  void shouldAcceptAlternateWeaponTalent() {
    Enforcer e = new Enforcer(
            "Test", "Pistoleer");
    assertEquals("Pistoleer",
            e.getChosenWeaponTalent()
                    .getName());
    List<String> names = e.getKeyTalents()
            .stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Pistoleer"));
    assertFalse(names.contains("Sharpshooter"));
  }

  @Test
  void shouldRejectInvalidWeaponTalent() {
    assertThrows(IllegalArgumentException.class,
            () -> new Enforcer(
                    "Test", "Fireball"));
  }

  @Test
  void shouldListAllEightAvailableWeaponTalents() {
    List<String> names =
            Enforcer.getAvailableWeaponTalents()
                    .stream()
                    .map(Talent::getName).toList();
    assertEquals(8, names.size());
    assertTrue(names.contains("Blade Fighter"));
    assertTrue(names.contains("Bowman"));
    assertTrue(names.contains(
            "Demolitions Expert"));
    assertTrue(names.contains("Heavy Weapons"));
    assertTrue(names.contains("Pistoleer"));
    assertTrue(names.contains("Polearms"));
    assertTrue(names.contains("Pugilist"));
    assertTrue(names.contains("Sharpshooter"));
  }

  @Test
  void shouldFilterWeaponTalentsByCombatCategory() {
    Enforcer e = new Enforcer("Test");
    List<String> weapons = e.getWeaponTalents();
    // getWeaponTalents() filters acquired
    // talents by COMBAT category — initially
    // empty since no talents have been added
    // via addTalent()
    assertNotNull(weapons);
  }
}