package darkforge.model;

import darkforge.mechanics.D6Table;
import java.util.*;

/**
 * Represents an Explorer's origin in the Coriolis: The Great Dark setting.
 * Origins are selected from the D66 Origin Table (Ch. 2).
 *
 * Each origin provides:
 * - A free talent at level 1
 * - An associated faction (fixed, or variable via D6 roll)
 * - A contact (determined by D6 roll: 1-2, 3-4, 5-6)
 * - A D66 range for random selection
 */
public class Origin {
  private final String location;
  private final Talent freeTalent;
  private final String fixedFaction;
  private final D6Table<String> factionTable;
  private final Map<Integer, String> contacts;
  private final int d66RangeLow;
  private final int d66RangeHigh;

  /**
   * Creates an Origin with a fixed associated faction.
   *
   * @param location     the origin location name
   * @param freeTalent   the talent granted at level 1
   * @param fixedFaction the associated faction name
   * @param contacts     D6 value (1-6) mapped to contact name; pairs share values
   *                     (1-2, 3-4, 5-6)
   * @param d66RangeLow  lower bound of the D66 range
   * @param d66RangeHigh upper bound of the D66 range
   */
  public Origin(String location, Talent freeTalent, String fixedFaction,
      Map<Integer, String> contacts, int d66RangeLow, int d66RangeHigh) {
    validateD66Value(d66RangeLow);
    validateD66Value(d66RangeHigh);
    if (d66RangeLow > d66RangeHigh) {
      throw new IllegalArgumentException(
          "D66 range low (" + d66RangeLow + ") must be <= high (" + d66RangeHigh + ")");
    }
    validateContacts(contacts);
    this.location = location;
    this.freeTalent = freeTalent;
    this.fixedFaction = fixedFaction;
    this.factionTable = null;
    this.contacts = Map.copyOf(contacts);
    this.d66RangeLow = d66RangeLow;
    this.d66RangeHigh = d66RangeHigh;
  }

  /**
   * Creates an Origin with a variable associated faction (determined by D6 roll).
   *
   * @param location     the origin location name
   * @param freeTalent   the talent granted at level 1
   * @param factionTable D6 table mapping roll results to faction names
   * @param contacts     D6 value (1-6) mapped to contact name
   * @param d66RangeLow  lower bound of the D66 range
   * @param d66RangeHigh upper bound of the D66 range
   */
  public Origin(String location, Talent freeTalent, D6Table<String> factionTable,
      Map<Integer, String> contacts, int d66RangeLow, int d66RangeHigh) {
    validateD66Value(d66RangeLow);
    validateD66Value(d66RangeHigh);
    if (d66RangeLow > d66RangeHigh) {
      throw new IllegalArgumentException(
          "D66 range low (" + d66RangeLow + ") must be <= high (" + d66RangeHigh + ")");
    }
    validateContacts(contacts);
    this.location = location;
    this.freeTalent = freeTalent;
    this.fixedFaction = null;
    this.factionTable = factionTable;
    this.contacts = Map.copyOf(contacts);
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

  private static void validateContacts(Map<Integer, String> contacts) {
    for (int i = 1; i <= 6; i++) {
      if (!contacts.containsKey(i)) {
        throw new IllegalArgumentException(
            "Contacts map must contain entry for D6 value: " + i);
      }
    }
  }

  public String getLocation() {
    return location;
  }

  public Talent getFreeTalent() {
    return freeTalent;
  }

  /** Returns true if this origin's faction is determined by a D6 roll. */
  public boolean hasVariableFaction() {
    return factionTable != null;
  }

  /**
   * Returns the fixed associated faction.
   * 
   * @throws IllegalStateException if the faction is variable (use getFaction(int)
   *                               instead)
   */
  public String getAssociatedFaction() {
    if (fixedFaction == null) {
      throw new IllegalStateException(
          "Origin '" + location + "' has a variable faction — use getFaction(int d6Value)");
    }
    return fixedFaction;
  }

  /**
   * Returns the faction for a given D6 roll. Works for both fixed and variable
   * factions.
   * 
   * @param d6Value a value from 1 to 6
   */
  public String getFaction(int d6Value) {
    if (factionTable != null) {
      return factionTable.getResult(d6Value);
    }
    return fixedFaction;
  }

  /**
   * Returns the D6Table for variable faction selection, or null if faction is
   * fixed.
   */
  public D6Table<String> getFactionTable() {
    return factionTable;
  }

  /**
   * Returns the contact for a given D6 roll value (1-6).
   * Contacts are grouped in pairs: 1-2, 3-4, 5-6.
   */
  public String getContact(int d6Value) {
    if (d6Value < 1 || d6Value > 6) {
      throw new IllegalArgumentException("D6 value must be 1-6, got " + d6Value);
    }
    return contacts.get(d6Value);
  }

  /** Returns the full contacts map (D6 value 1-6 to contact name). */
  public Map<Integer, String> getContacts() {
    return contacts;
  }

  public int getD66RangeLow() {
    return d66RangeLow;
  }

  public int getD66RangeHigh() {
    return d66RangeHigh;
  }

  public boolean matchesD66(int d66Value) {
    return d66Value >= d66RangeLow && d66Value <= d66RangeHigh;
  }

  @Override
  public String toString() {
    return location + " (D66: " + d66RangeLow + "-" + d66RangeHigh + ")";
  }
}
