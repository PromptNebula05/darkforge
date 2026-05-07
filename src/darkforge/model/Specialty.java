package darkforge.model;

/**
 * A profession specialty in Coriolis.
 * Each profession has 6 specialties (rolled on D6).
 * A specialty grants a free talent at level 1.
 */
public class Specialty {
  private final String name;
  private final String description;
  private final Talent freeTalent;

  /**
   * Constructs a Specialty.
   *
   * @param name        the specialty name (e.g., "Algebraist Apprentice")
   * @param description brief description of the specialty
   * @param freeTalent  the talent granted by this specialty
   */
  public Specialty(String name, String description, Talent freeTalent) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Specialty name cannot be null or blank");
    }
    this.name = name;
    this.description = (description != null) ? description : "";
    this.freeTalent = freeTalent;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Talent getFreeTalent() {
    return freeTalent;
  }

  @Override
  public String toString() {
    return name;
  }
}
