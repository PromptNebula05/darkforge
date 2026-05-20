package darkforge.display;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import darkforge.creation.ExplorerFactory;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Renamed + enhanced from CharacterSheetRendererTest.
 * Tests the Iteration 2 CharacterSheetFormatter with
 * dot-leader alignment, word wrapping, section dividers,
 * and search-highlighting.
 */
class CharacterSheetFormatterTest {

  private Explorer createTestExplorer() {
    try {
      GameDataProvider.getTheInstance().initialize();
      ExplorerFactory factory = new ExplorerFactory();
      EnumMap<Attribute, Integer> attrs =
              new EnumMap<>(Attribute.class);
      attrs.put(Attribute.STRENGTH, 2);
      attrs.put(Attribute.AGILITY, 4);
      attrs.put(Attribute.LOGIC, 6);
      attrs.put(Attribute.PERCEPTION, 3);
      attrs.put(Attribute.INSIGHT, 4);
      attrs.put(Attribute.EMPATHY, 5);
      Origin origin = GameDataProvider.getTheInstance()
              .getOrigins().get(0);
      return factory.createExplorer(
              "Scholar", origin, 0,
              attrs, new int[]{1, 1, 1, 0},
              "Constantly reading", "Silver coin",
              "Sharp eyes", "Cantara Loutreides"
      );
    } catch (Exception e) {
      throw new RuntimeException(
              "Test setup failed", e);
    }
  }

  @Test
  void shouldContainExplorerName() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    assertTrue(formatter.formatCharacterSheet(
                    createTestExplorer())
            .contains("Cantara Loutreides"));
  }

  @Test
  void shouldContainProfessionName() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    assertTrue(formatter.formatCharacterSheet(
                    createTestExplorer())
            .contains("Scholar"));
  }

  @Test
  void shouldContainAttributeTotal() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    assertTrue(sheet.contains("TOTAL"));
  }

  @Test
  void shouldContainKeyMarker() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    assertTrue(sheet.contains("★"));
  }

  @Test
  void shouldContainHeaderAndFooter() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    assertTrue(sheet.contains("══"));
    assertTrue(sheet.contains("DARKFORGE"));
  }

  @Test
  void shouldProduceSingleLineSummary() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String summary = formatter.toCompactCard(
            createTestExplorer());
    assertFalse(summary.contains("\n"),
            "Summary should be a single line");
    assertTrue(summary.contains("Cantara Loutreides"));
    assertTrue(summary.contains("Scholar"));
  }

  @Test
  void shouldRejectNullExplorer() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    assertThrows(NullPointerException.class, () ->
            formatter.formatCharacterSheet(null));
  }

  @Test
  void shouldContainDotLeaderAlignment() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    assertTrue(sheet.contains(".."),
            "Should contain dot-leader alignment "
                    + "for talents");
  }

  @Test
  void shouldContainBoxDrawingCharacters() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    assertTrue(
            sheet.contains("╔")
                    || sheet.contains("║")
                    || sheet.contains("╠"),
            "Should contain box-drawing characters "
                    + "for section dividers");
  }

  @Test
  void shouldHighlightSearchTerm() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    String highlighted =
            formatter.highlightTerm(sheet, "Smart");
    assertTrue(
            highlighted.contains(">>Smart<<")
                    || highlighted.contains(">>smart<<"),
            "Should wrap search term in >>markers<<");
  }

  @Test
  void shouldHighlightCaseInsensitive() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    String highlighted =
            formatter.highlightTerm(sheet, "scholar");
    assertTrue(
            highlighted.contains(">>")
                    && highlighted.contains("<<"),
            "Highlight should be case-insensitive");
  }

  @Test
  void shouldIncludePersonalDetailsSection() {
    CharacterSheetFormatter formatter =
            new CharacterSheetFormatter();
    String sheet = formatter.formatCharacterSheet(
            createTestExplorer());
    assertTrue(
            sheet.contains("Constantly reading")
                    || sheet.contains("quirk"),
            "Should include personal details");
  }
}