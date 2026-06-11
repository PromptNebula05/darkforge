package darkforge.facade;

import darkforge.exception
        .CharacterCorruptionException;
import darkforge.model.Explorer;
import darkforge.persistence.ExplorerFileManager;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Façade singleton for the darkforge.persistence
 * package. Provides save/load/list/delete
 * operations for Explorer JSON files.
 */
public class FacadePersistence {
    private static final FacadePersistence INSTANCE =
            new FacadePersistence();

    private final ExplorerFileManager fileManager;

    private FacadePersistence() {
        this.fileManager = new ExplorerFileManager(
                Path.of(System.getProperty("user.home"),
                        ".darkforge", "saves"));
    }

    public static FacadePersistence getTheInstance() {
        return INSTANCE;
    }

    /**
     * Save an Explorer to a JSON file.
     * @return the Path of the saved file
     */
    public Path saveExplorer(Explorer explorer)
            throws IOException {
        return fileManager.saveExplorer(explorer);
    }

    /**
     * Load an Explorer from a JSON file.
     */
    public Explorer loadExplorer(Path filePath)
            throws IOException,
            CharacterCorruptionException {
        return fileManager.loadExplorer(filePath);
    }

    /**
     * List all saved Explorer files.
     */
    public List<Path> listSaves()
            throws IOException {
        return fileManager.listSavedExplorers();
    }

    /**
     * Delete a saved Explorer file.
     */
    public boolean deleteExplorer(Path filePath)
            throws IOException {
        return fileManager.deleteExplorer(filePath);
    }
}