package darkforge.display;

import darkforge.model.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Formats Explorer data into rich text character
 * sheets for console display.
 *
 */
public class CharacterSheetFormatter {
  private static final int SHEET_WIDTH = 50;
  private static final int TALENT_PAD = 24;
  private static final int WRAP_WIDTH = 50;

  /**
   * Format a full character sheet with box-drawing
   * section dividers.
   */
  public String formatCharacterSheet(
          Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatHeader(explorer));
    sb.append(formatIdentitySection(explorer));
    sb.append(formatAttributeBlock(explorer));
    sb.append(formatTalentSection(explorer));
    sb.append(formatEquipmentList(explorer));
    sb.append(formatPersonalDetails(explorer));
    sb.append(formatFooter());
    return sb.toString();
  }

  /**
   * Box-drawing header.
   */
  private String formatHeader(Explorer explorer) {
    String title = "DARKFORGE — Explorer "
            + "Character Sheet";
    return String.format(
            "╔%s╗\n"
                    + "║ %s ║\n"
                    + "╠%s╣\n",
            "═".repeat(SHEET_WIDTH),
            centerPad(title, SHEET_WIDTH),
            "═".repeat(SHEET_WIDTH));
  }

  /**
   * Aligned attribute columns via String.format.
   */
  public String formatAttributeBlock(
          Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("ATTRIBUTES"));
    for (Attribute a : Attribute.values()) {
      int val = explorer.getAttribute(a);
      String marker = (a == explorer
              .getKeyAttribute()) ? " ★" : "";
      sb.append(String.format(
              "  %-12s %d%s\n",
              a.getDisplayName(), val, marker));
    }
    int total = Arrays.stream(Attribute.values())
            .mapToInt(explorer::getAttribute).sum();
    sb.append(String.format(
            "  %-12s %d\n", "TOTAL", total));
    return sb.toString();
  }

  /**
   * Dot-leader aligned talent descriptions.
   * e.g. "Smart .............. Lv 2/3  [KNOWLEDGE]"
   */
  public String formatTalentSection(
          Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("TALENTS"));
    for (Talent t : explorer.getTalents()) {
      sb.append("  ")
              .append(formatTalentDescription(t))
              .append("\n");
    }
    return sb.toString();
  }

  /**
   * Dot-leader formatting for a single talent.
   */
  public String formatTalentDescription(Talent t) {
    String padded = String.format(
                    "%-" + TALENT_PAD + "s", t.getName())
            .replace(' ', '.');
    return String.format("%s Lv %d/%d  [%s]",
            padded, t.getCurrentLevel(),
            t.getMaxLevel(),
            t.getCategory().name());
  }

  /**
   * Equipment listed individually with weight,
   * gear bonus, and word-wrapped descriptions.
   */
  public String formatEquipmentList(
          Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("EQUIPMENT"));
    for (Equipment e :
            explorer.getEquipment()) {
      sb.append(String.format(
              "  %s [%s, Gear +%d]\n",
              e.getName(),
              e.getWeight().name(),
              e.getGearBonus()));
      if (e.getDescription() != null
              && !e.getDescription()
              .isBlank()) {
        sb.append("    ")
                .append(wordWrap(
                        e.getDescription(),
                        WRAP_WIDTH - 4))
                .append("\n");
      }
    }
    return sb.toString();
  }

  /**
   * Single-line compact summary card.
   */
  public String toCompactCard(Explorer explorer) {
    return String.format(
            "%s | %s (%s) | HP:%d",
            explorer.getName(),
            explorer.getClass().getSimpleName(),
            explorer.getSpecialty() != null
                    ? explorer.getSpecialty().getName()
                    : "—",
            explorer.getAttribute(Attribute.STRENGTH)
                    + explorer.getAttribute(
                    Attribute.AGILITY));
  }

  /**
   * Word-wrap a long string at the specified width.
   */
  public String wordWrap(String text, int width) {
    if (text.length() <= width) return text;
    StringBuilder sb = new StringBuilder();
    String[] words = text.split(" ");
    int lineLen = 0;
    boolean atLineStart = true;
    for (String word : words) {
      if (lineLen + word.length() > width
              && lineLen > 0) {
        sb.append("\n    ");
        lineLen = 4;
        atLineStart = true;
      }
      if (!atLineStart) {
        sb.append(" ");
        lineLen++;
      }
      sb.append(word);
      lineLen += word.length();
      atLineStart = false;
    }
    return sb.toString();
  }

  /**
   * Optional search-highlight: wraps matches
   * in >>markers<< for console display.
   */
  public String highlightTerm(
          String text, String term) {
    if (term == null || term.isEmpty()) {
      return text;
    }
    return text.replaceAll(
            "(?i)(" + Pattern.quote(term) + ")",
            ">>$1<<");
  }

  private String sectionLabel(String label) {
    return String.format(
            "╠%s╣\n"
                    + "║ %-" + (SHEET_WIDTH - 2)
                    + "s ║\n",
            "═".repeat(SHEET_WIDTH), label);
  }

  private String centerPad(
          String text, int width) {
    int pad = (width - text.length()) / 2;
    return " ".repeat(Math.max(0, pad)) + text;
  }

  private String formatIdentitySection(
          Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("IDENTITY"));
    sb.append(String.format(
            "  Name:       %s\n",
            explorer.getName()));
    sb.append(String.format(
            "  Profession: %s\n",
            explorer.getClass().getSimpleName()));
    if (explorer.getSpecialty() != null) {
      sb.append(String.format(
              "  Specialty:  %s\n",
              explorer.getSpecialty().getName()));
    }
    if (explorer.getOrigin() != null) {
      sb.append(String.format(
              "  Origin:     %s\n",
              explorer.getOrigin().getLocation()));
    }
    return sb.toString();
  }

  private String formatPersonalDetails(
          Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("PERSONAL"));
    if (explorer.getQuirk() != null) {
      sb.append(String.format(
              "  Quirk:      %s\n",
              wordWrap(explorer.getQuirk(),
                      WRAP_WIDTH)));
    }
    if (explorer.getKeepsake() != null) {
      sb.append(String.format(
              "  Keepsake:   %s\n",
              explorer.getKeepsake()));
    }
    if (explorer.getAppearance() != null) {
      sb.append(String.format(
              "  Appearance: %s\n",
              wordWrap(explorer.getAppearance(),
                      WRAP_WIDTH)));
    }
    return sb.toString();
  }

  private String formatFooter() {
    return String.format(
            "╚%s╝\n",
            "═".repeat(SHEET_WIDTH));
  }

}