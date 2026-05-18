package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class RoughneckTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnStrengthAsKeyAttribute() {
    assertEquals(Attribute.STRENGTH, new Roughneck("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Roughneck", new Roughneck("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Roughneck r = new Roughneck("Test");
    assertEquals(4, r.getKeyTalents().size(),
            "Roughneck has 4 key talents: Endurance, Force, Jury-Rig, Scan Operator");
    assertEquals(6, r.getSpecialties().size(),
            "Roughneck has 6 specialties per Ch. 2");
    assertEquals(3, r.getStartingEquipmentSets().size(),
            "Roughneck has 3 starting equipment sets per Ch. 2");
    for (var set : r.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Roughneck r = new Roughneck("Test");
    List<String> names = r.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Endurance"),
            "Key talents should include Endurance (RESILIENCE)");
    assertTrue(names.contains("Force"),
            "Key talents should include Force (RESILIENCE)");
    assertTrue(names.contains("Jury-Rig"),
            "Key talents should include Jury-Rig (EQUIPMENT)");
    assertTrue(names.contains("Scan Operator"),
            "Key talents should include Scan Operator (EQUIPMENT)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Roughneck r = new Roughneck("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Endurance", TalentCategory.RESILIENCE,
            "Force", TalentCategory.RESILIENCE,
            "Jury-Rig", TalentCategory.EQUIPMENT,
            "Scan Operator", TalentCategory.EQUIPMENT
    );
    for (Talent t : r.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(t.getName());
      assertNotNull(expectedCat, "Unexpected key talent: " + t.getName());
      assertEquals(expectedCat, t.getCategory(),
              t.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Roughneck r = new Roughneck("Test");
    List<String> names = r.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Hull Guard"));
    assertTrue(names.contains("Wreck Diver"));
    assertTrue(names.contains("Vacuum Welder"));
    assertTrue(names.contains("Deep Miner"));
    assertTrue(names.contains("Crane Rat"));
    assertTrue(names.contains("Machine Tender"));
  }

  @Test
  void shouldComputeEnduranceBonusFromResilienceTalents() {
    Roughneck r = new Roughneck("Test");
    assertEquals(0, r.getEnduranceBonus());
    r.addTalent(new Talent("Hardy", "desc", TalentCategory.RESILIENCE, 3, 2, "effect"));
    r.addTalent(new Talent("Brawler", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    assertEquals(2, r.getEnduranceBonus());
  }
}