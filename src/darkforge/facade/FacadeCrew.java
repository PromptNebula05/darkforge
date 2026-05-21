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

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

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