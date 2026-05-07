package darkforge.model;

/**
 * A piece of equipment in Coriolis.
 * Equipment has weight and a gear bonus that adds dice to rolls.
 *
 * <p>
 * Extends GameEntity because equipment has a name ("Fusillard Pistol")
 * and a description.
 * </p>
 */
public class Equipment extends GameEntity {
  private final EquipmentWeight weight;
  private final int gearBonus;

  /**
   * Constructs Equipment.
   *
   * @param name        the item name
   * @param description the item description
   * @param weight      the weight category
   * @param gearBonus   bonus dice added to rolls when using this item (>= 0)
   * @throws IllegalArgumentException if gearBonus is negative
   */
  public Equipment(String name, String description, EquipmentWeight weight, int gearBonus) {
    super(name, description);
    if (gearBonus < 0) {
      throw new IllegalArgumentException("Gear bonus cannot be negative, got " + gearBonus);
    }
    this.weight = weight;
    this.gearBonus = gearBonus;
  }

  /** Convenience constructor with gearBonus = 0. */
  public Equipment(String name, String description, EquipmentWeight weight) {
    this(name, description, weight, 0);
  }

  public EquipmentWeight getWeight() {
    return weight;
  }

  public int getGearBonus() {
    return gearBonus;
  }

  @Override
  public String display() {
    String bonus = (gearBonus > 0) ? " (Gear +" + gearBonus + ")" : "";
    return String.format("%s [%s]%s", name, weight.getDisplayName(), bonus);
  }
}
