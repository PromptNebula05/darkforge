package darkforge.facade;

import darkforge.creation.ExplorerFactory;
import darkforge.creation.NameGenerator;
import darkforge.creation.SearchUtil;
import darkforge.data.GameDataProvider;
import darkforge.exception.*;
import darkforge.model.*;

import java.util.*;

/**
 * Sub-facade for Explorer creation, name
 * generation, and search operations.
 *
 * Delegates to ExplorerFactory, NameGenerator,
 * SearchUtil, and GameDataProvider.
 * Package-private constructor — only
 * FacadeDarkforge instantiates.
 */
public class FacadeCreation {

    private final ExplorerFactory factory;
    private NameGenerator nameGen;
    private final GameDataProvider data;

    private static FacadeCreation theInstance =
            new FacadeCreation();

    FacadeCreation() {
        this.data =
                GameDataProvider.getTheInstance();
        this.factory = new ExplorerFactory();
    }

    private NameGenerator getNameGen() {
        if (nameGen == null) {
            nameGen = new NameGenerator(
                    new Random(), data);
        }
        return nameGen;
    }

    public static FacadeCreation getTheInstance() {
        return theInstance;
    }

    public static void setTheInstance(
            FacadeCreation theInstance) {
        FacadeCreation.theInstance = theInstance;
    }

    /**
     * Return valid profession names from
     * professions.json via GameDataProvider.
     */
    public List<String> getValidProfessions() {
        return data.getValidProfessionNames();
    }

    /**
     * Return all Origin objects from
     * origins.json via GameDataProvider.
     */
    public List<Origin> getOrigins() {
        return data.getOrigins();
    }

    /**
     * Generate a random Explorer name via
     * NameGenerator.
     */
    public String generateName(String profession) {
        return getNameGen().generateName(profession);
    }

    /**
     * Delegate full Explorer creation to
     * ExplorerFactory.
     *
     * @throws InvalidProfessionException
     *         if professionName is not recognized
     * @throws InvalidAttributeDistributionException
     *         if attributes violate constraints
     * @throws IncompatibleTalentException
     *         if talent allocation is invalid
     */
    public Explorer createExplorer(
            String profession,
            Origin origin,
            int specialtyIndex,
            EnumMap<Attribute, Integer> attrs,
            int[] talentPoints,
            String quirk,
            String keepsake,
            String appearance,
            String name)
            throws InvalidProfessionException,
            InvalidAttributeDistributionException,
            IncompatibleTalentException {
        return factory.createExplorer(
                profession, origin,
                specialtyIndex, attrs,
                talentPoints, quirk,
                keepsake, appearance, name);
    }

    /**
     * Search Explorers by name using SearchUtil.
     * Used by ConsoleMainMenu's search feature.
     */
    public List<Explorer> searchByName(
            List<Explorer> explorers,
            String query) {
        return SearchUtil.searchByName(
                explorers, query);
    }

    /**
     * Suggest multiple random names for a profession.
     * Used by tests and potential future UI features.
     */
    public List<String> suggestNames(
            String profession, int count) {
        return getNameGen().generateNames(
                profession, count);
    }
}