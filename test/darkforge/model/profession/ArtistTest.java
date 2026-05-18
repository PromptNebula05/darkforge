package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnEmpathyAsKeyAttribute() {
    assertEquals(Attribute.EMPATHY, new Artist("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Artist", new Artist("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Artist a = new Artist("Test");
    assertEquals(4, a.getKeyTalents().size(),
            "Artist has 4 key talents: Acrobat, Charmer, Cultural Savant, Renowned");
    assertEquals(6, a.getSpecialties().size(),
            "Artist has 6 specialties per Ch. 2");
    assertEquals(3, a.getStartingEquipmentSets().size(),
            "Artist has 3 starting equipment sets per Ch. 2");
    for (var set : a.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Artist a = new Artist("Test");
    List<String> names = a.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Acrobat"),
            "Key talents should include Acrobat (STEALTH_MOBILITY)");
    assertTrue(names.contains("Charmer"),
            "Key talents should include Charmer (SOCIAL)");
    assertTrue(names.contains("Cultural Savant"),
            "Key talents should include Cultural Savant (KNOWLEDGE)");
    assertTrue(names.contains("Renowned"),
            "Key talents should include Renowned (SOCIAL)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Artist a = new Artist("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Acrobat", TalentCategory.STEALTH_MOBILITY,
            "Charmer", TalentCategory.SOCIAL,
            "Cultural Savant", TalentCategory.KNOWLEDGE,
            "Renowned", TalentCategory.SOCIAL
    );
    for (Talent t : a.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(t.getName());
      assertNotNull(expectedCat, "Unexpected key talent: " + t.getName());
      assertEquals(expectedCat, t.getCategory(),
              t.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Artist a = new Artist("Test");
    List<String> names = a.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Hull Painter"));
    assertTrue(names.contains("Staircase Poet"));
    assertTrue(names.contains("Maidy Row Balladeer"));
    assertTrue(names.contains("Alley Theater Actor"));
    assertTrue(names.contains("Occasional Publisher"));
    assertTrue(names.contains("Machine Artisan"));
  }

  @Test
  void shouldComputePerformanceBonusFromSocialTalents() {
    Artist a = new Artist("Test");
    assertEquals(0, a.getPerformanceBonus());
    a.addTalent(new Talent("Charming", "desc", TalentCategory.SOCIAL, 3, 2, "effect"));
    a.addTalent(new Talent("Tough", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    assertEquals(2, a.getPerformanceBonus());
  }
}