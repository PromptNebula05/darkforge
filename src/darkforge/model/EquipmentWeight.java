package darkforge.model;

/**
 * Weight categories for equipment in Coriolis.
 * Affects carrying capacity calculations.
 */
public enum EquipmentWeight {
  TINY("Tiny", 0),
  LIGHT("Light", 1),
  REGULAR("Regular", 2),
  HEAVY("Heavy", 4);

  private final String displayName;
  private final int carryUnits;

  EquipmentWeight(String displayName, int carryUnits) {
    this.displayName = displayName;
    this.carryUnits = carryUnits;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getCarryUnits() {
    return carryUnits;
  }
}
