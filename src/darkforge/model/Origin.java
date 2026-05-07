package darkforge.model;

/**
 * An Explorer's origin in Coriolis — where they come from.
 * Origins are determined by a D66 roll and grant a free talent,
 * an associated faction, and a contact NPC.
 *
 * <p>
 * Not a GameEntity — origins are data records, not named display entities.
 * </p>
 */
public class Origin {
  private final String location;
  private final Talent freeTalent;
  private final String associatedFaction;
  private final String contact;
  private final int d66RangeLow;
  private final int d66RangeHigh;

  /**
   * Constructs an Origin.
   *
   * @param location          the origin location description
   * @param freeTalent        the talent granted by this origin at level 1
   * @param associatedFaction the faction associated with this origin
   * @param contact           the contact NPC name/description
   * @param d66RangeLow       the low end of the D66 range (e.g., 11)
   * @param d66RangeHigh      the high end of the D66 range (e.g., 12)
   * @throws IllegalArgumentException if D66 range values have invalid digits
   */
  public Origin(String location, Talent freeTalent, String associatedFaction,
      String contact, int d66RangeLow, int d66RangeHigh) {
    validateD66Value(d66RangeLow);
    validateD66Value(d66RangeHigh);
    if (d66RangeLow > d66RangeHigh) {
      throw new IllegalArgumentException(
          "D66 range low (" + d66RangeLow + ") must be <= high (" + d66RangeHigh + ")");
    }
    this.location = location;
    this.freeTalent = freeTalent;
    this.associatedFaction = associatedFaction;
    this.contact = contact;
    this.d66RangeLow = d66RangeLow;
    this.d66RangeHigh = d66RangeHigh;
  }

  private static void validateD66Value(int value) {
    int tens = value / 10;
    int ones = value % 10;
    if (tens < 1 || tens > 6 || ones < 1 || ones > 6) {
      throw new IllegalArgumentException(
          "Invalid D66 value: " + value + " (both digits must be 1-6)");
    }
  }

  public String getLocation() {
    return location;
  }

  public Talent getFreeTalent() {
    return freeTalent;
  }

  public String getAssociatedFaction() {
    return associatedFaction;
  }

  public String getContact() {
    return contact;
  }

  public int getD66RangeLow() {
    return d66RangeLow;
  }

  public int getD66RangeHigh() {
    return d66RangeHigh;
  }

  /**
   * Returns true if this origin matches the given D66 value.
   */
  public boolean matchesD66(int d66Value) {
    return d66Value >= d66RangeLow && d66Value <= d66RangeHigh;
  }

  @Override
  public String toString() {
    return location + " (D66: " + d66RangeLow + "-" + d66RangeHigh + ")";
  }
}
