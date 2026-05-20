package darkforge.facade;

import darkforge.data.GameDataProvider;
import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Façade singleton for the darkforge.model package.
 * Provides access to profession listings, attribute
 * enums, and talent category enums.
 */
public class FacadeModel {
    private static final FacadeModel INSTANCE =
            new FacadeModel();

    private FacadeModel() {}

    public static FacadeModel getTheInstance() {
        return INSTANCE;
    }

    /**
     * Returns all 8 valid profession names as
     * loaded from professions.json via
     * GameDataProvider.
     */
    public List<String> getAvailableProfessions() {
        return GameDataProvider.getTheInstance()
                .getValidProfessionNames();
    }

    /**
     * Returns all 6 attribute enum names.
     */
    public List<String> getAttributeNames() {
        return Arrays.stream(Attribute.values())
                .map(Attribute::name)
                .collect(Collectors.toList());
    }

    /**
     * Returns all TalentCategory enum constants.
     */
    public TalentCategory[] getTalentCategories() {
        return TalentCategory.values();
    }

    /**
     * Look up an Attribute by its enum name.
     * Returns null if no match.
     */
    public Attribute getAttributeByName(
            String name) {
        try {
            return Attribute.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}