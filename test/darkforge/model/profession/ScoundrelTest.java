package darkforge.model.profession;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ScoundrelTest {

  @BeforeAll
  static void initGameData() {
    GameDataProvider.getTheInstance().initialize();
  }

  @Test
  void shouldReturnPerceptionAsKeyAttribute() {
    assertEquals(Attribute.PERCEPTION, new Scoundrel("Test").getKeyAttribute());
  }

  @Test
  void shouldReturnCorrectProfessionName() {
    assertEquals("Scoundrel", new Scoundrel("Test").getProfessionName());
  }

  @Test
  void shouldReturnCorrectCollectionSizes() {
    Scoundrel s = new Scoundrel("Test");
    assertEquals(4, s.getKeyTalents().size(),
            "Scoundrel has 4 key talents: Acrobat, Charmer, Lookout, Stealthy");
    assertEquals(6, s.getSpecialties().size(),
            "Scoundrel has 6 specialties per Ch. 2");
    assertEquals(3, s.getStartingEquipmentSets().size(),
            "Scoundrel has 3 starting equipment sets per Ch. 2");
    for (var set : s.getStartingEquipmentSets()) {
      assertFalse(set.isEmpty(), "Equipment set should not be empty");
    }
  }

  @Test
  void shouldHaveExpectedKeyTalentNames() {
    Scoundrel s = new Scoundrel("Test");
    List<String> names = s.getKeyTalents().stream()
            .map(Talent::getName).toList();
    assertTrue(names.contains("Acrobat"),
            "Key talents should include Acrobat (STEALTH_MOBILITY)");
    assertTrue(names.contains("Charmer"),
            "Key talents should include Charmer (SOCIAL)");
    assertTrue(names.contains("Lookout"),
            "Key talents should include Lookout (STEALTH_MOBILITY)");
    assertTrue(names.contains("Stealthy"),
            "Key talents should include Stealthy (STEALTH_MOBILITY)");
  }

  @Test
  void shouldHaveExpectedKeyTalentCategories() {
    Scoundrel s = new Scoundrel("Test");
    Map<String, TalentCategory> expected = Map.of(
            "Acrobat", TalentCategory.STEALTH_MOBILITY,
            "Charmer", TalentCategory.SOCIAL,
            "Lookout", TalentCategory.STEALTH_MOBILITY,
            "Stealthy", TalentCategory.STEALTH_MOBILITY
    );
    for (Talent t : s.getKeyTalents()) {
      TalentCategory expectedCat = expected.get(t.getName());
      assertNotNull(expectedCat,
              "Unexpected key talent: " + t.getName());
      assertEquals(expectedCat, t.getCategory(),
              t.getName() + " should have category " + expectedCat);
    }
  }

  @Test
  void shouldHaveExpectedSpecialtyNames() {
    Scoundrel s = new Scoundrel("Test");
    List<String> names = s.getSpecialties().stream()
            .map(Specialty::getName).toList();
    assertTrue(names.contains("Dust Runner"),
            "Specialties should include Dust Runner");
    assertTrue(names.contains("Tech Smuggler"),
            "Specialties should include Tech Smuggler");
    assertTrue(names.contains("Guild Spy"),
            "Specialties should include Guild Spy");
    assertTrue(names.contains("Alley Thug"),
            "Specialties should include Alley Thug");
    assertTrue(names.contains("Hull Cutter"),
            "Specialties should include Hull Cutter");
    assertTrue(names.contains("Con Artist"),
            "Specialties should include Con Artist");
  }

  @Test
  void shouldFilterDeceptionTalentsByStealthCategory() {
    // getDeceptionTalents() filters by STEALTH_MOBILITY — unchanged from Iter 1
    Scoundrel s = new Scoundrel("Test");
    s.addTalent(new Talent("Sneaky", "desc", TalentCategory.STEALTH_MOBILITY, 3, 1, "effect"));
    s.addTalent(new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 1, "effect"));
    List<String> deception = s.getDeceptionTalents();
    assertTrue(deception.contains("Sneaky"),
            "STEALTH_MOBILITY talents should appear in deception list");
    assertFalse(deception.contains("Smart"),
            "KNOWLEDGE talents should NOT appear in deception list");
  }
}