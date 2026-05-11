package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EsotericTest {

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
    assertEquals(4, e.getKeyTalents().size());
    assertEquals(6, e.getSpecialties().size());
    assertEquals(3, e.getStartingEquipmentSets().size());
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
