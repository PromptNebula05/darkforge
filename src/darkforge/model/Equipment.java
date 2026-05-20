package darkforge.model;

/**
 * A piece of equipment in Coriolis.
 * Equipment has weight and a gear bonus that adds dice to rolls.
 * Extends GameEntity because equipment has a name and description.
 */
public class Equipment extends GameEntity {

  private final EquipmentWeight weight;
  private final int gearBonus;
  private final boolean weapon;

  // =========================================
  // Constructors
  // =========================================

  public Equipment(String name, String description,
                   EquipmentWeight weight, int gearBonus,
                   boolean weapon) {
    super(name, description);
    if (gearBonus < 0) {
      throw new IllegalArgumentException(
              "Gear bonus cannot be negative, got " + gearBonus);
    }
    this.weight = weight;
    this.gearBonus = gearBonus;
    this.weapon = weapon;
  }

  // Non-weapon convenience constructor (backwards-compatible)
  public Equipment(String name, String description,
                   EquipmentWeight weight, int gearBonus) {
    this(name, description, weight, gearBonus, false);
  }

  // Non-weapon, zero gear bonus convenience constructor
  public Equipment(String name, String description,
                   EquipmentWeight weight) {
    this(name, description, weight, 0, false);
  }

  // =========================================
  // Getters
  // =========================================

  public EquipmentWeight getWeight() {
    return weight;
  }

  public int getGearBonus() {
    return gearBonus;
  }

  public boolean isWeapon() {
    return weapon;
  }

  // =========================================
  // Display
  // =========================================

  @Override
  public String display() {
    String bonus = (gearBonus > 0)
            ? " (Gear +" + gearBonus + ")" : "";
    String tag = weapon ? " ⚔" : "";
    return String.format("%s [%s]%s%s",
            name, weight.getDisplayName(), bonus, tag);
  }
}