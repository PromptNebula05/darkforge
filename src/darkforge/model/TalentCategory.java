package darkforge.model;

public enum TalentCategory {
    COMBAT("Combat"),
    SOCIAL("Social"),
    VEHICLE_EXO("Vehicle & Exo"),
    KNOWLEDGE("Knowledge"),
    INSIGHT("Insight"),
    EQUIPMENT("Equipment"),
    RECOVERY("Recovery"),
    STEALTH_MOBILITY("Stealth & Mobility"),
    RESILIENCE("Resilience");

    private final String displayName;

    TalentCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
