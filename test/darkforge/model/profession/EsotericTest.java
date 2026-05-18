package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EsotericTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnInsightAsKeyAttribute() {
    assertEquals(Attribute.INSIGHT, new Esoteric("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Esoteric", new Esoteric("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Esoteric e = new Esoteric("Test");
    assertEquals(4, e.getKeyTalents().size(),
            "Esoteric has 4 key talents: Actor, Botanist, Cultural Savant, Librarian");
    assertEquals(6, e.getSpecialties().size(),
            "Esoteric has 6 specialties per Ch. 2");
    assertEquals(3, e.getStartingEquipmentSets().size(),
            "Esoteric has 3 starting equipment sets per Ch. 2");
    for (var set : e.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Esoteric e = new Esoteric("Test");
    List<String> names = e.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Actor"),
            "Key talents should include Actor (SOCIAL)");
    assertTrue(names.contains("Botanist"),
            "Key talents should include Botanist (KNOWLEDGE)");
    assertTrue(names.contains("Cultural Savant"),
            "Key talents should include Cultural Savant (KNOWLEDGE)");
    assertTrue(names.contains("Librarian"),
            "Key talents should include Librarian (KNOWLEDGE)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Esoteric e = new Esoteric("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Actor", TalentCategory.SOCIAL,
            "Botanist", TalentCategory.KNOWLEDGE,
            "Cultural Savant", TalentCategory.KNOWLEDGE,
            "Librarian", TalentCategory.KNOWLEDGE
    );
    for (Talent t : e.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(t.getName());
      assertNotNull(expectedCat, "Unexpected key talent: " + t.getName());
      assertEquals(expectedCat, t.getCategory(),
              t.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Esoteric e = new Esoteric("Test");
    List<String> names = e.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Spice Engineer"));
    assertTrue(names.contains("Bird Warden"));
    assertTrue(names.contains("Coriolite Seer"));
    assertTrue(names.contains("Revolutionary Prophet"));
    assertTrue(names.contains("Toad Dreamer"));
    assertTrue(names.contains("Rim Zealot"));
  }

  @Test
  void shouldFilterMysticalTalentsByInsightCategory() {
    Esoteric e = new Esoteric("Test");
    e.addTalent(new Talent("Mystic", "desc", TalentCategory.INSIGHT, 3, 1, "effect"));
    e.addTalent(new Talent("Brawler", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    List<String> mystical = e.getMysticalTalents();
    assertTrue(mystical.contains("Mystic"));
    assertFalse(mystical.contains("Brawler"));
  }
}