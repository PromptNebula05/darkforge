package darkforge.display;

import darkforge.model.*;

public class CharacterSheetRenderer implements Displayable {

  private static final int WIDTH = 54;
  private static final String DOUBLE_LINE = "═".repeat(WIDTH);
  private static final String SINGLE_LINE = "─".repeat(WIDTH);
  private final Explorer explorer;

  public CharacterSheetRenderer(Explorer explorer) {
    if (explorer == null)
      throw new IllegalArgumentException("Explorer cannot be null");
    this.explorer = explorer;
  }

  @Override
  public String toFormattedString() {
    StringBuilder sb = new StringBuilder();
    sb.append(DOUBLE_LINE).append("\n");
    sb.append(centerText("DARKFORGE — Explorer Character Sheet")).append("\n");
    sb.append(DOUBLE_LINE).append("\n");

    sb.append(String.format(" Name:       %s%n", explorer.getName()));
    String spec = (explorer.getSpecialty() != null) ? " (" + explorer.getSpecialty().getName() + ")" : "";
    sb.append(String.format(" Profession: %s%s%n", explorer.getProfessionName(), spec));
    if (explorer.getOrigin() != null) {
      sb.append(String.format(" Origin:     %s%n", explorer.getOrigin().getLocation()));
      sb.append(String.format(" Faction:    %s%n", explorer.getResolvedFaction()));
      sb.append(String.format(" Contact:    %s%n", explorer.getResolvedContact()));
    }
    if (explorer.getQuirk() != null)
      sb.append(String.format(" Quirk:      %s%n", explorer.getQuirk()));
    if (explorer.getKeepsake() != null)
      sb.append(String.format(" Keepsake:   %s%n", explorer.getKeepsake()));
    if (explorer.getAppearance() != null)
      sb.append(String.format(" Appearance: %s%n", explorer.getAppearance()));
    if (explorer.getExplorerReason() != null)
      sb.append(String.format(" Why Explore: %s%n", explorer.getExplorerReason()));

    sb.append(SINGLE_LINE).append("\n");
    sb.append(" ATTRIBUTES          DERIVED STATS\n");
    Attribute keyAttr = explorer.getKeyAttribute();
    for (Attribute attr : Attribute.values()) {
      int value = explorer.getAttribute(attr);
      String keyMarker = (attr == keyAttr) ? " [KEY]" : "";
      String derivedStat = getDerivedStatForRow(attr);
      sb.append(String.format("  %s: %d%s%s%n", attr.getAbbreviation(), value, keyMarker, derivedStat));
    }
    int total = 0;
    for (Attribute attr : Attribute.values())
      total += explorer.getAttribute(attr);
    sb.append(String.format("  (Total: %d)%n", total));

    sb.append(SINGLE_LINE).append("\n");
    sb.append(" TALENTS\n");
    for (Talent talent : explorer.getTalents()) {
      String dots = ".".repeat(Math.max(1, 24 - talent.getName().length()));
      sb.append(
          String.format("  %s %s Lv %d/%d%n", talent.getName(), dots, talent.getCurrentLevel(), talent.getMaxLevel()));
    }

    sb.append(SINGLE_LINE).append("\n");
    sb.append(DOUBLE_LINE).append("\n");
    return sb.toString();
  }

  @Override
  public String toSummary() {
    String spec = (explorer.getSpecialty() != null) ? " (" + explorer.getSpecialty().getName() + ")" : "";
    return String.format("%s — %s%s — Health %d / Hope %d / Heart %d",
        explorer.getName(), explorer.getProfessionName(), spec,
        explorer.getHealth(), explorer.getHope(), explorer.getHeart());
  }

  private String getDerivedStatForRow(Attribute attr) {
    return switch (attr) {
      case STRENGTH -> String.format("%sHealth: %d  (STR + AGL)",
          pad(14 - String.valueOf(explorer.getAttribute(attr)).length()), explorer.getHealth());
      case LOGIC -> String.format("%sHope:   %d  (LOG + EMP)",
          pad(14 - String.valueOf(explorer.getAttribute(attr)).length()), explorer.getHope());
      case INSIGHT -> String.format("%sHeart:  %d  (INS + PER)",
          pad(14 - String.valueOf(explorer.getAttribute(attr)).length()), explorer.getHeart());
      default -> "";
    };
  }

  private String pad(int n) {
    return " ".repeat(Math.max(1, n));
  }

  private String centerText(String text) {
    return " ".repeat(Math.max(0, (WIDTH - text.length()) / 2)) + text;
  }
}
