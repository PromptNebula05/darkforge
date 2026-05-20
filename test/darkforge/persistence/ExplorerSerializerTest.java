package darkforge.persistence;

import darkforge.creation.ExplorerFactory;
import darkforge.data.GameDataProvider;
import darkforge.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExplorerSerializer JSON output.
 * Verifies that a complete Explorer serializes to well-formed JSON
 * with all required fields and correct values.
 */
class ExplorerSerializerTest {

    private ExplorerSerializer serializer;
    private Explorer testExplorer;

    @BeforeEach
    void setUp() throws Exception {
        serializer = new ExplorerSerializer();
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
    void shouldProduceValidJson() {
        String json = serializer.serialize(testExplorer);
        assertDoesNotThrow(() -> new JSONObject(json),
                "Serialized output should be valid JSON");
    }

    @Test
    void shouldIncludeVersionField() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        assertEquals("2.0", root.getString("version"));
    }

    @Test
    void shouldIncludeProfessionName() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        assertEquals("Scholar", root.getString("profession"));
    }

    @Test
    void shouldIncludeExplorerName() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        assertEquals("Cantara Loutreides", root.getString("name"));
    }

    @Test
    void shouldIncludeAllSixAttributes() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONObject attrs = root.getJSONObject("attributes");
        assertEquals(6, attrs.length(), "Should have all 6 attributes");
        for (Attribute attr : Attribute.values()) {
            assertTrue(attrs.has(attr.name()),
                    "Missing attribute: " + attr.name());
        }
    }

    @Test
    void shouldSerializeCorrectAttributeValues() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONObject attrs = root.getJSONObject("attributes");
        assertEquals(2, attrs.getInt("STRENGTH"));
        assertEquals(4, attrs.getInt("AGILITY"));
        assertEquals(6, attrs.getInt("LOGIC"));
        assertEquals(3, attrs.getInt("PERCEPTION"));
        assertEquals(4, attrs.getInt("INSIGHT"));
        assertEquals(5, attrs.getInt("EMPATHY"));
    }

    @Test
    void shouldIncludeTalentsArray() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONArray talents = root.getJSONArray("talents");
        assertFalse(talents.isEmpty(), "Should have at least one talent");
        JSONObject firstTalent = talents.getJSONObject(0);
        assertTrue(firstTalent.has("name"));
        assertTrue(firstTalent.has("category"));
        assertTrue(firstTalent.has("currentLevel"));
        assertTrue(firstTalent.has("maxLevel"));
        assertTrue(firstTalent.has("effect"));
    }

    @Test
    void shouldIncludeEquipmentArray() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONArray equipment = root.getJSONArray("equipment");
        assertFalse(equipment.isEmpty(), "Should have at least one equipment item");
        JSONObject firstItem = equipment.getJSONObject(0);
        assertTrue(firstItem.has("name"));
        assertTrue(firstItem.has("weight"));
        assertTrue(firstItem.has("gearBonus"));
    }

    @Test
    void shouldIncludePersonalDetails() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONObject details = root.getJSONObject("personalDetails");
        assertEquals("Constantly reading", details.getString("quirk"));
        assertEquals("Silver coin", details.getString("keepsake"));
        assertEquals("Sharp eyes", details.getString("appearance"));
    }

    @Test
    void shouldIncludeSpecialtySection() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONObject specialty = root.getJSONObject("specialty");
        assertTrue(specialty.has("name"));
        assertTrue(specialty.has("description"));
        assertTrue(specialty.has("freeTalent"));
    }

    @Test
    void shouldIncludeOriginSection() {
        JSONObject root = new JSONObject(serializer.serialize(testExplorer));
        JSONObject origin = root.getJSONObject("origin");
        assertTrue(origin.has("location"));
        assertTrue(origin.has("faction"));
        assertTrue(origin.has("contact"));
        assertTrue(origin.has("freeTalent"));
        assertTrue(origin.has("d66Range"));
    }

    @Test
    void shouldBeRoundTripParseable() {
        String json = serializer.serialize(testExplorer);
        JSONObject parsed = new JSONObject(json);
        String reSerialized = parsed.toString(4);
        JSONObject reParsed = new JSONObject(reSerialized);
        assertEquals(parsed.getString("name"), reParsed.getString("name"));
        assertEquals(parsed.getString("profession"), reParsed.getString("profession"));
    }
}