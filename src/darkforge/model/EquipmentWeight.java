package darkforge.model;

/**
 * Weight categories for equipment in Coriolis.
 * Three new tiers for heavy equipment.
 */
public enum EquipmentWeight {

  TINY("Tiny", 0.0),
  LIGHT("Light", 0.5),
  REGULAR("Regular", 1.0),
  HEAVY("Heavy", 2.0),
  VERY_HEAVY("Very Heavy", 3.0),
  EXTRA_HEAVY("Extra Heavy", 4.0),
  MASSIVE("Massive", 5.0);

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

  // =========================================
  // Parsing
  // =========================================

  public static EquipmentWeight fromWeight(
          double wt) {
    EquipmentWeight closest = TINY;
    double minDiff = Double.MAX_VALUE;
    for (EquipmentWeight ew : values()) {
      double diff = Math.abs(
              ew.weightValue - wt);
      if (diff < minDiff) {
        minDiff = diff;
        closest = ew;
      }
    }
    return closest;
  }
}