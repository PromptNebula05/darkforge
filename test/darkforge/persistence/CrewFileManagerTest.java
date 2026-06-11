package darkforge.persistence;

import darkforge.creation.ExplorerFactory;
import darkforge.crew.*;
import darkforge.data.GameDataProvider;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CrewFileManager file I/O.
 * Uses @TempDir for disposable test directories.
 * Requires GameDataProvider for Explorer
 * round-trip through ExplorerDeserializer.
 */
class CrewFileManagerTest {

    @TempDir
    Path tempDir;

    private CrewFileManager manager;
    private Crew testCrew;

    @BeforeEach
    void setUp() throws Exception {
        manager = new CrewFileManager(tempDir);
        GameDataProvider.getTheInstance()
                .initialize();
        testCrew = buildTestCrew();
    }

    // =========================================
    // Helpers
    // =========================================

    private Crew buildTestCrew()
            throws Exception {
        ExplorerFactory factory =
                new ExplorerFactory();
        Origin origin = GameDataProvider
                .getTheInstance()
                .getOrigins().get(0);

        Explorer[] members = new Explorer[4];
        String[] names = {
                "Kiran Voss", "Nala Theron",
                "Tarek Orin", "Sera Kai"
        };
        for (int i = 0; i < 4; i++) {
            EnumMap<Attribute, Integer> attrs =
                    new EnumMap<>(
                            Attribute.class);
            attrs.put(
                    Attribute.STRENGTH,4);
            attrs.put(
                    Attribute.AGILITY, 4);
            attrs.put(
                    Attribute.LOGIC, 4);
            attrs.put(
                    Attribute.PERCEPTION, 4);
            attrs.put(
                    Attribute.INSIGHT, 4);
            attrs.put(
                    Attribute.EMPATHY, 4);
            members[i] =
                    factory.createExplorer(
                            "Scholar", origin, 0,
                            attrs,
                            new int[]{1, 1, 1, 0},
                            "Quirk " + i,
                            "Keepsake " + i,
                            "Look " + i,
                            names[i]);
        }

        Crew crew =
                new Crew("The Scarred Seekers");
        for (Explorer m : members) {
            crew.addMember(m);
        }
        crew.assignRole(
                CrewRole.DELVER, members[0]);
        crew.assignRole(
                CrewRole.SCOUT, members[1]);
        crew.assignRole(
                CrewRole.BURROWER, members[2]);
        crew.assignRole(
                CrewRole.GUARD, members[3]);

        Bird bird = new Bird("Jade",
                BirdType.GUIDE, "Emerald",
                "Long tail feathers",
                "Curious");
        crew.setBird(bird);
        crew.setShuttle(Vehicle.createOwl(
                "Dustrunner", "Matte black"));
        crew.setRover(Vehicle.createRhino(
                "Sandcrawler", "Desert tan"));
        crew.addSupply(42);
        crew.addCrewPoints(15);
        return crew;
    }

    // =========================================
    // Save
    // =========================================

    @Test
    void shouldSaveCrewToFile()
            throws IOException {
        Path saved =
                manager.saveCrew(testCrew);
        assertTrue(Files.exists(saved));
        assertTrue(saved.toString()
                .endsWith(
                        ".darkforge-crew.json"));
    }

    @Test
    void savedFileShouldContainValidJson()
            throws IOException {
        Path saved =
                manager.saveCrew(testCrew);
        String content =
                Files.readString(saved);
        assertDoesNotThrow(
                () -> new org.json.JSONObject(
                        content));
    }

    @Test
    void shouldSanitizeFilename()
            throws IOException {
        Path saved =
                manager.saveCrew(testCrew);
        String filename =
                saved.getFileName().toString();
        assertFalse(
                filename.contains(" "));
        assertTrue(filename.endsWith(
                ".darkforge-crew.json"));
    }

    // =========================================
    // Round-trip
    // =========================================

    @Test
    void shouldSaveAndLoadRoundTrip()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        Crew loaded =
                manager.loadCrew(saved);

        assertEquals(
                testCrew.getName(),
                loaded.getName());
        assertEquals(
                testCrew.getCrewSize(),
                loaded.getCrewSize());
        assertEquals(
                testCrew.getTotalSupply(),
                loaded.getTotalSupply());
        assertEquals(
                testCrew.getCrewPoints(),
                loaded.getCrewPoints());
    }

    @Test
    void roundTripPreservesMemberNames()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        Crew loaded =
                manager.loadCrew(saved);

        Set<String> original = new HashSet<>();
        for (Explorer m :
                testCrew.getMembers()) {
            original.add(m.getName());
        }
        Set<String> restored = new HashSet<>();
        for (Explorer m :
                loaded.getMembers()) {
            restored.add(m.getName());
        }
        assertEquals(original, restored);
    }

    @Test
    void roundTripPreservesRoleAssignments()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        Crew loaded =
                manager.loadCrew(saved);

        for (CrewRole role :
                CrewRole.values()) {
            if (role.isOptional()) continue;
            Explorer orig =
                    testCrew.getAssignedExplorer(
                            role);
            Explorer rest =
                    loaded.getAssignedExplorer(
                            role);
            assertNotNull(rest,
                    "Role " + role
                            + " should be assigned");
            assertEquals(
                    orig.getName(),
                    rest.getName());
        }
    }

    @Test
    void roundTripPreservesBird()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        Crew loaded =
                manager.loadCrew(saved);

        assertNotNull(loaded.getBird());
        assertEquals(
                testCrew.getBird().getName(),
                loaded.getBird().getName());
        assertEquals(
                testCrew.getBird().getType(),
                loaded.getBird().getType());
    }

    @Test
    void roundTripPreservesVehicles()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        Crew loaded =
                manager.loadCrew(saved);

        assertNotNull(loaded.getShuttle());
        assertEquals("Dustrunner",
                loaded.getShuttle().getName());
        assertNotNull(loaded.getRover());
        assertEquals("Sandcrawler",
                loaded.getRover().getName());
    }

    // =========================================
    // Listing
    // =========================================

    @Test
    void listCrewFilesReturnsCrewFilesOnly()
            throws Exception {
        manager.saveCrew(testCrew);

        // Drop an explorer file in the same dir
        Files.writeString(
                tempDir.resolve(
                        "test.darkforge.json"),
                "{}");
        // Drop a random file
        Files.writeString(
                tempDir.resolve("notes.txt"),
                "hello");

        List<Path> crewFiles =
                manager.listCrewFiles();
        assertEquals(1, crewFiles.size());
        assertTrue(
                crewFiles.get(0).toString()
                        .endsWith(
                                ".darkforge-crew.json"));
    }

    @Test
    void shouldReturnEmptyListWhenNoSaves()
            throws IOException {
        List<Path> files =
                manager.listCrewFiles();
        assertTrue(files.isEmpty());
    }

    // =========================================
    // Delete
    // =========================================

    @Test
    void shouldDeleteCrewFile()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        assertTrue(Files.exists(saved));
        assertTrue(
                manager.deleteCrew(saved));
        assertFalse(Files.exists(saved));
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistent()
            throws IOException {
        assertFalse(manager.deleteCrew(
                tempDir.resolve(
                        "ghost.darkforge-crew.json"
                )));
    }

    // =========================================
    // Error handling
    // =========================================

    @Test
    void shouldThrowIOExceptionForNonExistentLoad() {
        assertThrows(IOException.class,
                () -> manager.loadCrew(
                        tempDir.resolve(
                                "nope.darkforge-crew"
                                        + ".json")));
    }

    @Test
    void shouldThrowCorruptionForMalformedFile()
            throws IOException {
        Path corrupt = tempDir.resolve(
                "bad.darkforge-crew.json");
        Files.writeString(
                corrupt, "not json");
        assertThrows(
                CharacterCorruptionException
                        .class,
                () -> manager.loadCrew(corrupt));
    }

    // =========================================
    // Backup on overwrite
    // =========================================

    @Test
    void shouldCreateBackupOnOverwrite()
            throws Exception {
        manager.saveCrew(testCrew);
        String filename =
                testCrew.getName().toLowerCase()
                        .replaceAll(
                                "[^a-z0-9_-]", "_")
                        .replaceAll("_+", "_")
                        + ".darkforge-crew.json";
        Path backupPath = tempDir.resolve(
                filename + ".bak");

        assertFalse(Files.exists(backupPath),
                "No backup before overwrite");

        // Save again to trigger backup
        manager.saveCrew(testCrew);
        assertTrue(Files.exists(backupPath),
                "Backup should exist after "
                        + "overwrite");
    }

    @Test
    void backupContainsPreviousContent()
            throws Exception {
        Path saved =
                manager.saveCrew(testCrew);
        String originalContent =
                Files.readString(saved);

        // Modify supply and save again
        testCrew.addSupply(100);
        manager.saveCrew(testCrew);

        String filename =
                saved.getFileName().toString();
        Path backupPath = tempDir.resolve(
                filename + ".bak");
        String backupContent =
                Files.readString(backupPath);

        assertEquals(originalContent,
                backupContent);
    }

    // =========================================
    // Backward compatibility
    // =========================================

    @Test
    void explorerFilesStillLoadableAlongside()
            throws Exception {
        ExplorerFileManager explorerMgr =
                new ExplorerFileManager(tempDir);

        // Save an explorer via old manager
        Explorer explorer =
                testCrew.getMembers().get(0);
        Path explorerPath =
                explorerMgr.saveExplorer(
                        explorer);

        // Save a crew via new manager
        manager.saveCrew(testCrew);

        // Explorer file still loadable
        Explorer loaded =
                explorerMgr.loadExplorer(
                        explorerPath);
        assertEquals(explorer.getName(),
                loaded.getName());

        // Each manager lists only its own type
        assertEquals(1,
                explorerMgr
                        .listSavedExplorers()
                        .size());
        assertEquals(1,
                manager.listCrewFiles().size());
    }
}