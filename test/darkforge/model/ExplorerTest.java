package darkforge.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the abstract Explorer class using
 * a minimal TestExplorer stub. Updated for
 * Iteration 3: Inventory<Talent> replaces
 * List<Talent>.
 */
class ExplorerTest {

  /**
   * Minimal concrete subclass of Explorer
   * for testing purposes.
   */
  private static class TestExplorer
          extends Explorer {

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
              new Talent("TestTalentA",
                      "desc",
                      TalentCategory.KNOWLEDGE,
                      3, "effect"),
              new Talent("TestTalentB",
                      "desc",
                      TalentCategory.COMBAT,
                      3, "effect"),
              new Talent("TestTalentC",
                      "desc",
                      TalentCategory.SOCIAL,
                      3, "effect"),
              new Talent("TestTalentD",
                      "desc",
                      TalentCategory.RESILIENCE,
                      3, "effect"));
    }

    @Override
    public List<Specialty>
    getSpecialties() {
      return List.of(
              new Specialty("TestSpec",
                      "A test specialty",
                      new Talent("SpecTalent",
                              "desc",
                              TalentCategory.KNOWLEDGE,
                              3, "effect")));
    }

    @Override
    public List<List<Equipment>>
    getStartingEquipmentSets() {
      return List.of(
              List.of(new Equipment(
                      "TestGear", "desc",
                      EquipmentWeight.LIGHT, 1)));
    }
  }

  // =========================================
  // Helper
  // =========================================

  private Explorer createTestExplorer() {
    TestExplorer explorer =
            new TestExplorer("Test Scholar");
    EnumMap<Attribute, Integer> attrs =
            new EnumMap<>(Attribute.class);
    attrs.put(Attribute.STRENGTH, 3);
    attrs.put(Attribute.AGILITY, 4);
    attrs.put(Attribute.LOGIC, 5);
    attrs.put(Attribute.PERCEPTION, 4);
    attrs.put(Attribute.INSIGHT, 4);
    attrs.put(Attribute.EMPATHY, 4);
    explorer.setAttributes(attrs);
    return explorer;
  }

  // =========================================
  // Derived stat tests
  // =========================================

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

  // =========================================
  // Attribute storage tests
  // =========================================

  @Test
  void shouldStoreAndRetrieveAllAttributes() {
    Explorer e = createTestExplorer();
    int total = 0;
    for (Attribute attr
            : Attribute.values()) {
      int value = e.getAttribute(attr);
      assertTrue(value >= 2,
              attr + " should be at"
                      + " least 2");
      total += value;
    }
    assertEquals(24, total,
            "Total attribute points"
                    + " should equal 24");
  }

  @Test
  void shouldRejectMissingAttribute() {
    TestExplorer explorer =
            new TestExplorer("Test");
    EnumMap<Attribute, Integer> partial =
            new EnumMap<>(Attribute.class);
    partial.put(Attribute.STRENGTH, 4);
    assertThrows(
            IllegalArgumentException.class,
            () -> explorer.setAttributes(
                    partial));
  }

  // =========================================
  // Talent management (Inventory<Talent>)
  // =========================================

  @Test
  void shouldHandleTalentDuplication() {
    Explorer e = createTestExplorer();
    Talent t1 = new Talent(
            "Exo-Specialist", "desc",
            TalentCategory.VEHICLE_EXO,
            3, 1, "effect");
    Talent t2 = new Talent(
            "Exo-Specialist", "desc",
            TalentCategory.VEHICLE_EXO,
            3, 1, "effect");

    assertTrue(e.addTalent(t1),
            "First add should return true");
    assertFalse(e.addTalent(t2),
            "Second add should return false"
                    + " (duplicate, level up)");
    assertEquals(1,
            e.getTalents().size(),
            "Should have 1 talent, not 2");
    assertEquals(2,
            e.getTalents().getAll().get(0)
                    .getCurrentLevel(),
            "Duplicate should increase"
                    + " level to 2");
  }

  @Test
  void shouldReturnUnmodifiableTalentView() {
    Explorer e = createTestExplorer();
    e.addTalent(new Talent(
            "TestTalent", "desc",
            TalentCategory.COMBAT,
            3, 1, "effect"));
    List<Talent> view =
            e.getTalents().getAll();
    assertThrows(
            UnsupportedOperationException.class,
            () -> view.add(new Talent(
                    "Hacked", "desc",
                    TalentCategory.COMBAT,
                    3, 1, "effect")),
            "getAll() should be"
                    + " unmodifiable");
  }

  // =========================================
  // getTalentsByCategory (NEW)
  // =========================================

  @Test
  void getTalentsByCategoryGroupsCorrectly() {
    Explorer e = createTestExplorer();
    e.addTalent(new Talent(
            "Blademaster", "desc",
            TalentCategory.COMBAT,
            3, "effect"));
    e.addTalent(new Talent(
            "Quick Draw", "desc",
            TalentCategory.COMBAT,
            3, "effect"));
    e.addTalent(new Talent(
            "Diplomat", "desc",
            TalentCategory.SOCIAL,
            3, "effect"));

    Map<TalentCategory, List<Talent>>
            grouped =
            e.getTalentsByCategory();

    assertEquals(2,
            grouped.get(
                    TalentCategory.COMBAT).size());
    assertEquals(1,
            grouped.get(
                    TalentCategory.SOCIAL).size());
    assertNull(grouped.get(
                    TalentCategory.KNOWLEDGE),
            "No talents in KNOWLEDGE");
  }

  @Test
  void getTalentsByCategoryEmptyWhenNone() {
    Explorer e = createTestExplorer();
    Map<TalentCategory, List<Talent>>
            grouped =
            e.getTalentsByCategory();
    assertTrue(grouped.isEmpty());
  }

  // =========================================
  // Inventory findByName (NEW)
  // =========================================

  @Test
  void talentsFindByNamePartialMatch() {
    Explorer e = createTestExplorer();
    e.addTalent(new Talent(
            "Street Smart", "desc",
            TalentCategory.SOCIAL,
            3, "effect"));
    e.addTalent(new Talent(
            "Blademaster", "desc",
            TalentCategory.COMBAT,
            3, "effect"));

    List<Talent> results =
            e.getTalents().findByName("Smart");
    assertEquals(1, results.size());
    assertEquals("Street Smart",
            results.get(0).getName());
  }

  @Test
  void talentsFindByNameNoMatch() {
    Explorer e = createTestExplorer();
    e.addTalent(new Talent(
            "Blademaster", "desc",
            TalentCategory.COMBAT,
            3, "effect"));

    List<Talent> results =
            e.getTalents()
                    .findByName("Nonexistent");
    assertTrue(results.isEmpty());
  }

  @Test
  void talentsFilterByCombat() {
    Explorer e = createTestExplorer();
    e.addTalent(new Talent(
            "Blademaster", "desc",
            TalentCategory.COMBAT,
            3, "effect"));
    e.addTalent(new Talent(
            "Diplomat", "desc",
            TalentCategory.SOCIAL,
            3, "effect"));

    List<Talent> combat =
            e.getTalents().filter(
                    t -> t.getCategory()
                            == TalentCategory.COMBAT);
    assertEquals(1, combat.size());
    assertEquals("Blademaster",
            combat.get(0).getName());
  }

  // =========================================
  // Display tests
  // =========================================

  @Test
  void shouldProduceNonEmptyDisplay() {
    Explorer e = createTestExplorer();
    assertFalse(e.display().isBlank());
    assertTrue(e.display()
            .contains("Test Scholar"));
  }

  @Test
  void shouldIncludeProfessionNameInDisplay() {
    Explorer e = createTestExplorer();
    assertTrue(e.display()
                    .contains("TestProfession"),
            "display() should include"
                    + " the profession name");
  }

  @Test
  void shouldIncludeDerivedStatsInDisplay() {
    Explorer e = createTestExplorer();
    String output = e.display();
    assertTrue(output.contains("Health"));
    assertTrue(output.contains("Hope"));
    assertTrue(output.contains("Heart"));
  }

  // =========================================
  // Abstract method contract tests
  // =========================================

  @Test
  void shouldReturnKeyAttributeFromSubclass() {
    Explorer e = createTestExplorer();
    assertEquals(Attribute.LOGIC,
            e.getKeyAttribute(),
            "Key attribute should come"
                    + " from the subclass");
  }

  @Test
  void shouldReturnKeyTalentsFromSubclass() {
    Explorer e = createTestExplorer();
    assertEquals(4,
            e.getKeyTalents().size(),
            "TestExplorer defines"
                    + " 4 key talents");
  }

  @Test
  void shouldReturnSpecialtiesFromSubclass() {
    Explorer e = createTestExplorer();
    assertFalse(
            e.getSpecialties().isEmpty(),
            "TestExplorer defines at"
                    + " least 1 specialty");
  }

  @Test
  void shouldReturnEquipmentSetsFromSubclass() {
    Explorer e = createTestExplorer();
    assertFalse(
            e.getStartingEquipmentSets()
                    .isEmpty(),
            "TestExplorer defines at"
                    + " least 1 equipment set");
  }
}