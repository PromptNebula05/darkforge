package darkforge.persistence;

import darkforge.crew.*;
import darkforge.model.*;
import org.json.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CrewSerializer JSON v3.0 output.
 * Verifies structure, field values, and edge
 * cases without requiring CrewDeserializer.
 */
class CrewSerializerTest {

    private CrewSerializer serializer;

    @BeforeEach
    void setUp() {
        serializer = new CrewSerializer();
    }

    // =========================================
    // Helpers
    // =========================================

    private Explorer makeExplorer(
            String name,
            int str, int agl, int log,
            int per, int ins, int emp) {
        Explorer explorer = new Explorer(
                name, "Test explorer") {
            public Attribute getKeyAttribute() {
                return Attribute.STRENGTH;
            }
            public List<Talent> getKeyTalents() {
                return List.of();
            }
            public List<List<Equipment>>
            getStartingEquipmentSets() {
                return List.of();
            }
            public String getProfessionName() {
                return "Roughneck";
            }
            public List<Specialty>
            getSpecialties() {
                return List.of();
            }
        };
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        attrs.put(Attribute.STRENGTH, str);
        attrs.put(Attribute.AGILITY, agl);
        attrs.put(Attribute.LOGIC, log);
        attrs.put(Attribute.PERCEPTION, per);
        attrs.put(Attribute.INSIGHT, ins);
        attrs.put(Attribute.EMPATHY, emp);
        explorer.setAttributes(attrs);
        return explorer;
    }

    private Crew buildTestCrew() {
        Explorer kiran = makeExplorer(
                "Kiran Voss", 3, 4, 3, 2, 5, 2);
        Explorer nala = makeExplorer(
                "Nala Theron", 2, 3, 4, 4, 2, 2);
        Explorer tarek = makeExplorer(
                "Tarek Orin", 5, 2, 3, 3, 2, 2);
        Explorer sera = makeExplorer(
                "Sera Kai", 4, 2, 3, 5, 1, 2);

        Bird bird = new Bird("Jade",
                BirdType.GUIDE, "Emerald",
                "Long tail feathers", "Curious");

        Crew crew = new Crew(
                "The Scarred Seekers");
        crew.addMember(kiran);
        crew.addMember(nala);
        crew.addMember(tarek);
        crew.addMember(sera);
        crew.assignRole(
                CrewRole.DELVER, kiran);
        crew.assignRole(
                CrewRole.SCOUT, nala);
        crew.assignRole(
                CrewRole.BURROWER, tarek);
        crew.assignRole(
                CrewRole.GUARD, sera);
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
    // Version and top-level structure
    // =========================================

    @Test
    void serializeIncludesVersion3() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        assertEquals("3.0",
                root.getString("version"));
    }

    @Test
    void serializeCrewName() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        assertEquals("The Scarred Seekers",
                root.getString("crewName"));
    }

    @Test
    void serializeSupplyAndCrewPoints() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        assertEquals(42,
                root.getInt("totalSupply"));
        assertEquals(15,
                root.getInt("crewPoints"));
    }

    @Test
    void serializeContainsAllTopLevelKeys() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        assertTrue(root.has("version"));
        assertTrue(root.has("crewName"));
        assertTrue(root.has("totalSupply"));
        assertTrue(root.has("crewPoints"));
        assertTrue(
                root.has("learnedManeuvers"));
        assertTrue(root.has("members"));
        assertTrue(
                root.has("roleAssignments"));
        assertTrue(root.has("bird"));
        assertTrue(root.has("shuttle"));
        assertTrue(root.has("rover"));
    }

    // =========================================
    // Members
    // =========================================

    @Test
    void serializeMembersCount() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        assertEquals(4,
                root.getJSONArray("members")
                        .length());
    }

    @Test
    void serializeMembersPreservesNames() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONArray members =
                root.getJSONArray("members");

        Set<String> names = new HashSet<>();
        for (int i = 0;
             i < members.length(); i++) {
            names.add(
                    members.getJSONObject(i)
                            .getString("name"));
        }
        assertTrue(
                names.contains("Kiran Voss"));
        assertTrue(
                names.contains("Nala Theron"));
        assertTrue(
                names.contains("Tarek Orin"));
        assertTrue(
                names.contains("Sera Kai"));
    }

    @Test
    void membersIncludeExplorerFields() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONObject first =
                root.getJSONArray("members")
                        .getJSONObject(0);
        assertTrue(first.has("attributes"));
        assertTrue(first.has("profession"));
        assertTrue(first.has("version"));
    }

    // =========================================
    // Role assignments
    // =========================================

    @Test
    void serializeRoleAssignments() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONObject roles =
                root.getJSONObject(
                        "roleAssignments");
        assertEquals("Kiran Voss",
                roles.getString("DELVER"));
        assertEquals("Nala Theron",
                roles.getString("SCOUT"));
        assertEquals("Tarek Orin",
                roles.getString("BURROWER"));
        assertEquals("Sera Kai",
                roles.getString("GUARD"));
    }

    @Test
    void serializeOptionalRoleIncluded() {
        Crew crew = buildTestCrew();
        Explorer fifth = makeExplorer(
                "Ryn Daro", 2, 2, 5, 2, 4, 2);
        crew.addMember(fifth);
        crew.assignRole(
                CrewRole.ARCHAEOLOGIST, fifth);

        String json =
                serializer.serialize(crew);
        JSONObject root = new JSONObject(json);
        assertEquals("Ryn Daro",
                root.getJSONObject(
                                "roleAssignments")
                        .getString("ARCHAEOLOGIST"));
    }

    // =========================================
    // Bird
    // =========================================

    @Test
    void serializeBirdFields() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONObject bird =
                root.getJSONObject("bird");
        assertEquals("Jade",
                bird.getString("name"));
        assertEquals("GUIDE",
                bird.getString("type"));
        assertEquals(4,
                bird.getInt("maxHealth"));
        assertEquals(4,
                bird.getInt("currentHealth"));
        assertEquals(3,
                bird.getInt("maxEnergy"));
        assertEquals(3,
                bird.getInt("currentEnergy"));
        assertEquals("Emerald",
                bird.getString("color"));
        assertEquals(
                "Long tail feathers",
                bird.getString("bodyFeature"));
        assertEquals("Curious",
                bird.getString("personality"));
    }

    @Test
    void serializeBirdPowersCount() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONArray powers =
                root.getJSONObject("bird")
                        .getJSONArray("powers");
        // Guide: 6 basic + Farsight = 7
        assertEquals(7, powers.length());
    }

    @Test
    void serializeBirdPowerFields() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONArray powers =
                root.getJSONObject("bird")
                        .getJSONArray("powers");

        Set<String> names = new HashSet<>();
        for (int i = 0;
             i < powers.length(); i++) {
            JSONObject p =
                    powers.getJSONObject(i);
            names.add(p.getString("name"));
            assertTrue(p.has("energyCost"));
            assertTrue(p.has("isBasic"));
            assertTrue(
                    p.has("nativeTypes"));
        }
        assertTrue(
                names.contains("Attack"));
        assertTrue(
                names.contains("Farsight"));
    }

    @Test
    void serializeBirdDamagedState() {
        Crew crew = buildTestCrew();
        crew.getBird().takeDamage(2);
        crew.getBird().spendEnergy(1);

        String json =
                serializer.serialize(crew);
        JSONObject root = new JSONObject(json);
        JSONObject bird =
                root.getJSONObject("bird");
        assertEquals(4,
                bird.getInt("maxHealth"));
        assertEquals(2,
                bird.getInt("currentHealth"));
        assertEquals(3,
                bird.getInt("maxEnergy"));
        assertEquals(2,
                bird.getInt("currentEnergy"));
    }

    // =========================================
    // Vehicles
    // =========================================

    @Test
    void serializeShuttle() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONObject shuttle =
                root.getJSONObject("shuttle");
        assertEquals("Dustrunner",
                shuttle.getString("name"));
        assertEquals("OWL",
                shuttle.getString("type"));
        assertEquals("Matte black",
                shuttle.getString("paintColor"));
        assertTrue(
                shuttle.has("currentHull"));
        assertTrue(
                shuttle.has("upgrades"));
    }

    @Test
    void serializeRover() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        JSONObject rover =
                root.getJSONObject("rover");
        assertEquals("Sandcrawler",
                rover.getString("name"));
        assertEquals("RHINO",
                rover.getString("type"));
        assertEquals("Desert tan",
                rover.getString("paintColor"));
    }

    @Test
    void serializeNullShuttle() {
        Crew crew = buildTestCrew();
        crew.setShuttle(null);
        String json =
                serializer.serialize(crew);
        JSONObject root = new JSONObject(json);
        assertTrue(root.isNull("shuttle"));
    }

    @Test
    void serializeNullRover() {
        Crew crew = buildTestCrew();
        crew.setRover(null);
        String json =
                serializer.serialize(crew);
        JSONObject root = new JSONObject(json);
        assertTrue(root.isNull("rover"));
    }

    @Test
    void serializeVehicleWithUpgrades() {
        Crew crew = buildTestCrew();
        Equipment turret = new Equipment(
                "Turret", "Mounted weapon",
                EquipmentWeight.HEAVY, 2, true);
        crew.getRover().installUpgrade(turret);

        String json =
                serializer.serialize(crew);
        JSONObject root = new JSONObject(json);
        JSONArray upgrades =
                root.getJSONObject("rover")
                        .getJSONArray("upgrades");
        assertEquals(1, upgrades.length());

        JSONObject u =
                upgrades.getJSONObject(0);
        assertEquals("Turret",
                u.getString("name"));
        assertEquals("HEAVY",
                u.getString("weight"));
        assertEquals(2,
                u.getInt("gearBonus"));
        assertTrue(
                u.getBoolean("isWeapon"));
    }

    // =========================================
    // Learned maneuvers
    // =========================================

    @Test
    void serializeLearnedManeuvers() {
        Crew crew = buildTestCrew();
        crew.addCrewPoints(10);
        crew.learnManeuver("Overwatch", 5);
        crew.learnManeuver("Pincer", 5);

        String json =
                serializer.serialize(crew);
        JSONObject root = new JSONObject(json);
        JSONArray maneuvers =
                root.getJSONArray(
                        "learnedManeuvers");
        assertEquals(2, maneuvers.length());

        Set<String> names = new HashSet<>();
        for (int i = 0;
             i < maneuvers.length(); i++) {
            names.add(
                    maneuvers.getString(i));
        }
        assertTrue(
                names.contains("Overwatch"));
        assertTrue(
                names.contains("Pincer"));
    }

    @Test
    void serializeEmptyLearnedManeuvers() {
        String json = serializer.serialize(
                buildTestCrew());
        JSONObject root = new JSONObject(json);
        assertEquals(0,
                root.getJSONArray(
                                "learnedManeuvers")
                        .length());
    }

    // =========================================
    // JSON format
    // =========================================

    @Test
    void serializeProducesPrettyPrintedJson() {
        String json = serializer.serialize(
                buildTestCrew());
        assertTrue(json.contains("    "));
        assertDoesNotThrow(
                () -> new JSONObject(json));
    }
}