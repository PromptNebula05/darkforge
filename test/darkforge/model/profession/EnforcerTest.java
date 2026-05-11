package darkforge.model.profession;

import darkforge.model.*;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.Collectors;
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

  // ── Weapon talent selection tests ────────────────────────────────

  @Test
  void shouldDefaultToSharpshooterWeaponTalent() {
    Enforcer e = new Enforcer("Test");
    assertEquals("Sharpshooter", e.getChosenWeaponTalent().getName());
  }

  @Test
  void shouldAcceptValidWeaponTalentByName() {
    Enforcer e = new Enforcer("Test", "Pistoleer");
    assertEquals("Pistoleer", e.getChosenWeaponTalent().getName());
    assertEquals(TalentCategory.COMBAT, e.getChosenWeaponTalent().getCategory());
  }

  @Test
  void shouldIncludeChosenWeaponTalentInKeyTalents() {
    Enforcer e = new Enforcer("Test", "Blade Fighter");
    List<String> keyTalentNames = e.getKeyTalents().stream()
        .map(Talent::getName).toList();
    assertTrue(keyTalentNames.contains("Blade Fighter"),
        "Chosen weapon talent should appear in key talents");
  }

  @Test
  void shouldRejectInvalidWeaponTalentName() {
    assertThrows(IllegalArgumentException.class, () -> new Enforcer("Test", "Laser Eyes"),
        "Non-existent weapon talent should be rejected");
  }

  @Test
  void shouldReturnAllAvailableWeaponTalents() {
    List<Talent> available = Enforcer.getAvailableWeaponTalents();
    assertFalse(available.isEmpty());
    assertTrue(available.size() >= 8,
        "Should have at least 8 weapon talents per Ch. 3");
    assertTrue(available.stream().allMatch(t -> t.getCategory() == TalentCategory.COMBAT),
        "All weapon talents should be COMBAT category");
  }

  @Test
  void shouldBeCaseInsensitiveForWeaponTalentName() {
    Enforcer e = new Enforcer("Test", "pistoleer");
    assertEquals("Pistoleer", e.getChosenWeaponTalent().getName());
  }
}
