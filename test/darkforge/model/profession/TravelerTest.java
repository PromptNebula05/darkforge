package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TravelerTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnPerceptionAsKeyAttribute() {
    assertEquals(Attribute.PERCEPTION, new Traveler("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Traveler", new Traveler("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Traveler t = new Traveler("Test");
    assertEquals(4, t.getKeyTalents().size(),
            "Traveler has 4 key talents: Driver, Mechanic, Exo-Specialist, Zero-G Training");
    assertEquals(6, t.getSpecialties().size(),
            "Traveler has 6 specialties per Ch. 2");
    assertEquals(3, t.getStartingEquipmentSets().size(),
            "Traveler has 3 starting equipment sets per Ch. 2");
    for (var set : t.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Traveler t = new Traveler("Test");
    List<String> names = t.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Driver"),
            "Key talents should include Driver (VEHICLE_EXO)");
    assertTrue(names.contains("Mechanic"),
            "Key talents should include Mechanic (EQUIPMENT)");
    assertTrue(names.contains("Exo-Specialist"),
            "Key talents should include Exo-Specialist (VEHICLE_EXO)");
    assertTrue(names.contains("Zero-G Training"),
            "Key talents should include Zero-G Training (STEALTH_MOBILITY)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Traveler t = new Traveler("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Driver", TalentCategory.VEHICLE_EXO,
            "Mechanic", TalentCategory.EQUIPMENT,
            "Exo-Specialist", TalentCategory.VEHICLE_EXO,
            "Zero-G Training", TalentCategory.STEALTH_MOBILITY
    );
    for (Talent talent : t.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(talent.getName());
      assertNotNull(expectedCat, "Unexpected key talent: " + talent.getName());
      assertEquals(expectedCat, talent.getCategory(),
              talent.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Traveler t = new Traveler("Test");
    List<String> names = t.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Tugship Pilot"));
    assertTrue(names.contains("Hull Warden"));
    assertTrue(names.contains("Guild Surveyor"));
    assertTrue(names.contains("Kite Handler"));
    assertTrue(names.contains("Lighthouse Keeper"));
    assertTrue(names.contains("Asteroid Hauler"));
  }

  @Test
  void shouldFilterSurvivalTalentsByVehicleExoCategory() {
    Traveler t = new Traveler("Test");
    t.addTalent(new Talent("Pilot", "desc", TalentCategory.VEHICLE_EXO, 3, 1, "effect"));
    t.addTalent(new Talent("Tough", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    List<String> survival = t.getSurvivalTalents();
    assertTrue(survival.contains("Pilot"));
    assertFalse(survival.contains("Tough"));
  }
}