package darkforge.persistence;

import darkforge.exception
        .CharacterCorruptionException;
import darkforge.model.Explorer;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bridges serialization/deserialization (String)
 * with the filesystem (Path). All file operations
 * use try-with-resources to guarantee resource
 * cleanup.
 *
 * Three distinct try-with-resources demonstrations:
 * 1. BufferedWriter in saveExplorer()
 * 2. BufferedReader in loadExplorer()
 * 3. Stream<Path> in listSavedExplorers()
 *
 * Files use the .darkforge.json extension to
 * prevent collisions with generic JSON files.
 */
public class ExplorerFileManager {
    private static final String FILE_EXTENSION =
            ".darkforge.json";
    private final Path saveDirectory;
    private final ExplorerSerializer serializer;
    private final ExplorerDeserializer deserializer;

    public ExplorerFileManager(
            Path saveDirectory) {
        this.saveDirectory = saveDirectory;
        this.serializer =
                new ExplorerSerializer();
        this.deserializer =
                new ExplorerDeserializer();
    }

    /**
     * Save an Explorer to a JSON file.
     * Uses try-with-resources for BufferedWriter.
     *
     * @param explorer the Explorer to save
     * @return the Path of the created file
     * @throws IOException if the file cannot be
     *         written
     */
    public Path saveExplorer(Explorer explorer)
            throws IOException {
        String json =
                serializer.serialize(explorer);
        String filename = sanitizeFilename(
                explorer.getName()) + FILE_EXTENSION;
        Path filePath =
                saveDirectory.resolve(filename);

        // Ensure save directory exists
        Files.createDirectories(saveDirectory);

        // TRY-WITH-RESOURCES: BufferedWriter
        // Writer is GUARANTEED closed here,
        // even if write() throws mid-file
        // (prevents partial/corrupted saves)
        try (BufferedWriter writer =
                     Files.newBufferedWriter(filePath,
                             StandardCharsets.UTF_8)) {
            writer.write(json);
        }

        return filePath;
    }

    /**
     * Load an Explorer from a JSON file.
     * Uses try-with-resources for BufferedReader.
     *
     * @param filePath the path to the save file
     * @return the reconstructed Explorer
     * @throws IOException if the file cannot be
     *         read (e.g. file not found)
     * @throws CharacterCorruptionException if the
     *         file contains malformed or invalid
     *         data
     */
    public Explorer loadExplorer(Path filePath)
            throws IOException,
            CharacterCorruptionException {
        String json;

        // TRY-WITH-RESOURCES: BufferedReader
        // Reader is GUARANTEED closed here,
        // even if readLine() throws mid-read
        // (prevents file locks on Windows)
        try (BufferedReader reader =
                     Files.newBufferedReader(filePath,
                             StandardCharsets.UTF_8)) {
            StringBuilder sb =
                    new StringBuilder();
            String line;
            while ((line = reader.readLine())
                    != null) {
                sb.append(line).append('\n');
            }
            json = sb.toString();
        }

        return deserializer.deserialize(
                json, filePath.toString());
    }

    /**
     * List all saved Explorer files in the save
     * directory.
     * Uses try-with-resources for Stream\<Path\>.
     *
     * Files.list() returns a Stream that holds
     * an open directory handle.
     * Try-with-resources ensures the handle is
     * closed — a frequently-missed case since
     * most developers don't realize
     * Stream\<Path\> is AutoCloseable.
     *
     * @return sorted list of .darkforge.json paths
     * @throws IOException if the directory cannot
     *         be listed
     */
    public List<Path> listSaves()
            throws IOException {
        if (!Files.exists(saveDirectory)) {
            return List.of();
        }

        // TRY-WITH-RESOURCES: Stream<Path>
        // Directory handle is GUARANTEED closed
        try (Stream<Path> stream =
                     Files.list(saveDirectory)) {
            return stream
                    .filter(p -> p.toString()
                            .endsWith(FILE_EXTENSION))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Delete a saved Explorer file.
     *
     * @param filePath the path to the save file
     * @return true if the file was deleted, false
     *         if it did not exist
     * @throws IOException if the file cannot be
     *         deleted
     */
    public boolean deleteExplorer(Path filePath)
            throws IOException {
        return Files.deleteIfExists(filePath);
    }

    /**
     * Get the save directory path.
     *
     * @return the configured save directory
     */
    public Path getSaveDirectory() {
        return saveDirectory;
    }

    /**
     * Sanitize a name for use as a filename.
     * Lowercases, replaces non-alphanumeric chars
     * with underscores, collapses consecutive
     * underscores.
     * e.g. "Cantara Loutreides" ->
     *      "cantara_loutreides"
     */
    private String sanitizeFilename(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9_-]", "_")
                .replaceAll("_+", "_");
    }
}