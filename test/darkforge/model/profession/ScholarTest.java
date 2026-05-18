package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ScholarTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnLogicAsKeyAttribute() {
    assertEquals(Attribute.LOGIC, new Scholar("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Scholar", new Scholar("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Scholar s = new Scholar("Test");
    assertEquals(4, s.getKeyTalents().size(),
            "Scholar has 4 key talents: Investigator, Librarian, Smart, Teratology");
    assertEquals(6, s.getSpecialties().size(),
            "Scholar has 6 specialties per Ch. 2");
    assertEquals(3, s.getStartingEquipmentSets().size(),
            "Scholar has 3 starting equipment sets per Ch. 2");
    for (var set : s.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Scholar s = new Scholar("Test");
    List<String> names = s.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Investigator"),
            "Key talents should include Investigator (KNOWLEDGE)");
    assertTrue(names.contains("Librarian"),
            "Key talents should include Librarian (KNOWLEDGE)");
    assertTrue(names.contains("Smart"),
            "Key talents should include Smart (RESILIENCE)");
    assertTrue(names.contains("Teratology"),
            "Key talents should include Teratology (KNOWLEDGE)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Scholar s = new Scholar("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Investigator", TalentCategory.KNOWLEDGE,
            "Librarian", TalentCategory.KNOWLEDGE,
            "Smart", TalentCategory.RESILIENCE,
            "Teratology", TalentCategory.KNOWLEDGE
    );
    for (Talent t : s.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(t.getName());
      assertNotNull(expectedCat, "Unexpected key talent: " + t.getName());
      assertEquals(expectedCat, t.getCategory(),
              t.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Scholar s = new Scholar("Test");
    List<String> names = s.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Guild Archivist"));
    assertTrue(names.contains("Algebraist Apprentice"));
    assertTrue(names.contains("Slipstream Cartographer"));
    assertTrue(names.contains("Diaspora Historian"));
    assertTrue(names.contains("Cave Botanist"));
    assertTrue(names.contains("Builder Archaeologist"));
  }

  @Test
  void shouldFilterResearchBonusByKnowledgeCategory() {
    Scholar s = new Scholar("Test");
    assertEquals(0, s.getResearchBonus());
    s.addTalent(new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 2, "effect"));
    s.addTalent(new Talent("Tough", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    assertEquals(2, s.getResearchBonus());
  }
}