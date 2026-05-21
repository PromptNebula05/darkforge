package darkforge.persistence;

import darkforge.exception
        .CharacterCorruptionException;
import darkforge.exception
        .CharacterCorruptionException
        .CorruptionType;
import org.json.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CrewDeserializer v3.0 validation.
 * Builds JSON programmatically to verify each
 * corruption path without requiring
 * GameDataProvider / ExplorerFactory.
 */
class CrewDeserializerTest {

    private CrewDeserializer deserializer;
    private static final String FILE =
            "test_crew.darkforge.json";

    @BeforeEach
    void setUp() {
        deserializer = new CrewDeserializer();
    }

    // =========================================
    // Helpers
    // =========================================

    /**
     * Minimal valid v3.0 skeleton: no members,
     * null bird/vehicles, empty roles. Will
     * fail at mandatory-role validation.
     */
    private JSONObject buildBaseJson() {
        JSONObject root = new JSONObject();
        root.put("version", "3.0");
        root.put("crewName", "Test Crew");
        root.put("totalSupply", 10);
        root.put("crewPoints", 5);
        root.put("learnedManeuvers",
                new JSONArray());
        root.put("members", new JSONArray());
        root.put("bird", JSONObject.NULL);
        root.put("shuttle", JSONObject.NULL);
        root.put("rover", JSONObject.NULL);
        root.put("roleAssignments",
                new JSONObject());
        return root;
    }

    private JSONObject buildBirdJson() {
        JSONObject bird = new JSONObject();
        bird.put("name", "Jade");
        bird.put("type", "GUIDE");
        bird.put("maxHealth", 4);
        bird.put("currentHealth", 4);
        bird.put("maxEnergy", 3);
        bird.put("currentEnergy", 3);
        bird.put("color", "Emerald");
        bird.put("bodyFeature",
                "Long tail feathers");
        bird.put("personality", "Curious");
        bird.put("powers", new JSONArray());
        return bird;
    }

    private JSONObject buildVehicleJson(
            String name, String type,
            String paint, int hull) {
        JSONObject v = new JSONObject();
        v.put("name", name);
        v.put("type", type);
        v.put("paintColor", paint);
        v.put("currentHull", hull);
        v.put("upgrades", new JSONArray());
        return v;
    }

    // =========================================
    // Malformed JSON
    // =========================================

    @Test
    void malformedJsonThrows() {
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                "not json!", FILE));
        assertEquals(
                CorruptionType.MALFORMED_FORMAT,
                ex.getCorruptionType());
    }

    // =========================================
    // Version
    // =========================================

    @Test
    void versionMismatchThrows() {
        JSONObject root = buildBaseJson();
        root.put("version", "1.0");
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.VERSION_MISMATCH,
                ex.getCorruptionType());
    }

    @Test
    void missingVersionThrows() {
        JSONObject root = buildBaseJson();
        root.remove("version");
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.MISSING_FIELD,
                ex.getCorruptionType());
    }

    // =========================================
    // Crew name
    // =========================================

    @Test
    void missingCrewNameThrows() {
        JSONObject root = buildBaseJson();
        root.remove("crewName");
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.MISSING_FIELD,
                ex.getCorruptionType());
        assertEquals("crewName",
                ex.getFieldName());
    }

    // =========================================
    // Supply and crew points
    // =========================================

    @Test
    void negativeSupplyThrows() {
        JSONObject root = buildBaseJson();
        root.put("totalSupply", -5);
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.INVALID_VALUE,
                ex.getCorruptionType());
        assertEquals("totalSupply",
                ex.getFieldName());
    }

    @Test
    void negativeCrewPointsThrows() {
        JSONObject root = buildBaseJson();
        root.put("crewPoints", -3);
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.INVALID_VALUE,
                ex.getCorruptionType());
        assertEquals("crewPoints",
                ex.getFieldName());
    }

    // =========================================
    // Bird
    // =========================================

    @Test
    void unknownBirdTypeThrows() {
        JSONObject root = buildBaseJson();
        JSONObject bird = buildBirdJson();
        bird.put("type", "PHOENIX");
        root.put("bird", bird);
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.INVALID_VALUE,
                ex.getCorruptionType());
        assertEquals("bird.type",
                ex.getFieldName());
    }

    @Test
    void missingBirdNameThrows() {
        JSONObject root = buildBaseJson();
        JSONObject bird = buildBirdJson();
        bird.remove("name");
        root.put("bird", bird);
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.MISSING_FIELD,
                ex.getCorruptionType());
    }

    // =========================================
    // Vehicles
    // =========================================

    @Test
    void unknownVehicleTypeThrows() {
        JSONObject root = buildBaseJson();
        root.put("shuttle",
                buildVehicleJson("Ship",
                        "TANK", "Red", 10));
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.INVALID_VALUE,
                ex.getCorruptionType());
        assertEquals("shuttle.type",
                ex.getFieldName());
    }

    // =========================================
    // Role assignments
    // =========================================

    @Test
    void missingMandatoryRoleThrows() {
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                buildBaseJson().toString(),
                                FILE));
        assertEquals(
                CorruptionType.MISSING_FIELD,
                ex.getCorruptionType());
        assertEquals("roleAssignments",
                ex.getFieldName());
    }

    @Test
    void roleReferencesNonExistentMember() {
        JSONObject root = buildBaseJson();
        JSONObject roles = new JSONObject();
        roles.put("DELVER", "Ghost");
        root.put("roleAssignments", roles);
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.INVALID_VALUE,
                ex.getCorruptionType());
        assertTrue(
                ex.getFieldName().contains(
                        "roleAssignments"));
    }

    @Test
    void unknownRoleKeyThrows() {
        JSONObject root = buildBaseJson();
        JSONObject roles = new JSONObject();
        roles.put("WIZARD", "Test");
        root.put("roleAssignments", roles);
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.INVALID_VALUE,
                ex.getCorruptionType());
    }

    // =========================================
    // Missing top-level fields
    // =========================================

    @Test
    void missingMembersArrayThrows() {
        JSONObject root = buildBaseJson();
        root.remove("members");
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.MISSING_FIELD,
                ex.getCorruptionType());
    }

    @Test
    void missingRoleAssignmentsThrows() {
        JSONObject root = buildBaseJson();
        root.remove("roleAssignments");
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                root.toString(), FILE));
        assertEquals(
                CorruptionType.MISSING_FIELD,
                ex.getCorruptionType());
    }

    // =========================================
    // Error context
    // =========================================

    @Test
    void filePathPreservedInException() {
        String path = "/saves/my_crew.json";
        CharacterCorruptionException ex =
                assertThrows(
                        CharacterCorruptionException
                                .class,
                        () -> deserializer.deserialize(
                                "bad!", path));
        assertEquals(path,
                ex.getFilePath());
    }
}