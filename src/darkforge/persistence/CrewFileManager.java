package darkforge.persistence;

import darkforge.crew.Crew;
import darkforge.exception
        .CharacterCorruptionException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Bridges Crew serialization with the
 * filesystem. Mirrors ExplorerFileManager
 * but uses the .darkforge-crew.json extension
 * and creates .bak backups on overwrite.
 *
 * Try-with-resources demonstrations:
 * 1. BufferedWriter in saveCrew()
 * 2. BufferedReader in loadCrew()
 * 3. Stream<Path> in listCrewFiles()
 */
public class CrewFileManager {

    private static final String
            FILE_EXTENSION =
            ".darkforge-crew.json";

    private final Path saveDirectory;
    private final CrewSerializer serializer;
    private final CrewDeserializer
            deserializer;

    public CrewFileManager(
            Path saveDirectory) {
        this.saveDirectory = saveDirectory;
        this.serializer =
                new CrewSerializer();
        this.deserializer =
                new CrewDeserializer();
    }

    // =========================================
    // Save
    // =========================================

    /**
     * Save a Crew to a JSON file. Creates a
     * .bak backup if the file already exists.
     *
     * @param crew the Crew to save
     * @return the Path of the created file
     * @throws IOException if the file cannot
     *     be written
     */
    public Path saveCrew(Crew crew)
            throws IOException {
        String json =
                serializer.serialize(crew);
        String filename =
                sanitizeFilename(crew.getName())
                        + FILE_EXTENSION;
        Path filePath =
                saveDirectory.resolve(filename);

        Files.createDirectories(
                saveDirectory);

        // Backup existing file before overwrite
        if (Files.exists(filePath)) {
            Path backupPath =
                    saveDirectory.resolve(
                            filename + ".bak");
            Files.copy(filePath, backupPath,
                    StandardCopyOption
                            .REPLACE_EXISTING);
        }

        try (BufferedWriter writer =
                     Files.newBufferedWriter(
                             filePath,
                             StandardCharsets.UTF_8)) {
            writer.write(json);
        }

        return filePath;
    }

    // =========================================
    // Load
    // =========================================

    /**
     * Load a Crew from a JSON file.
     *
     * @param filePath path to the save file
     * @return the reconstructed Crew
     * @throws IOException if the file cannot
     *     be read
     * @throws CharacterCorruptionException if
     *     the file is malformed or invalid
     */
    public Crew loadCrew(Path filePath)
            throws IOException,
            CharacterCorruptionException {
        String json;

        try (BufferedReader reader =
                     Files.newBufferedReader(
                             filePath,
                             StandardCharsets.UTF_8)) {
            StringBuilder sb =
                    new StringBuilder();
            String line;
            while ((line = reader.readLine())
                    != null) {
                sb.append(line)
                        .append('\n');
            }
            json = sb.toString();
        }

        return deserializer.deserialize(
                json, filePath.toString());
    }

    // =========================================
    // List
    // =========================================

    /**
     * List all saved Crew files in the save
     * directory. Returns only files with the
     * .darkforge-crew.json extension.
     *
     * @return sorted list of crew file paths
     * @throws IOException if the directory
     *     cannot be listed
     */
    public List<Path> listCrewFiles()
            throws IOException {
        if (!Files.exists(saveDirectory)) {
            return List.of();
        }

        try (Stream<Path> stream =
                     Files.list(saveDirectory)) {
            return stream
                    .filter(p -> p.toString()
                            .endsWith(FILE_EXTENSION))
                    .sorted()
                    .collect(
                            Collectors.toList());
        }
    }

    // =========================================
    // Delete
    // =========================================

    /**
     * Delete a saved Crew file.
     *
     * @param filePath path to the save file
     * @return true if deleted, false if
     *     it did not exist
     * @throws IOException if the file cannot
     *     be deleted
     */
    public boolean deleteCrew(Path filePath)
            throws IOException {
        return Files.deleteIfExists(filePath);
    }

    // =========================================
    // Getters
    // =========================================

    public Path getSaveDirectory() {
        return saveDirectory;
    }

    // =========================================
    // Utility
    // =========================================

    private String sanitizeFilename(
            String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9_-]", "_")
                .replaceAll("_+", "_");
    }
}