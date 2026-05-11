package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OddJobberTest {

  @Test
  void shouldReturnPerceptionAsKeyAttribute() {
    assertEquals(Attribute.EMPATHY, new OddJobber("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Odd Jobber", new OddJobber("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    OddJobber o = new OddJobber("Test");
    assertEquals(4, o.getKeyTalents().size());
    assertEquals(6, o.getSpecialties().size());
    assertEquals(3, o.getStartingEquipmentSets().size());
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
