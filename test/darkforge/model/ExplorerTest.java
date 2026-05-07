package darkforge.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the abstract Explorer class using a minimal TestExplorer stub.
 * This decouples Explorer tests from any profession subclass (Scholar,
 * Enforcer, etc.),
 * allowing Step 5 to be tested independently before Steps 6–7.
 */
class ExplorerTest {

  /**
   * Minimal concrete subclass of Explorer for testing purposes.
   * Returns hardcoded values for all abstract methods so that
   * Explorer's concrete behavior can be verified in isolation.
   */
  private static class TestExplorer extends Explorer {

    TestExplorer(String name) {
      super(name, "Test explorer");
    }

    @Override
    public Attribute getKeyAttribute() {
      return Attribute.LOGIC;
    }

    @Override
    public String getProfessionName() {
      return "TestProfession";
    }

    @Override
    public List<Talent> getKeyTalents() {
      return List.of(
          new Talent("TestTalentA", "desc", TalentCategory.KNOWLEDGE, 3, "effect"),
          new Talent("TestTalentB", "desc", TalentCategory.COMBAT, 3, "effect"),
          new Talent("TestTalentC", "desc", TalentCategory.SOCIAL, 3, "effect"),
          new Talent("TestTalentD", "desc", TalentCategory.RESILIENCE, 3, "effect"));
    }

    @Override
    public List<Specialty> getSpecialties() {
      return List.of(
          new Specialty("TestSpec", "A test specialty",
              new Talent("SpecTalent", "desc", TalentCategory.KNOWLEDGE, 3, "effect")));
    }

    @Override
    public List<List<Equipment>> getStartingEquipmentSets() {
      return List.of(
          List.of(new Equipment("TestGear", "desc", EquipmentWeight.LIGHT, 1)));
    }
  }

  // ── Helper ──────────────────────────────────────────────────────

  private Explorer createTestExplorer() {
    TestExplorer explorer = new TestExplorer("Test Scholar");
    EnumMap<Attribute, Integer> attrs = new EnumMap<>(Attribute.class);
    attrs.put(Attribute.STRENGTH, 3);
    attrs.put(Attribute.AGILITY, 4);
    attrs.put(Attribute.LOGIC, 5);
    attrs.put(Attribute.PERCEPTION, 4);
    attrs.put(Attribute.INSIGHT, 4);
    attrs.put(Attribute.EMPATHY, 4);
    explorer.setAttributes(attrs);
    return explorer;
  }

  // ── Derived stat tests ──────────────────────────────────────────

  @Test
  void shouldComputeHealthCorrectly() {
    Explorer e = createTestExplorer();
    // Health = STR + AGL = 3 + 4 = 7
    assertEquals(7, e.getHealth());
  }

  @Test
  void shouldComputeHopeCorrectly() {
    Explorer e = createTestExplorer();
    // Hope = LOG + EMP = 5 + 4 = 9
    assertEquals(9, e.getHope());
  }

  @Test
  void shouldComputeHeartCorrectly() {
    Explorer e = createTestExplorer();
    // Heart = INS + PER = 4 + 4 = 8
    assertEquals(8, e.getHeart());
  }

  // ── Attribute storage tests ─────────────────────────────────────

  @Test
  void shouldStoreAndRetrieveAllAttributes() {
    Explorer e = createTestExplorer();
    int total = 0;
    for (Attribute attr : Attribute.values()) {
      int value = e.getAttribute(attr);
      assertTrue(value >= 2, attr + " should be at least 2");
      total += value;
    }
    assertEquals(24, total, "Total attribute points should equal 24");
  }

  @Test
  void shouldRejectMissingAttribute() {
    TestExplorer explorer = new TestExplorer("Test");
    EnumMap<Attribute, Integer> partial = new EnumMap<>(Attribute.class);
    partial.put(Attribute.STRENGTH, 4);
    assertThrows(IllegalArgumentException.class, () -> explorer.setAttributes(partial));
  }

  // ── Talent management tests ─────────────────────────────────────

  @Test
  void shouldHandleTalentDuplication() {
    Explorer e = createTestExplorer();
    Talent t1 = new Talent("Exo-Specialist", "desc", TalentCategory.VEHICLE_EXO, 3, 1, "effect");
    Talent t2 = new Talent("Exo-Specialist", "desc", TalentCategory.VEHICLE_EXO, 3, 1, "effect");
    assertTrue(e.addTalent(t1), "First add should return true (new talent)");
    assertFalse(e.addTalent(t2), "Second add should return false (duplicate — level increased)");
    assertEquals(1, e.getTalents().size(), "Should have 1 talent, not 2");
    assertEquals(2, e.getTalents().get(0).getCurrentLevel(), "Duplicate should increase level to 2");
  }

  @Test
  void shouldReturnUnmodifiableTalentList() {
    Explorer e = createTestExplorer();
    e.addTalent(new Talent("TestTalent", "desc", TalentCategory.COMBAT, 3, 1, "effect"));
    List<Talent> talents = e.getTalents();
    assertThrows(UnsupportedOperationException.class,
        () -> talents.add(new Talent("Hacked", "desc", TalentCategory.COMBAT, 3, 1, "effect")),
        "Talent list should be unmodifiable");
  }

  // ── Display tests ───────────────────────────────────────────────

  @Test
  void shouldProduceNonEmptyDisplay() {
    Explorer e = createTestExplorer();
    assertFalse(e.display().isBlank());
    assertTrue(e.display().contains("Test Scholar"));
  }

  @Test
  void shouldIncludeProfessionNameInDisplay() {
    Explorer e = createTestExplorer();
    assertTrue(e.display().contains("TestProfession"),
        "display() should include the profession name");
  }

  @Test
  void shouldIncludeDerivedStatsInDisplay() {
    Explorer e = createTestExplorer();
    String output = e.display();
    assertTrue(output.contains("Health"), "display() should include Health");
    assertTrue(output.contains("Hope"), "display() should include Hope");
    assertTrue(output.contains("Heart"), "display() should include Heart");
  }

  // ── Abstract method contract tests ──────────────────────────────

  @Test
  void shouldReturnKeyAttributeFromSubclass() {
    Explorer e = createTestExplorer();
    assertEquals(Attribute.LOGIC, e.getKeyAttribute(),
        "Key attribute should come from the subclass override");
  }

  @Test
  void shouldReturnKeyTalentsFromSubclass() {
    Explorer e = createTestExplorer();
    assertEquals(4, e.getKeyTalents().size(),
        "TestExplorer defines 4 key talents");
  }

  @Test
  void shouldReturnSpecialtiesFromSubclass() {
    Explorer e = createTestExplorer();
    assertFalse(e.getSpecialties().isEmpty(),
        "TestExplorer defines at least 1 specialty");
  }

  @Test
  void shouldReturnEquipmentSetsFromSubclass() {
    Explorer e = createTestExplorer();
    assertFalse(e.getStartingEquipmentSets().isEmpty(),
        "TestExplorer defines at least 1 equipment set");
  }
}
