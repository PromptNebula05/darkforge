package darkforge.data;

import darkforge.crew.BirdType;
import darkforge.crew.GarudaPower;
import darkforge.exception
        .GameDataLoadException;
import darkforge.model.TalentCategory;

import java.util.*;

/**
 * Validates all loaded game data for internal
 * consistency at startup. Runs after
 * GameDataProvider.initialize() to catch
 * malformed or incomplete JSON before any
 * game operations.
 *
 * Throws GameDataLoadException (unchecked) on
 * any validation failure — deployment error,
 * not user-recoverable.
 */
public class GameDataValidator {

    private GameDataValidator() {}

    public static void validate(
            GameDataProvider data) {
        validateOrigins(data);
        validateD66Table(data.getQuirks(),
                "quirks.json");
        validateD66Table(data.getKeepsakes(),
                "keepsakes.json");
        validateD66Table(
                data.getAppearances(),
                "appearances.json");
        validateD66Table(
                data.getExplorerReasons(),
                "explorer-reasons.json");
        validateProfessions(data);
        validateNameTables(data);
        validateGarudaPowers(data);
    }

    private static void validateOrigins(
            GameDataProvider data) {
        if (data.getOrigins() == null
                || data.getOrigins().isEmpty()) {
            throw new GameDataLoadException(
                    "origins.json",
                    "No origins loaded");
        }
    }

    /**
     * Validate a D66 table has all 36 entries
     * (11–16, 21–26, ... 61–66).
     */
    private static void validateD66Table(
            Map<Integer, String> table,
            String resourceName) {
        if (table == null) {
            throw new GameDataLoadException(
                    resourceName,
                    "Table is null");
        }
        List<Integer> missing =
                new ArrayList<>();
        for (int tens = 1; tens <= 6;
             tens++) {
            for (int ones = 1; ones <= 6;
                 ones++) {
                int key = tens * 10 + ones;
                if (!table.containsKey(key)) {
                    missing.add(key);
                }
            }
        }
        if (!missing.isEmpty()) {
            throw new GameDataLoadException(
                    resourceName,
                    "Missing D66 entries: "
                            + missing);
        }
    }

    private static void validateProfessions(
            GameDataProvider data) {
        List<String> profs =
                data.getValidProfessionNames();
        if (profs == null
                || profs.isEmpty()) {
            throw new GameDataLoadException(
                    "professions.json",
                    "No professions loaded");
        }
        for (String prof : profs) {
            ProfessionData pd =
                    data.getProfession(prof);
            if (pd == null) {
                throw new GameDataLoadException(
                        "professions.json",
                        "Missing data: " + prof);
            }
            if (pd.getKeyAttribute() == null) {
                throw new GameDataLoadException(
                        "professions.json",
                        prof
                                + " has null keyAttribute");
            }
            if (pd.getSpecialties() == null
                    || pd.getSpecialties()
                    .isEmpty()) {
                throw new GameDataLoadException(
                        "professions.json",
                        prof
                                + " has no specialties");
            }
            if (pd.getTalents() == null
                    || pd.getTalents().isEmpty()) {
                throw new GameDataLoadException(
                        "professions.json",
                        prof
                                + " has no talents");
            }
            for (ProfessionData.TalentData t :
                    pd.getTalents()) {
                try {
                    TalentCategory.valueOf(
                            t.category());
                } catch (
                        IllegalArgumentException e) {
                    throw new
                            GameDataLoadException(
                            "professions.json",
                            String.format(
                                    "%s talent '%s' has"
                                            + " invalid category:"
                                            + " %s",
                                    prof, t.name(),
                                    t.category()));
                }
            }
        }
    }

    private static void validateNameTables(
            GameDataProvider data) {
        for (String prof :
                data.getValidProfessionNames()) {
            String key =
                    prof.toLowerCase();
            Map<Integer, String> first =
                    data.getFirstNames(prof);
            Map<Integer, String> last =
                    data.getLastNames(prof);
            if (first == null
                    || first.isEmpty()) {
                throw new GameDataLoadException(
                        "names/" + key
                                + "-first.json",
                        "No first names for "
                                + prof);
            }
            if (last == null
                    || last.isEmpty()) {
                throw new GameDataLoadException(
                        "names/" + key
                                + "-last.json",
                        "No last names for "
                                + prof);
            }
        }
    }

    // =========================================
    // Garuda powers validation
    // =========================================

    private static void validateGarudaPowers(
            GameDataProvider data) {
        GarudaPowerRegistry registry =
                data.getGarudaPowerRegistry();
        if (registry == null) {
            throw new GameDataLoadException(
                    "garuda-powers.json",
                    "GarudaPowerRegistry is null");
        }

        // Must have exactly 18 powers
        if (registry.size() != 18) {
            throw new GameDataLoadException(
                    "garuda-powers.json",
                    "Expected 18 powers, found "
                            + registry.size());
        }

        // Must have exactly 6 basic
        if (registry.getBasicPowers().size()
                != 6) {
            throw new GameDataLoadException(
                    "garuda-powers.json",
                    "Expected 6 basic powers, found "
                            + registry.getBasicPowers()
                            .size());
        }

        // Must have exactly 12 advanced
        if (registry.getAllAdvancedPowers().size()
                != 12) {
            throw new GameDataLoadException(
                    "garuda-powers.json",
                    "Expected 12 advanced powers,"
                            + " found "
                            + registry
                            .getAllAdvancedPowers()
                            .size());
        }

        // Validate each power
        for (GarudaPower power
                : registry.getAllPowers()) {
            if (power.getEnergyCost() < 0) {
                throw new GameDataLoadException(
                        "garuda-powers.json",
                        power.getName()
                                + " has negative energy"
                                + " cost: "
                                + power.getEnergyCost());
            }
            if (power.getNativeTypes() == null) {
                throw new GameDataLoadException(
                        "garuda-powers.json",
                        power.getName()
                                + " has null nativeTypes");
            }
            for (BirdType type
                    : power.getNativeTypes()) {
                if (type == null) {
                    throw new
                            GameDataLoadException(
                            "garuda-powers.json",
                            power.getName()
                                    + " has null"
                                    + " BirdType");
                }
            }
        }

        // Each type gets 8 advanced powers
        for (BirdType type
                : BirdType.values()) {
            int count = registry
                    .getAdvancedPowersFor(type)
                    .size();
            if (count != 8) {
                throw new GameDataLoadException(
                        "garuda-powers.json",
                        type + " should have 8"
                                + " advanced powers,"
                                + " found " + count);
            }
        }
    }
}