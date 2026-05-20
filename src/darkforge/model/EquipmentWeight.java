package darkforge.model;

/**
 * Weight categories for equipment in Coriolis (Ch. 6).
 * Refactored in Iteration 3: int carryUnits → double weightValue
 * to support EquipmentInventory encumbrance math.
 */
public enum EquipmentWeight {

  TINY("Tiny", 0.0),
  LIGHT("Light", 0.5),
  REGULAR("Regular", 1.0),
  HEAVY("Heavy", 2.0);

  // =========================================
  // Fields
  // =========================================

  private final String displayName;
  private final double weightValue;

  // =========================================
  // Constructor
  // =========================================

  EquipmentWeight(String displayName,
                  double weightValue) {
    this.displayName = displayName;
    this.weightValue = weightValue;
  }

  // =========================================
  // Getters
  // =========================================

  public String getDisplayName() {
    return displayName;
  }

  public double getWeightValue() {
    return weightValue;
  }
}