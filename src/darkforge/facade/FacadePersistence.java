package darkforge.facade;

import darkforge.exception
        .CharacterCorruptionException;
import darkforge.model.Explorer;
import darkforge.persistence
        .ExplorerFileManager;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Façade for the darkforge.persistence package.
 * Wraps file save/load/list/delete operations
 * behind simple methods. Uses a default "saves"
 * directory.
 */
public class FacadePersistence {
    private static final FacadePersistence
            INSTANCE = new FacadePersistence();
    private final ExplorerFileManager fileManager;

    private FacadePersistence() {
        this.fileManager =
                new ExplorerFileManager(
                        Path.of("saves"));
    }

    public static FacadePersistence
    getTheInstance() {
        return INSTANCE;
    }

    /**
     * Save an Explorer to a JSON file.
     * @return the Path of the created file
     * @throws IOException if the file cannot
     *         be written
     */
    public Path saveExplorer(Explorer explorer)
            throws IOException {
        return fileManager.saveExplorer(
                explorer);
    }

    /**
     * Load an Explorer from a save file.
     * @throws IOException if the file cannot
     *         be read
     * @throws CharacterCorruptionException if
     *         the file is malformed or invalid
     */
    public Explorer loadExplorer(Path filePath)
            throws IOException,
            CharacterCorruptionException {
        return fileManager.loadExplorer(
                filePath);
    }

    /**
     * List all saved Explorer files.
     * @return sorted list of .darkforge.json paths
     * @throws IOException if the directory cannot
     *         be listed
     */
    public List<Path> listSaves()
            throws IOException {
        return fileManager
                .listSavedExplorers();
    }

    /**
     * Delete a saved Explorer file.
     * @return true if deleted, false if not found
     * @throws IOException if the file cannot be
     *         deleted
     */
    public boolean deleteSave(Path filePath)
            throws IOException {
        return fileManager.deleteExplorer(
                filePath);
    }
}