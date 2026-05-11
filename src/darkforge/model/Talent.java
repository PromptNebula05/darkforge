package darkforge.model;

public class Talent extends GameEntity {
  private final TalentCategory category;
  private final int maxLevel;
  private int currentLevel;
  private final String effect;
  private String source; // e.g., "Origin", "Specialty", "Chosen"

  public Talent(String name, String description, TalentCategory category,
      int maxLevel, int currentLevel, String effect) {
    super(name, description);
    if (maxLevel < 1) {
      throw new IllegalArgumentException("Max level must be at least 1, got " + maxLevel);
    }
    if (currentLevel < 0 || currentLevel > maxLevel) {
      throw new IllegalArgumentException(
          "Current level must be 0-" + maxLevel + ", got " + currentLevel);
    }
    this.category = category;
    this.maxLevel = maxLevel;
    this.currentLevel = currentLevel;
    this.effect = (effect != null) ? effect : "";
  }

  public Talent(String name, String description, TalentCategory category,
      int maxLevel, String effect) {
    this(name, description, category, maxLevel, 1, effect);
  }

  public TalentCategory getCategory() {
    return category;
  }

  public int getMaxLevel() {
    return maxLevel;
  }

  public int getCurrentLevel() {
    return currentLevel;
  }

  public String getEffect() {
    return effect;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void increaseLevel() {
    if (currentLevel >= maxLevel) {
      throw new IllegalStateException(
          "Talent '" + name + "' is already at max level " + maxLevel);
    }
    currentLevel++;
  }

  public void setCurrentLevel(int level) {
    if (level < 0 || level > maxLevel) {
      throw new IllegalArgumentException(
          "Level must be 0-" + maxLevel + " for '" + name + "', got " + level);
    }
    this.currentLevel = level;
  }

  @Override
  public String display() {
    String src = (source != null) ? " (" + source + ")" : "";
    return String.format("%s [%s] Lv %d/%d%s — %s",
        name, category.getDisplayName(), currentLevel, maxLevel, src, effect);
  }
}
