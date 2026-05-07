package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

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
    assertEquals(4, a.getKeyTalents().size());
    assertEquals(6, a.getSpecialties().size());
    assertEquals(3, a.getStartingEquipmentSets().size());
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
