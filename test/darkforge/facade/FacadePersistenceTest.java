package darkforge.facade;

import darkforge.model.Explorer;
import darkforge.persistence.ExplorerFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadePersistence sub-facade.
 * Verifies save/load/list/delete pass-through
 * to the persistence layer via @TempDir.
 */
class FacadePersistenceTest {

    @TempDir
    Path tempDir;

    private ExplorerFileManager fileManager;
    private Explorer testExplorer;

    @BeforeEach
    void setUp() throws Exception {
        fileManager = new ExplorerFileManager(tempDir);
        testExplorer = FacadeDarkforge.getTheInstance()
                .creationAccess().createExplorer(
                        "Enforcer", "Kaan Verros", 1, 1,
                        new int[]{4, 4, 4, 4, 4, 4},
                        new int[]{1, 1, 1, 0},
                        "quirk", "keepsake", "appearance"
                );
    }

    @Test
    void shouldSaveAndLoadExplorer() throws Exception {
        Path saved = fileManager.saveExplorer(testExplorer);
        assertNotNull(saved);
        Explorer loaded = fileManager.loadExplorer(saved);
        assertEquals(
                testExplorer.getName(),
                loaded.getName());
        assertEquals(
                testExplorer.getProfessionName(),
                loaded.getProfessionName());
    }

    @Test
    void shouldListSavedExplorers() throws Exception {
        fileManager.saveExplorer(testExplorer);
        List<Path> saves = fileManager.listSavedExplorers();
        assertEquals(1, saves.size());
    }

    @Test
    void shouldDeleteExplorer() throws Exception {
        Path saved = fileManager.saveExplorer(testExplorer);
        assertTrue(fileManager.deleteExplorer(saved));
        assertTrue(fileManager.listSavedExplorers().isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoSaves()
            throws IOException {
        assertTrue(
                fileManager.listSavedExplorers().isEmpty());
    }

    @Test
    void shouldThrowOnLoadNonexistentFile() {
        assertThrows(Exception.class, () ->
                fileManager.loadExplorer(
                        tempDir.resolve("nope.darkforge.json")));
    }

    @Test
    void shouldReturnFalseForNonexistentDelete()
            throws IOException {
        assertFalse(fileManager.deleteExplorer(
                tempDir.resolve("nope.darkforge.json")));
    }

    @Test
    void shouldUseSingletonPattern() {
        FacadePersistence p1 =
                FacadeDarkforge.getTheInstance()
                        .persistenceAccess();
        FacadePersistence p2 =
                FacadeDarkforge.getTheInstance()
                        .persistenceAccess();
        assertSame(p1, p2,
                "persistenceAccess() should return "
                        + "same Singleton instance");
    }
}