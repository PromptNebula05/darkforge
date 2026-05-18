package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class OddJobberTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnEmpathyAsKeyAttribute() {
    assertEquals(Attribute.EMPATHY, new OddJobber("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Odd Jobber", new OddJobber("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    OddJobber o = new OddJobber("Test");
    assertEquals(4, o.getKeyTalents().size(),
            "Odd Jobber has 4 key talents: Actor, Charmer, Cultural Savant, Streetwise");
    assertEquals(6, o.getSpecialties().size(),
            "Odd Jobber has 6 specialties per Ch. 2");
    assertEquals(3, o.getStartingEquipmentSets().size(),
            "Odd Jobber has 3 starting equipment sets per Ch. 2");
    for (var set : o.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    OddJobber o = new OddJobber("Test");
    List<String> names = o.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Actor"),
            "Key talents should include Actor (SOCIAL)");
    assertTrue(names.contains("Charmer"),
            "Key talents should include Charmer (SOCIAL)");
    assertTrue(names.contains("Cultural Savant"),
            "Key talents should include Cultural Savant (KNOWLEDGE)");
    assertTrue(names.contains("Streetwise"),
            "Key talents should include Streetwise (STEALTH_MOBILITY)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    OddJobber o = new OddJobber("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Actor", TalentCategory.SOCIAL,
            "Charmer", TalentCategory.SOCIAL,
            "Cultural Savant", TalentCategory.KNOWLEDGE,
            "Streetwise", TalentCategory.STEALTH_MOBILITY
    );
    for (Talent t : o.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(t.getName());
      assertNotNull(expectedCat, "Unexpected key talent: " + t.getName());
      assertEquals(expectedCat, t.getCategory(),
              t.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    OddJobber o = new OddJobber("Test");
    List<String> names = o.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Guild Clerk"));
    assertTrue(names.contains("Stair Peddler"));
    assertTrue(names.contains("Ice Trader"));
    assertTrue(names.contains("Alley Cook"));
    assertTrue(names.contains("Coriolite Servant"));
    assertTrue(names.contains("Artifact Dealer"));
  }

  @Test
  void shouldCountDistinctTalentCategoriesForAdaptability() {
    OddJobber o = new OddJobber("Test");
    assertEquals(0, o.getAdaptabilityBonus());
    o.addTalent(new Talent("Sneaky", "desc", TalentCategory.STEALTH_MOBILITY, 3, 1, "effect"));
    o.addTalent(new Talent("Scrounger", "desc", TalentCategory.EQUIPMENT, 3, 1, "effect"));
    assertEquals(2, o.getAdaptabilityBonus());
  }
}