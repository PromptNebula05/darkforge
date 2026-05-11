package darkforge.display;

import darkforge.model.*;
import darkforge.model.profession.Scholar;
import darkforge.creation.ExplorerFactory;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class CharacterSheetRendererTest {

  private Explorer createTestExplorer() {
    ExplorerFactory factory = new ExplorerFactory();
    EnumMap<Attribute, Integer> attrs = new EnumMap<>(Attribute.class);
    attrs.put(Attribute.STRENGTH, 2);
    attrs.put(Attribute.AGILITY, 4);
    attrs.put(Attribute.LOGIC, 6);
    attrs.put(Attribute.PERCEPTION, 3);
    attrs.put(Attribute.INSIGHT, 4);
    attrs.put(Attribute.EMPATHY, 5);
    return factory.createExplorer(
        "Scholar", "Cantara Loutreides", 1, 1,
        attrs, new int[] { 1, 1, 1, 0 },
        "Constantly reading", "Silver coin", "Sharp eyes");
  }

  @Test
  void shouldContainExplorerName() {
    CharacterSheetRenderer renderer = new CharacterSheetRenderer(createTestExplorer());
    assertTrue(renderer.toFormattedString().contains("Cantara Loutreides"));
  }

  @Test
  void shouldContainProfessionName() {
    CharacterSheetRenderer renderer = new CharacterSheetRenderer(createTestExplorer());
    assertTrue(renderer.toFormattedString().contains("Scholar"));
  }

  @Test
  void shouldContainAllAttributeAbbreviations() {
    String sheet = new CharacterSheetRenderer(createTestExplorer()).toFormattedString();
    for (Attribute attr : Attribute.values()) {
      assertTrue(sheet.contains(attr.getAbbreviation()),
          "Missing attribute: " + attr.getAbbreviation());
    }
  }

  @Test
  void shouldContainDerivedStats() {
    String sheet = new CharacterSheetRenderer(createTestExplorer()).toFormattedString();
    assertTrue(sheet.contains("Health"));
    assertTrue(sheet.contains("Hope"));
    assertTrue(sheet.contains("Heart"));
  }

  @Test
  void shouldContainKeyMarker() {
    String sheet = new CharacterSheetRenderer(createTestExplorer()).toFormattedString();
    assertTrue(sheet.contains("[KEY]"));
  }

  @Test
  void shouldContainHeaderAndFooter() {
    String sheet = new CharacterSheetRenderer(createTestExplorer()).toFormattedString();
    assertTrue(sheet.contains("══"));
    assertTrue(sheet.contains("DARKFORGE"));
  }

  @Test
  void shouldProduceSingleLineSummary() {
    CharacterSheetRenderer renderer = new CharacterSheetRenderer(createTestExplorer());
    String summary = renderer.toSummary();
    assertFalse(summary.contains("\n"), "Summary should be a single line");
    assertTrue(summary.contains("Cantara Loutreides"));
    assertTrue(summary.contains("Scholar"));
  }

  @Test
  void shouldRejectNullExplorer() {
    assertThrows(IllegalArgumentException.class, () -> new CharacterSheetRenderer(null));
  }
}
