package darkforge.data;

import darkforge.model.Attribute;
import java.util.List;

/**
 * Immutable data record holding all static game
 * data for a single profession, loaded from
 * professions.json. Separates data definition
 * from behavior — profession subclasses retain
 * their behavioral polymorphism while this class
 * holds the configurable data.
 */
public class ProfessionData {
    private final String name;
    private final Attribute keyAttribute;
    private final List<SpecialtyData> specialties;
    private final List<TalentData> talents;
    private final List<List<EquipmentData>>
            startingEquipmentSets;

    public ProfessionData(
            String name,
            Attribute keyAttribute,
            List<SpecialtyData> specialties,
            List<TalentData> talents,
            List<List<EquipmentData>>
                    startingEquipmentSets) {
        this.name = name;
        this.keyAttribute = keyAttribute;
        this.specialties =
                List.copyOf(specialties);
        this.talents = List.copyOf(talents);
        this.startingEquipmentSets =
                startingEquipmentSets.stream()
                        .map(List::copyOf)
                        .toList();
    }

    public String getName() { return name; }
    public Attribute getKeyAttribute() {
        return keyAttribute;
    }
    public List<SpecialtyData> getSpecialties() {
        return specialties;
    }
    public List<TalentData> getTalents() {
        return talents;
    }
    public List<List<EquipmentData>>
    getStartingEquipmentSets() {
        return startingEquipmentSets;
    }

    /** Specialty definition from professions.json. */
    public record SpecialtyData(
            String name,
            String description,
            String freeTalentName) {}

    /** Talent definition from professions.json. */
    public record TalentData(
            String name,
            String category,
            int maxLevel,
            String description,
            String effect) {}

    /** Equipment item from professions.json. */
    public record EquipmentData(
            String name,
            String description,
            String weight,
            int gearBonus) {}
}