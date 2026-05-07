package darkforge.model;

/**
 * A talent in Coriolis — a learned skill or special ability.
 * Talents have a category, a current level, and a maximum level.
 *
 * <p>
 * Extends GameEntity because a talent has a name ("Smart")
 * and a description of its effect.
 * </p>
 */
public class Talent extends GameEntity {
  private final TalentCategory category;
  private final int maxLevel;
  private int currentLevel;
  private final String effect;

  /**
   * Constructs a Talent.
   *
   * @param name         the talent's name (e.g., "Smart")
   * @param description  brief description
   * @param category     the talent category from Ch. 3
   * @param maxLevel     maximum achievable level (typically 3)
   * @param currentLevel current level (0 = untrained)
   * @param effect       the talent's mechanical effect description
   * @throws IllegalArgumentException if currentLevel < 0 or > maxLevel
   */
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

  /** Convenience constructor with currentLevel = 1. */
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

  /**
   * Increases the talent level by 1.
   *
   * @throws IllegalStateException if already at max level
   */
  public void increaseLevel() {
    if (currentLevel >= maxLevel) {
      throw new IllegalStateException(
          "Talent '" + name + "' is already at max level " + maxLevel);
    }
    currentLevel++;
  }

  /**
   * Sets the current level directly.
   *
   * @param level the new level
   * @throws IllegalArgumentException if level is out of bounds
   */
  public void setCurrentLevel(int level) {
    if (level < 0 || level > maxLevel) {
      throw new IllegalArgumentException(
          "Level must be 0-" + maxLevel + " for '" + name + "', got " + level);
    }
    this.currentLevel = level;
  }

  @Override
  public String display() {
    return String.format("%s [%s] Lv %d/%d — %s",
        name, category.getDisplayName(), currentLevel, maxLevel, effect);
  }
}
