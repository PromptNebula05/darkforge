package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EnforcerTest {

  @Test
  void shouldReturnAgilityAsKeyAttribute() {
    assertEquals(Attribute.AGILITY, new Enforcer("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Enforcer", new Enforcer("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Enforcer e = new Enforcer("Test");
    assertEquals(4, e.getKeyTalents().size());
    assertEquals(6, e.getSpecialties().size());
    assertEquals(3, e.getStartingEquipmentSets().size());
  }

  @Test
  void shouldFilterWeaponTalentsByCombatCategory() {
    Enforcer e = new Enforcer("Test");
    e.addTalent(new Talent("Tough", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    e.addTalent(new Talent("Charming", "desc", TalentCategory.SOCIAL, 3, 1, "effect"));
    List<String> weapons = e.getWeaponTalents();
    assertTrue(weapons.contains("Tough"));
    assertFalse(weapons.contains("Charming"));
  }
}
