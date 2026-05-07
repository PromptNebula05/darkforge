package darkforge.model;

public enum Attribute {
    STRENGTH("Strength", "STR"),
    AGILITY("Agility", "AGL"),
    LOGIC("Logic", "LOG"),
    PERCEPTION("Perception", "PER"),
    INSIGHT("Insight", "INS"),
    EMPATHY("Empathy", "EMP");

    private final String displayName;
    private final String abbreviation;
    Attribute(String displayName, String abbreviation) {
        this.displayName = displayName;
        this.abbreviation = abbreviation;
    }

    public String getDisplayName() { return displayName; }
    public String getAbbreviation() { return abbreviation; }
}