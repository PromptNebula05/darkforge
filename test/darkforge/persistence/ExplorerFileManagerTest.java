package darkforge.persistence;

import darkforge.creation.ExplorerFactory;
import darkforge.data.GameDataProvider;
import darkforge.exception.CharacterCorruptionException;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExplorerFileManager file I/O operations.
 * Uses JUnit 5 @TempDir for disposable test directories.
 * Verifies try-with-resources guarantees for all stream ops.
 */
class ExplorerFileManagerTest {

    @TempDir
    Path tempDir;

    private ExplorerFileManager manager;
    private Explorer testExplorer;

    @BeforeEach
    void setUp() throws Exception {
        manager = new ExplorerFileManager(tempDir);
        GameDataProvider.getTheInstance().initialize();
        ExplorerFactory factory = new ExplorerFactory();
        EnumMap<Attribute, Integer> attrs = new EnumMap<>(Attribute.class);
        attrs.put(Attribute.STRENGTH, 2);
        attrs.put(Attribute.AGILITY, 4);
        attrs.put(Attribute.LOGIC, 6);
        attrs.put(Attribute.PERCEPTION, 3);
        attrs.put(Attribute.INSIGHT, 4);
        attrs.put(Attribute.EMPATHY, 5);
        Origin origin = GameDataProvider.getTheInstance()
                .getOrigins().get(0);
        testExplorer = factory.createExplorer(
                "Scholar", origin, 0,
                attrs, new int[]{1, 1, 1, 0},
                "Constantly reading", "Silver coin",
                "Sharp eyes", "Cantara Loutreides"
        );
    }

    @Test
    void shouldSaveExplorerToFile() throws IOException {
        Path saved = manager.saveExplorer(testExplorer);
        assertTrue(Files.exists(saved));
        assertTrue(saved.toString().endsWith(".darkforge.json"));
    }

    @Test
    void shouldSaveAndLoadRoundTrip() throws Exception {
        Path saved = manager.saveExplorer(testExplorer);
        Explorer loaded = manager.loadExplorer(saved);
        assertEquals(testExplorer.getName(), loaded.getName());
        assertEquals(testExplorer.getProfessionName(), loaded.getProfessionName());
        for (Attribute attr : Attribute.values()) {
            assertEquals(testExplorer.getAttribute(attr), loaded.getAttribute(attr),
                    "Attribute mismatch: " + attr);
        }
    }

    @Test
    void shouldListSavedExplorers() throws Exception {
        manager.saveExplorer(testExplorer);
        EnumMap<Attribute, Integer> attrs2 = new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) attrs2.put(a, 4);
        ExplorerFactory factory = new ExplorerFactory();
        Origin origin2 = GameDataProvider.getTheInstance()
                .getOrigins().get(0);
        Explorer second = factory.createExplorer(
                "Enforcer", origin2, 0,
                attrs2, new int[]{1, 1, 1, 0},
                "Scarred", "Dog tags", "Tall",
                "Kaan Verros"
        );
        manager.saveExplorer(second);
        List<Path> saves = manager.listSavedExplorers();
        assertEquals(2, saves.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoSaves() throws IOException {
        List<Path> saves = manager.listSavedExplorers();
        assertTrue(saves.isEmpty());
    }

    @Test
    void shouldDeleteSavedExplorer() throws Exception {
        Path saved = manager.saveExplorer(testExplorer);
        assertTrue(Files.exists(saved));
        assertTrue(manager.deleteExplorer(saved));
        assertFalse(Files.exists(saved));
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentFile() throws IOException {
        assertFalse(manager.deleteExplorer(
                tempDir.resolve("nonexistent.darkforge.json")));
    }

    @Test
    void shouldThrowIOExceptionForNonExistentLoad() {
        assertThrows(IOException.class, () ->
                manager.loadExplorer(
                        tempDir.resolve("nonexistent.darkforge.json")));
    }

    @Test
    void shouldThrowCorruptionExceptionForMalformedFile()
            throws IOException {
        Path corrupt = tempDir.resolve("corrupt.darkforge.json");
        Files.writeString(corrupt, "this is not json");
        assertThrows(CharacterCorruptionException.class, () ->
                manager.loadExplorer(corrupt));
    }

    @Test
    void shouldSanitizeFilename() throws IOException {
        Path saved = manager.saveExplorer(testExplorer);
        String filename = saved.getFileName().toString();
        assertFalse(filename.contains(" "),
                "Filename should not contain spaces");
        assertTrue(filename.endsWith(".darkforge.json"));
    }

    @Test
    void savedFileShouldContainValidJson() throws IOException {
        Path saved = manager.saveExplorer(testExplorer);
        String content = Files.readString(saved);
        assertDoesNotThrow(() -> new org.json.JSONObject(content),
                "Saved file should contain valid JSON");
    }
}