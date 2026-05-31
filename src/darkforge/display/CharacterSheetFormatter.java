package darkforge.display;

import darkforge.model.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Formats Explorer data into rich text character sheets
 * for console display.
 *
 * EQUIPMENT section now reads
 * Explorer.getAllItems() (the unified itemInventory) so both
 * starting gear (bridged from legacy Equipment) and catalog
 * additions (CharacterItem / Weapon / Armor) appear together.
 */
public class CharacterSheetFormatter {
  private static final int SHEET_WIDTH = 50;
  private static final int TALENT_PAD = 24;
  private static final int WRAP_WIDTH = 50;

  public String formatCharacterSheet(Explorer explorer) {
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

  private String formatHeader(Explorer explorer) {
    String title = "DARKFORGE \u2014 Explorer Character Sheet";
    return String.format(
            "\u2554%s\u2557\n"
                    + "\u2551 %s \u2551\n"
                    + "\u2560%s\u2563\n",
            "\u2550".repeat(SHEET_WIDTH),
            centerPad(title, SHEET_WIDTH),
            "\u2550".repeat(SHEET_WIDTH));
  }

  public String formatAttributeBlock(Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("ATTRIBUTES"));
    for (Attribute a : Attribute.values()) {
      int val = explorer.getAttribute(a);
      String marker = (a == explorer.getKeyAttribute())
              ? " \u2605" : "";
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

  public String formatTalentSection(Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("TALENTS"));
    for (Talent t : explorer.getTalents()) {
      sb.append("  ")
              .append(formatTalentDescription(t))
              .append("\n");
    }
    return sb.toString();
  }

  public String formatTalentDescription(Talent t) {
    String padded = String.format(
                    "%-" + TALENT_PAD + "s", t.getName())
            .replace(' ', '.');
    return String.format("%s Lv %d/%d [%s]",
            padded, t.getCurrentLevel(),
            t.getMaxLevel(),
            t.getCategory().name());
  }

  /**
   * Equipment section. Iteration 4 / Phase 9 — reads the
   * unified itemInventory via Explorer.getAllItems() so both
   * starting gear and catalog additions are displayed.
   * Equipped weapons are marked with ✦.
   */
  public String formatEquipmentList(Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("EQUIPMENT"));
    List<CharacterItem> items = explorer.getAllItems();
    if (items.isEmpty()) {
      sb.append("  (none)\n");
      return sb.toString();
    }
    List<Weapon> equippedWeapons = explorer.getEquipped();
    for (CharacterItem ci : items) {
      boolean isEquipped =
              (ci instanceof Weapon w)
                      && equippedWeapons.contains(w);
      String marker = isEquipped ? " \u2726" : "";
      String bonus = (ci.getGearBonus() > 0)
              ? String.format(", Gear +%d", ci.getGearBonus())
              : "";
      String extra = "";
      if (ci instanceof Weapon wp) {
        extra = String.format(
                " Dmg %d | %s",
                wp.getDamage(), wp.getRange());
      }
      sb.append(String.format(
              "  %s [%s%s]%s%s\n",
              ci.getName(),
              ci.getWeightClass().getDisplayName(),
              bonus, extra, marker));
      if (ci.getDescription() != null
              && !ci.getDescription().isBlank()) {
        sb.append("    ")
                .append(wordWrap(
                        ci.getDescription(), WRAP_WIDTH - 4))
                .append("\n");
      }
    }
    sb.append(String.format(
            "  Load: %.1f / %.1f\n",
            explorer.getCurrentCarryWeight(),
            explorer.getMaxCarryWeight()));
    return sb.toString();
  }

  public String toCompactCard(Explorer explorer) {
    return String.format(
            "%s | %s (%s) | HP:%d",
            explorer.getName(),
            explorer.getClass().getSimpleName(),
            explorer.getSpecialty() != null
                    ? explorer.getSpecialty().getName() : "\u2014",
            explorer.getAttribute(Attribute.STRENGTH)
                    + explorer.getAttribute(Attribute.AGILITY));
  }

  public String wordWrap(String text, int width) {
    if (text.length() <= width) return text;
    StringBuilder sb = new StringBuilder();
    String[] words = text.split(" ");
    int lineLen = 0;
    boolean atLineStart = true;
    for (String word : words) {
      if (lineLen + word.length() > width && lineLen > 0) {
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

  public String highlightTerm(String text, String term) {
    if (term == null || term.isEmpty()) return text;
    return text.replaceAll(
            "(?i)(" + Pattern.quote(term) + ")",
            ">>$1<<");
  }

  private String sectionLabel(String label) {
    return String.format(
            "\u2560%s\u2563\n"
                    + "\u2551 %-" + (SHEET_WIDTH - 2) + "s \u2551\n",
            "\u2550".repeat(SHEET_WIDTH), label);
  }

  private String centerPad(String text, int width) {
    int pad = (width - text.length()) / 2;
    return " ".repeat(Math.max(0, pad)) + text;
  }

  private String formatIdentitySection(Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("IDENTITY"));
    sb.append(String.format(
            "  Name: %s\n", explorer.getName()));
    sb.append(String.format(
            "  Profession: %s\n",
            explorer.getClass().getSimpleName()));
    if (explorer.getSpecialty() != null) {
      sb.append(String.format(
              "  Specialty: %s\n",
              explorer.getSpecialty().getName()));
    }
    if (explorer.getOrigin() != null) {
      sb.append(String.format(
              "  Origin: %s\n",
              explorer.getOrigin().getLocation()));
    }
    return sb.toString();
  }

  private String formatPersonalDetails(Explorer explorer) {
    StringBuilder sb = new StringBuilder();
    sb.append(sectionLabel("PERSONAL"));
    if (explorer.getQuirk() != null) {
      sb.append(String.format(
              "  Quirk: %s\n",
              wordWrap(explorer.getQuirk(), WRAP_WIDTH)));
    }
    if (explorer.getKeepsake() != null) {
      sb.append(String.format(
              "  Keepsake: %s\n", explorer.getKeepsake()));
    }
    if (explorer.getAppearance() != null) {
      sb.append(String.format(
              "  Appearance: %s\n",
              wordWrap(explorer.getAppearance(), WRAP_WIDTH)));
    }
    return sb.toString();
  }

  private String formatFooter() {
    return String.format(
            "\u255A%s\u255D\n",
            "\u2550".repeat(SHEET_WIDTH));
  }
}
