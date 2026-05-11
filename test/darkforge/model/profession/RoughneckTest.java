package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoughneckTest {

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
    assertEquals(4, r.getKeyTalents().size());
    assertEquals(6, r.getSpecialties().size());
    assertEquals(3, r.getStartingEquipmentSets().size());
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
