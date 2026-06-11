package darkforge.model;

import darkforge.mechanics.D6Table;
import java.util.*;

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
   */
  public Origin(String location, Talent freeTalent,
                String fixedFaction,
                Map<Integer, String> contacts,
                int d66RangeLow, int d66RangeHigh) {
    validateD66Value(d66RangeLow);
    validateD66Value(d66RangeHigh);
    if (d66RangeLow > d66RangeHigh) {
      throw new IllegalArgumentException(
              "D66 range low (" + d66RangeLow
                      + ") must be <= high ("
                      + d66RangeHigh + ")");
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
   * Creates an Origin with a variable associated
   * faction (determined by D6 roll).
   */
  public Origin(String location, Talent freeTalent,
                D6Table<String> factionTable,
                Map<Integer, String> contacts,
                int d66RangeLow, int d66RangeHigh) {
    validateD66Value(d66RangeLow);
    validateD66Value(d66RangeHigh);
    if (d66RangeLow > d66RangeHigh) {
      throw new IllegalArgumentException(
              "D66 range low (" + d66RangeLow
                      + ") must be <= high ("
                      + d66RangeHigh + ")");
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

  /**
   * Deserialization constructor — creates an
   * Origin from save file data where contacts
   * are not stored. Skips contacts validation
   * and populates placeholder contacts.
   */
  public Origin(String location, Talent freeTalent,
                String fixedFaction,
                int d66RangeLow, int d66RangeHigh) {
    this.location = location;
    this.freeTalent = freeTalent;
    this.fixedFaction = fixedFaction;
    this.factionTable = null;
    this.contacts = Map.of(
            1, "", 2, "", 3, "",
            4, "", 5, "", 6, "");
    this.d66RangeLow = d66RangeLow;
    this.d66RangeHigh = d66RangeHigh;
  }

  private static void validateD66Value(int value) {
    int tens = value / 10;
    int ones = value % 10;
    if (tens < 1 || tens > 6
            || ones < 1 || ones > 6) {
      throw new IllegalArgumentException(
              "Invalid D66 value: " + value
                      + " (both digits must be 1-6)");
    }
  }

  private static void validateContacts(
          Map<Integer, String> contacts) {
    for (int i = 1; i <= 6; i++) {
      if (!contacts.containsKey(i)) {
        throw new IllegalArgumentException(
                "Contacts map must contain "
                        + "entry for D6 value: " + i);
      }
    }
  }

  public String getLocation() { return location; }
  public Talent getFreeTalent() {
    return freeTalent;
  }

  public boolean hasVariableFaction() {
    return factionTable != null;
  }

  public String getAssociatedFaction() {
    if (fixedFaction == null) {
      throw new IllegalStateException(
              "Origin '" + location + "' has a "
                      + "variable faction — use "
                      + "getFaction(int d6Value)");
    }
    return fixedFaction;
  }

  public String getFaction(int d6Value) {
    if (factionTable != null) {
      return factionTable.getResult(d6Value);
    }
    return fixedFaction;
  }

  public D6Table<String> getFactionTable() {
    return factionTable;
  }

  public String getContact(int d6Value) {
    if (d6Value < 1 || d6Value > 6) {
      throw new IllegalArgumentException(
              "D6 value must be 1-6, got "
                      + d6Value);
    }
    return contacts.get(d6Value);
  }

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
    return d66Value >= d66RangeLow
            && d66Value <= d66RangeHigh;
  }

  @Override
  public String toString() {
    return location + " (D66: " + d66RangeLow
            + "-" + d66RangeHigh + ")";
  }
}