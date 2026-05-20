package darkforge.crew;

import darkforge.model.Attribute;
import darkforge.model.Explorer;

/**
 * The five crew roles from Coriolis: The Great Dark (Ch. 12).
 * Four are mandatory; Archaeologist is optional.
 * Each role has a primary/secondary attribute pair used to
 * compute role fitness, plus a starting crew maneuver.
 */
public enum CrewRole {

    DELVER("Delver", Attribute.AGILITY,
            Attribute.INSIGHT, "Rally"),

    SCOUT("Scout", Attribute.PERCEPTION,
            Attribute.LOGIC, "Situational Awareness"),

    BURROWER("Burrower", Attribute.STRENGTH,
            Attribute.LOGIC, "Destabilize"),

    GUARD("Guard", Attribute.STRENGTH,
            Attribute.PERCEPTION, "Flank"),

    ARCHAEOLOGIST("Archaeologist", Attribute.LOGIC,
            Attribute.INSIGHT, "Analyze");

    // =========================================
    // Fields
    // =========================================

    private final String displayName;
    private final Attribute primaryAttribute;
    private final Attribute secondaryAttribute;
    private final String startingManeuver;

    // =========================================
    // Constructor
    // =========================================

    CrewRole(String displayName,
             Attribute primaryAttribute,
             Attribute secondaryAttribute,
             String startingManeuver) {
        this.displayName = displayName;
        this.primaryAttribute = primaryAttribute;
        this.secondaryAttribute = secondaryAttribute;
        this.startingManeuver = startingManeuver;
    }

    // =========================================
    // Getters
    // =========================================

    public String getDisplayName() {
        return displayName;
    }

    public Attribute getPrimaryAttribute() {
        return primaryAttribute;
    }

    public Attribute getSecondaryAttribute() {
        return secondaryAttribute;
    }

    public String getStartingManeuver() {
        return startingManeuver;
    }

    // =========================================
    // Behavior
    // =========================================

    // Only ARCHAEOLOGIST is optional; the other four are mandatory.
    public boolean isOptional() {
        return this == ARCHAEOLOGIST;
    }

    // Sum of the Explorer's primary + secondary attribute values for this role.
    public int getRoleFitness(Explorer explorer) {
        return explorer.getAttribute(primaryAttribute)
                + explorer.getAttribute(secondaryAttribute);
    }

    // =========================================
    // Display
    // =========================================

    // Format: "Delver (AGL + INS) — Rally [mandatory]"
    @Override
    public String toString() {
        String optionalTag = isOptional()
                ? "optional" : "mandatory";
        return String.format(
                "%s (%s + %s) — %s [%s]",
                displayName,
                primaryAttribute.getAbbreviation(),
                secondaryAttribute.getAbbreviation(),
                startingManeuver,
                optionalTag);
    }
}