package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScholarTest {

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
    assertEquals(4, s.getKeyTalents().size());
    assertEquals(6, s.getSpecialties().size());
    assertEquals(3, s.getStartingEquipmentSets().size());
    for (var set : s.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldComputeResearchBonusFromKnowledgeTalents() {
    Scholar s = new Scholar("Test");
    assertEquals(0, s.getResearchBonus());
    s.addTalent(new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 2, "effect"));
    s.addTalent(new Talent("Tough", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    assertEquals(2, s.getResearchBonus());
  }

  @Test
  void shouldIncludeScholarInDisplay() {
    assertTrue(new Scholar("Test").display().contains("Scholar"));
  }
}
