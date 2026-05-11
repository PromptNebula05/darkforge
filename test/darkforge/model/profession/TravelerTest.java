package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TravelerTest {

  @Test
  void shouldReturnEmpathyAsKeyAttribute() {
    assertEquals(Attribute.EMPATHY, new Traveler("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Traveler", new Traveler("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Traveler t = new Traveler("Test");
    assertEquals(4, t.getKeyTalents().size());
    assertEquals(6, t.getSpecialties().size());
    assertEquals(3, t.getStartingEquipmentSets().size());
  }

  @Test
  void shouldFilterSurvivalTalentsByRecoveryCategory() {
    Traveler t = new Traveler("Test");
    t.addTalent(new Talent("Survivor", "desc", TalentCategory.RECOVERY, 3, 1, "effect"));
    t.addTalent(new Talent("Tough", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    List<String> survival = t.getSurvivalTalents();
    assertTrue(survival.contains("Survivor"));
    assertFalse(survival.contains("Tough"));
  }
}
