package darkforge.model;

/**
 * A piece of equipment in Coriolis.
 * Equipment has weight and a gear bonus that adds dice to rolls.
 * Extends GameEntity because equipment has a name and description.
 *
 * Legacy class retained for backward
 * compatibility with the Iteration 1–3 starting-gear data path and
 * serialization format. New code (catalog, GUI, character sheet)
 * uses the CharacterItem hierarchy. Use {@link #toCharacterItem()}
 * when bridging from legacy starting gear into the new model.
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
  // Migration to new Item hierarchy
  // =========================================

  /**
   * Adapt this legacy Equipment to a CharacterItem so it can flow
   * into Explorer.itemInventory.
   *
   * Note: legacy Equipment lacks cost, category, tech level,
   * damage, range, and weapon-type data. Weapons therefore convert
   * to a generic CharacterItem (not a Weapon subclass) and cannot
   * be equipped via the GUI Equip button. To equip a starting
   * weapon, replace it with the equivalent catalog Weapon entry
   * via the catalog browser. Iteration 5 work: change
   * ExplorerFactory.assignRandomEquipment() to resolve starting
   * gear from the catalog by name so starting weapons become real
   * Weapon instances.
   */
  public CharacterItem toCharacterItem() {
    EquipmentWeight wc =
            (weight != null) ? weight : EquipmentWeight.fromWeight(0);
    double rawWeight =
            (weight != null) ? weight.getWeightValue() : 0.0;
    String category = weapon ? "Starting Weapon" : "Starting Gear";
    return new CharacterItem(
            getName(),
            getDescription(),
            rawWeight,
            0,                 // cost unknown for legacy Equipment
            category,
            TechLevel.ORDINARY,
            false,             // not restricted
            wc,
            gearBonus,
            true               // mark as starting gear
    );
  }

  // =========================================
  // Display
  // =========================================

  @Override
  public String display() {
    String bonus = (gearBonus > 0)
            ? " (Gear +" + gearBonus + ")" : "";
    String tag = weapon ? " \u2694" : "";
    return String.format("%s [%s]%s%s",
            name, weight.getDisplayName(), bonus, tag);
  }
}
