package darkforge.persistence;

import darkforge.creation.ExplorerFactory;
import darkforge.data.GameDataProvider;
import darkforge.exception.CharacterCorruptionException;
import darkforge.exception.CharacterCorruptionException.CorruptionType;
import darkforge.model.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExplorerDeserializer JSON parsing.
 * Verifies round-trip fidelity and comprehensive corruption detection.
 */
class ExplorerDeserializerTest {

    private ExplorerSerializer serializer;
    private ExplorerDeserializer deserializer;
    private Explorer originalExplorer;
    private String validJson;

    @BeforeEach
    void setUp() throws Exception {
        serializer = new ExplorerSerializer();
        deserializer = new ExplorerDeserializer();
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
        originalExplorer = factory.createExplorer(
                "Scholar", origin, 0,
                attrs, new int[]{1, 1, 1, 0},
                "Constantly reading", "Silver coin",
                "Sharp eyes", "Cantara Loutreides"
        );
        validJson = serializer.serialize(originalExplorer);
    }

    @Test
    void shouldRoundTripExplorerName() throws CharacterCorruptionException {
        Explorer loaded = deserializer.deserialize(validJson, "test.json");
        assertEquals(originalExplorer.getName(), loaded.getName());
    }

    @Test
    void shouldRoundTripProfession() throws CharacterCorruptionException {
        Explorer loaded = deserializer.deserialize(validJson, "test.json");
        assertEquals(originalExplorer.getProfessionName(), loaded.getProfessionName());
    }

    @Test
    void shouldRoundTripAttributes() throws CharacterCorruptionException {
        Explorer loaded = deserializer.deserialize(validJson, "test.json");
        for (Attribute attr : Attribute.values()) {
            assertEquals(originalExplorer.getAttribute(attr), loaded.getAttribute(attr),
                    "Attribute mismatch for " + attr);
        }
    }

    @Test
    void shouldRoundTripTalentCount() throws CharacterCorruptionException {
        Explorer loaded = deserializer.deserialize(validJson, "test.json");
        assertEquals(originalExplorer.getTalents().size(), loaded.getTalents().size());
    }

    @Test
    void shouldRoundTripKeyAttribute() throws CharacterCorruptionException {
        Explorer loaded = deserializer.deserialize(validJson, "test.json");
        assertEquals(originalExplorer.getKeyAttribute(), loaded.getKeyAttribute());
    }

    @Test
    void shouldThrowOnMalformedJson() {
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize("not json at all", "bad.json"));
        assertEquals(CorruptionType.MALFORMED_FORMAT, ex.getCorruptionType());
    }

    @Test
    void shouldThrowOnMissingProfession() {
        JSONObject root = new JSONObject(validJson);
        root.remove("profession");
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize(root.toString(), "test.json"));
        assertEquals(CorruptionType.MISSING_FIELD, ex.getCorruptionType());
        assertEquals("profession", ex.getFieldName());
    }

    @Test
    void shouldThrowOnMissingName() {
        JSONObject root = new JSONObject(validJson);
        root.remove("name");
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize(root.toString(), "test.json"));
        assertEquals(CorruptionType.MISSING_FIELD, ex.getCorruptionType());
    }

    @Test
    void shouldThrowOnMissingAttributes() {
        JSONObject root = new JSONObject(validJson);
        root.remove("attributes");
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize(root.toString(), "test.json"));
        assertEquals(CorruptionType.MISSING_FIELD, ex.getCorruptionType());
    }

    @Test
    void shouldThrowOnInvalidAttributeValue() {
        JSONObject root = new JSONObject(validJson);
        root.getJSONObject("attributes").put("STRENGTH", -1);
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize(root.toString(), "test.json"));
        assertEquals(CorruptionType.INVALID_VALUE, ex.getCorruptionType());
        assertTrue(ex.getFieldName().contains("STRENGTH"));
    }

    @Test
    void shouldThrowOnVersionMismatch() {
        JSONObject root = new JSONObject(validJson);
        root.put("version", "99.0");
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize(root.toString(), "test.json"));
        assertEquals(CorruptionType.VERSION_MISMATCH, ex.getCorruptionType());
    }

    @Test
    void shouldThrowOnUnknownProfession() {
        JSONObject root = new JSONObject(validJson);
        root.put("profession", "Wizard");
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize(root.toString(), "test.json"));
        assertEquals(CorruptionType.INVALID_VALUE, ex.getCorruptionType());
    }

    @Test
    void shouldThrowOnEmptyJson() {
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize("{}", "empty.json"));
        assertEquals(CorruptionType.MISSING_FIELD, ex.getCorruptionType());
    }

    @Test
    void wrappedJsonExceptionShouldPreserveCause() {
        var ex = assertThrows(CharacterCorruptionException.class,
                () -> deserializer.deserialize("not json", "bad.json"));
        assertNotNull(ex.getCause(),
                "Cause should be preserved for malformed JSON");
    }
}