package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ScoundrelTest {

  @Test
  void shouldReturnAgilityAsKeyAttribute() {
    assertEquals(Attribute.PERCEPTION, new Scoundrel("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Scoundrel", new Scoundrel("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Scoundrel s = new Scoundrel("Test");
    assertEquals(4, s.getKeyTalents().size());
    assertEquals(6, s.getSpecialties().size());
    assertEquals(3, s.getStartingEquipmentSets().size());
  }

  @Test
  void shouldFilterDeceptionTalentsByStealthCategory() {
    Scoundrel s = new Scoundrel("Test");
    s.addTalent(new Talent("Sneaky", "desc", TalentCategory.STEALTH_MOBILITY, 3, 1, "effect"));
    s.addTalent(new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 1, "effect"));
    List<String> deception = s.getDeceptionTalents();
    assertTrue(deception.contains("Sneaky"));
    assertFalse(deception.contains("Smart"));
  }
}
