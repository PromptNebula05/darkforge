package darkforge.facade;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Façade for the darkforge.model package.
 * Provides convenient access to enums, constants,
 * and factory methods for model data classes.
 */
public class FacadeModel {
    private static final FacadeModel INSTANCE =
            new FacadeModel();

    private FacadeModel() {}

    public static FacadeModel getTheInstance() {
        return INSTANCE;
    }

    /**
     * Return all valid profession names from
     * game data.
     */
    public List<String> getAvailableProfessions() {
        return GameDataProvider.getInstance()
                .getValidProfessionNames();
    }

    /**
     * Return all Attribute enum values.
     */
    public List<Attribute> getAttributeList() {
        return List.of(Attribute.values());
    }

    /**
     * Return all attribute display names.
     */
    public List<String> getAttributeNames() {
        return Arrays.stream(Attribute.values())
                .map(Attribute::name)
                .collect(Collectors.toList());
    }

    /**
     * Look up an Attribute by its enum name.
     * Returns null if the name is not recognized.
     */
    public Attribute getAttributeByName(
            String name) {
        try {
            return Attribute.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Return all TalentCategory enum values
     * as an array.
     */
    public TalentCategory[]
    getTalentCategories() {
        return TalentCategory.values();
    }

    /**
     * Create a new Talent instance.
     */
    public Talent createTalent(
            String name, String description,
            TalentCategory category,
            int maxLevel, String effect) {
        return new Talent(name, description,
                category, maxLevel, effect);
    }

    /**
     * Create a new Equipment instance.
     */
    public Equipment createEquipment(
            String name, String description,
            EquipmentWeight weight,
            int gearBonus) {
        return new Equipment(name, description,
                weight, gearBonus);
    }
}