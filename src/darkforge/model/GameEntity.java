package darkforge.model;

import java.io.Serializable;

/**
 * Abstract root of all named game entities.
 * Implements Serializable for binary I/O evaluation.
 */
public abstract class GameEntity
        implements Serializable {

  private static final long
          serialVersionUID = 1L;

  // =========================================
  // Fields
  // =========================================

  protected final String name;
  protected final String description;

  // =========================================
  // Constructor
  // =========================================

  protected GameEntity(String name,
                       String description) {
    if (name == null
            || name.isBlank()) {
      throw new IllegalArgumentException(
              "Name cannot be null or"
                      + " blank");
    }
    this.name = name;
    this.description =
            (description != null)
                    ? description : "";
  }

  // =========================================
  // Getters
  // =========================================

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  // =========================================
  // Abstract
  // =========================================

  public abstract String display();

  // =========================================
  // Object overrides
  // =========================================

  @Override
  public String toString() {
    return name;
  }
}