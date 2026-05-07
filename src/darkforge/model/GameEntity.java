package darkforge.model;

public abstract class GameEntity {
  protected final String name;
  protected final String description;

  protected GameEntity(String name, String description) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("GameEntity name cannot be null or blank");
    }
    this.name = name;
    this.description = (description != null) ? description : "";
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  // Subclasses MUST provide their own display format
  public abstract String display();

  @Override
  public String toString() {
    return display();
  }
}
