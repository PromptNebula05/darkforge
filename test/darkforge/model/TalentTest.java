package darkforge.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TalentTest {

  @Test
  void shouldCreateTalentWithValidLevel() {
    Talent t = new Talent("Smart", "Quick thinking", TalentCategory.KNOWLEDGE, 3, 1, "Re-roll one die");
    assertEquals("Smart", t.getName());
    assertEquals(TalentCategory.KNOWLEDGE, t.getCategory());
    assertEquals(3, t.getMaxLevel());
    assertEquals(1, t.getCurrentLevel());
  }

  @Test
  void shouldRejectNegativeLevel() {
    assertThrows(IllegalArgumentException.class,
        () -> new Talent("Bad", "test", TalentCategory.COMBAT, 3, -1, "effect"));
  }

  @Test
  void shouldRejectLevelAboveMax() {
    assertThrows(IllegalArgumentException.class,
        () -> new Talent("Bad", "test", TalentCategory.COMBAT, 3, 4, "effect"));
  }

  @Test
  void shouldIncreaseLevel() {
    Talent t = new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 1, "effect");
    t.increaseLevel();
    assertEquals(2, t.getCurrentLevel());
  }

  @Test
  void shouldRejectIncreaseBeyondMax() {
    Talent t = new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 3, "effect");
    assertThrows(IllegalStateException.class, t::increaseLevel);
  }

  @Test
  void shouldFormatDisplayCorrectly() {
    Talent t = new Talent("Smart", "desc", TalentCategory.KNOWLEDGE, 3, 1, "Re-roll one die");
    String display = t.display();
    assertTrue(display.contains("Smart"));
    assertTrue(display.contains("KNOWLEDGE") || display.contains("Knowledge"));
    assertTrue(display.contains("Lv 1/3"));
  }

  @Test
  void shouldRejectNullName() {
    assertThrows(IllegalArgumentException.class, () -> new Talent(null, "desc", TalentCategory.COMBAT, 3, 1, "effect"));
  }

  @Test
  void shouldRejectBlankName() {
    assertThrows(IllegalArgumentException.class, () -> new Talent("  ", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
  }
}
