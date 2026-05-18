package darkforge.facade;

import darkforge.creation.*;
import darkforge.data.GameDataProvider;
import darkforge.exception.*;
import darkforge.model.*;
import java.util.*;

/**
 * Façade for the darkforge.creation and
 * darkforge.data packages. Wraps Explorer
 * creation, name generation, search, and
 * game data access behind simple methods.
 */
public class FacadeCreation {
    private static final FacadeCreation INSTANCE =
            new FacadeCreation();
    private final ExplorerFactory factory;
    private final NameGenerator nameGenerator;
    private final GameDataProvider data;

    private FacadeCreation() {
        this.data =
                GameDataProvider.getInstance();
        data.initialize();
        this.factory =
                new ExplorerFactory(
                        new Random(), data);
        this.nameGenerator =
                new NameGenerator(
                        new Random(), data);
    }

    public static FacadeCreation
    getTheInstance() {
        return INSTANCE;
    }

    /**
     * Create a fully validated Explorer using
     * domain objects.
     *
     * @throws InvalidProfessionException
     *         if professionName is not recognized
     * @throws InvalidAttributeDistributionException
     *         if attributes violate constraints
     * @throws IncompatibleTalentException
     *         if talent allocation is invalid
     */
    public Explorer createExplorer(
            String professionName, Origin origin,
            int specialtyIndex,
            EnumMap<Attribute, Integer> attributes,
            int[] talentPoints,
            String quirk, String keepsake,
            String appearance, String name)
            throws InvalidProfessionException,
            InvalidAttributeDistributionException,
            IncompatibleTalentException {
        return factory.createExplorer(
                professionName, origin,
                specialtyIndex, attributes,
                talentPoints, quirk, keepsake,
                appearance, name);
    }

    /**
     * Convenience overload: create a fully
     * validated Explorer from primitive values.
     * Converts origin index to Origin object
     * and int[] attributes to EnumMap.
     *
     * @param professionName profession name
     * @param name           explorer name
     * @param originIndex    0-based index into
     *                       the origins list
     * @param specialtyIndex 0-based specialty
     *                       index
     * @param attributeValues 6-element int[]
     *        in Attribute.values() order
     * @param talentPoints   points per key talent
     * @param quirk          quirk text
     * @param keepsake       keepsake text
     * @param appearance     appearance text
     * @throws InvalidProfessionException
     *         if professionName is not recognized
     * @throws InvalidAttributeDistributionException
     *         if attributes violate constraints
     * @throws IncompatibleTalentException
     *         if talent allocation is invalid
     */
    public Explorer createExplorer(
            String professionName, String name,
            int originIndex, int specialtyIndex,
            int[] attributeValues,
            int[] talentPoints,
            String quirk, String keepsake,
            String appearance)
            throws InvalidProfessionException,
            InvalidAttributeDistributionException,
            IncompatibleTalentException {

        // Resolve origin by index
        List<Origin> origins = data.getOrigins();
        Origin origin = null;
        if (originIndex >= 0
                && originIndex < origins.size()) {
            origin = origins.get(originIndex);
        }

        // Convert int[] to EnumMap
        EnumMap<Attribute, Integer> attributes =
                new EnumMap<>(Attribute.class);
        Attribute[] attrValues =
                Attribute.values();
        for (int i = 0;
             i < attrValues.length
                     && i < attributeValues.length;
             i++) {
            attributes.put(attrValues[i],
                    attributeValues[i]);
        }

        return factory.createExplorer(
                professionName, origin,
                specialtyIndex, attributes,
                talentPoints, quirk, keepsake,
                appearance, name);
    }

    /**
     * Generate a random name for a profession.
     */
    public String generateName(
            String profession) {
        return nameGenerator.generateName(
                profession);
    }

    /**
     * Generate multiple name suggestions for
     * a profession.
     *
     * @param profession the profession name
     * @param count      number of names to
     *                   generate
     * @return list of formatted names
     */
    public List<String> suggestNames(
            String profession, int count) {
        return nameGenerator.generateNames(
                profession, count);
    }

    /**
     * Search entities by name with relevance
     * ranking (exact > starts-with > contains).
     */
    public <T extends GameEntity>
    List<T> searchByName(
            List<T> entities, String query) {
        return SearchUtil.searchByNameRanked(
                entities, query);
    }

    /**
     * Get the list of valid profession names
     * from game data.
     */
    public List<String> getValidProfessions() {
        return data.getValidProfessionNames();
    }

    /**
     * Get all loaded Origins from game data.
     */
    public List<Origin> getOrigins() {
        return data.getOrigins();
    }
}