package darkforge.facade;

import darkforge.crew.*;
import darkforge.data.GameDataProvider;
import darkforge.data.GarudaPowerRegistry;
import darkforge.data.TalentRegistry;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.model.Explorer;
import darkforge.model.Talent;
import darkforge.model.TalentCategory;
import darkforge.persistence.CrewFileManager;

import darkforge.mechanics.D6Table;
import darkforge.mechanics.D66Table;

import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Façade singleton for crew assembly, talent
 * browsing, and crew persistence operations.
 */
public class FacadeCrew {
    private static final FacadeCrew INSTANCE =
            new FacadeCrew();

    private final CrewFileManager fileManager;

    private FacadeCrew() {
        this.fileManager = new CrewFileManager(
                Path.of(System.getProperty("user.home"),
                        ".darkforge", "saves"));
    }

    public static FacadeCrew getTheInstance() {
        return INSTANCE;
    }

    // =========================================
    // Builder
    // =========================================

    public CrewBuilder newCrewBuilder() {
        return new CrewBuilder();
    }

    // =========================================
    // Role suggestions & analytics
    // =========================================

    public Map<CrewRole, List<Explorer>>
    suggestRoles(Crew crew) {
        return crew.suggestRoleAssignments();
    }

    public Map<CrewRole, Explorer>
    getOptimalAssignment(Crew crew) {
        return new CrewAnalytics(crew)
                .getOptimalRoleAssignment();
    }

    // =========================================
    // Talent browsing
    // =========================================

    public List<Talent> browseTalents(
            TalentCategory category) {
        return getTalentRegistry()
                .getByCategory(category);
    }

    public List<Talent> searchTalents(
            String query) {
        return getTalentRegistry().search(query);
    }

    // =========================================
    // Garuda powers
    // =========================================

    public List<GarudaPower> getAvailablePowers(
            BirdType type) {
        return getGarudaPowerRegistry()
                .getAdvancedPowersFor(type);
    }

    // =========================================
    // Random name generation (crew-names.json)
    // =========================================

    /**
     * Generate a random crew name by rolling
     * two D6 tables (prefix + suffix) from
     * crew-names.json. If the prefix roll
     * yields "EXPLORER_NAME", substitutes
     * the given explorer name.
     */
    public String generateRandomCrewName(
            String explorerName) {
        GameDataProvider gdp =
                GameDataProvider.getTheInstance();
        D6Table<String> prefixTable =
                new D6Table<>(
                        gdp.getCrewNamePrefixes(),
                        new Random());
        D6Table<String> suffixTable =
                new D6Table<>(
                        gdp.getCrewNameSuffixes(),
                        new Random());

        String prefix =
                prefixTable.selectRandom();
        if ("EXPLORER_NAME"
                .equals(prefix)) {
            prefix =
                    (explorerName != null
                            && !explorerName
                            .isEmpty())
                            ? explorerName + "'s"
                            : "The Unknown's";
        }
        String suffix =
                suffixTable.selectRandom();
        return prefix + " " + suffix;
    }

    // =========================================
    // Random Bird appearance (D66 tables)
    // =========================================

    /**
     * Generate random Bird appearance traits
     * by rolling four D66 tables from
     * bird-appearances.json.
     * @return map with keys: name, color,
     *     bodyFeature, personality
     */
    public Map<String, String>
    generateRandomBirdAppearance() {
        GameDataProvider gdp =
                GameDataProvider.getTheInstance();
        D66Table<String> colorTable =
                new D66Table<>(
                        gdp.getBirdColors(),
                        new Random());
        D66Table<String> featureTable =
                new D66Table<>(
                        gdp.getBirdBodyFeatures(),
                        new Random());
        D66Table<String> personalityTable =
                new D66Table<>(
                        gdp.getBirdPersonalities(),
                        new Random());
        D66Table<String> nameTable =
                new D66Table<>(
                        gdp.getBirdNames(),
                        new Random());

        Map<String, String> result =
                new LinkedHashMap<>();
        result.put("name",
                nameTable.selectRandom());
        result.put("color",
                colorTable.selectRandom());
        result.put("bodyFeature",
                featureTable.selectRandom());
        result.put("personality",
                personalityTable
                        .selectRandom());
        return result;
    }

    // =========================================
    // Persistence
    // =========================================

    public Path saveCrew(Crew crew)
            throws IOException {
        return fileManager.saveCrew(crew);
    }

    public Crew loadCrew(Path filePath)
            throws IOException,
            CharacterCorruptionException {
        return fileManager.loadCrew(filePath);
    }

    public List<Path> listCrewSaves()
            throws IOException {
        return fileManager.listCrewFiles();
    }

    public boolean deleteCrewSave(Path filePath)
            throws IOException {
        return fileManager.deleteCrew(filePath);
    }

    // =========================================
    // Registry helpers
    // =========================================

    private TalentRegistry getTalentRegistry() {
        return GameDataProvider.getTheInstance()
                .getTalentRegistry();
    }

    private GarudaPowerRegistry
    getGarudaPowerRegistry() {
        return GameDataProvider.getTheInstance()
                .getGarudaPowerRegistry();
    }
}