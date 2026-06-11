package darkforge.crew;

import darkforge.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;

import static org.junit.jupiter.api.Assertions.*;

class CrewBuilderTest {

    private Explorer delver;
    private Explorer scout;
    private Explorer burrower;
    private Explorer guard;
    private Explorer archaeologist;
    private Bird bird;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        delver = makeExplorer("Kiran",
                2, 5, 2, 2, 4, 2);
        scout = makeExplorer("Nala",
                2, 2, 5, 4, 2, 2);
        burrower = makeExplorer("Tarek",
                5, 2, 4, 2, 2, 2);
        guard = makeExplorer("Sera",
                4, 2, 2, 5, 2, 2);
        archaeologist = makeExplorer("Zara",
                2, 2, 5, 2, 4, 2);
        bird = new Bird("Talon", BirdType.WARD,
                "Crimson", "Hooked beak", "Fierce");
    }

    // =========================================
    // Fluent chaining helper
    // =========================================

    private CrewBuilder validBase() {
        return new CrewBuilder()
                .name("Horizon Seekers")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, scout)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard)
                .bird(bird);
    }

    // =========================================
    // Happy path: 4 members, all mandatory
    // roles, Bird
    // =========================================

    @Test
    void happyPathFourMembersBuildSucceeds() {
        Crew crew = validBase().build();

        assertNotNull(crew);
        assertEquals("Horizon Seekers",
                crew.getName());
        assertEquals(4, crew.getCrewSize());
        assertTrue(
                crew.areAllMandatoryRolesFilled());
        assertNotNull(crew.getBird());
    }

    @Test
    void happyPathCrewHasCorrectRoles() {
        Crew crew = validBase().build();

        assertEquals(delver,
                crew.getAssignedExplorer(
                        CrewRole.DELVER));
        assertEquals(scout,
                crew.getAssignedExplorer(
                        CrewRole.SCOUT));
        assertEquals(burrower,
                crew.getAssignedExplorer(
                        CrewRole.BURROWER));
        assertEquals(guard,
                crew.getAssignedExplorer(
                        CrewRole.GUARD));
    }

    // =========================================
    // 5 members with Archaeologist
    // =========================================

    @Test
    void fiveMembersWithArchaeologistSucceeds() {
        Crew crew = validBase()
                .addMember(archaeologist)
                .assignRole(
                        CrewRole.ARCHAEOLOGIST,
                        archaeologist)
                .build();

        assertEquals(5, crew.getCrewSize());
        assertEquals(archaeologist,
                crew.getAssignedExplorer(
                        CrewRole.ARCHAEOLOGIST));
    }

    // =========================================
    // Missing name
    // =========================================

    @Test
    void missingNameThrowsWithMessage() {
        CrewBuilder builder = new CrewBuilder()
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, scout)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard)
                .bird(bird);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build);
        assertTrue(
                ex.getMessage().toLowerCase()
                        .contains("name"),
                "Message should mention name");
    }

    @Test
    void blankNameThrows() {
        CrewBuilder builder = validBase()
                .name("   ");

        // Re-set name to blank
        assertThrows(
                IllegalStateException.class,
                builder::build);
    }

    // =========================================
    // Fewer than 4 members
    // =========================================

    @Test
    void threeMembersThrowsWithMessage() {
        CrewBuilder builder = new CrewBuilder()
                .name("Small Crew")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, scout)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard)
                .bird(bird);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build);
        assertTrue(
                ex.getMessage().contains("4"),
                "Message should mention minimum");
    }

    @Test
    void zeroMembersThrows() {
        CrewBuilder builder = new CrewBuilder()
                .name("Empty Crew")
                .bird(bird);

        assertThrows(
                IllegalStateException.class,
                builder::build);
    }

    // =========================================
    // Missing Bird
    // =========================================

    @Test
    void missingBirdThrowsWithMessage() {
        CrewBuilder builder = new CrewBuilder()
                .name("No Bird Crew")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, scout)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build);
        assertTrue(
                ex.getMessage().toLowerCase()
                        .contains("bird"),
                "Message should mention Bird");
    }

    // =========================================
    // Missing mandatory role
    // =========================================

    @Test
    void missingScoutRoleThrows() {
        CrewBuilder builder = new CrewBuilder()
                .name("No Scout")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                // Scout intentionally omitted
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard)
                .bird(bird);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build);
        assertTrue(
                ex.getMessage().contains("Scout"),
                "Message should mention Scout");
    }

    @Test
    void missingGuardRoleThrows() {
        CrewBuilder builder = new CrewBuilder()
                .name("No Guard")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, scout)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                // Guard intentionally omitted
                .bird(bird);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build);
        assertTrue(
                ex.getMessage().contains("Guard"),
                "Message should mention Guard");
    }

    // =========================================
    // Assigned explorer not in roster
    // =========================================

    @Test
    void assignedExplorerNotInRosterThrows() {
        Explorer outsider = makeExplorer(
                "Ghost", 3, 3, 3, 3, 3, 3);

        CrewBuilder builder = new CrewBuilder()
                .name("Bad Assignment")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, outsider)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard)
                .bird(bird);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                builder::build);
        assertTrue(
                ex.getMessage().contains("Ghost"),
                "Message should mention the"
                        + " outsider's name");
        assertTrue(
                ex.getMessage().contains("roster")
                        || ex.getMessage()
                        .contains("not in"),
                "Message should indicate not"
                        + " in roster");
    }

    // =========================================
    // Fluent chaining
    // =========================================

    @Test
    void fluentChainingReturnsCrewBuilder() {
        CrewBuilder builder = new CrewBuilder()
                .name("Chain Test")
                .addMember(delver)
                .addMember(scout)
                .addMember(burrower)
                .addMember(guard)
                .assignRole(CrewRole.DELVER, delver)
                .assignRole(CrewRole.SCOUT, scout)
                .assignRole(CrewRole.BURROWER,
                        burrower)
                .assignRole(CrewRole.GUARD, guard)
                .bird(bird)
                .supply(20)
                .crewPoints(10);

        assertInstanceOf(CrewBuilder.class,
                builder);
    }

    @Test
    void supplyAndCPPassedToCrew() {
        Crew crew = validBase()
                .supply(25)
                .crewPoints(15)
                .build();

        assertEquals(25,
                crew.getTotalSupply());
        assertEquals(15,
                crew.getCrewPoints());
    }

    // =========================================
    // Optional role not required
    // =========================================

    @Test
    void archaeologistNotRequiredForBuild() {
        // 4 members, no Archaeologist assigned
        Crew crew = validBase().build();
        assertNull(
                crew.getAssignedExplorer(
                        CrewRole.ARCHAEOLOGIST));
    }

    // =========================================
    // Bird is passed through
    // =========================================

    @Test
    void birdSetOnBuiltCrew() {
        Crew crew = validBase().build();
        assertEquals("Talon",
                crew.getBird().getName());
        assertEquals(BirdType.WARD,
                crew.getBird().getType());
    }

    // =========================================
    // Helper
    // =========================================

    private Explorer makeExplorer(
            String name,
            int str, int agl, int log,
            int per, int ins, int emp) {
        Explorer explorer = new Explorer(
                name, "Test") {
            public Attribute getKeyAttribute() {
                return Attribute.AGILITY;
            }
            public java.util.List<Talent>
            getKeyTalents() {
                return java.util.List.of();
            }
            public java.util.List<
                    java.util.List<Equipment>>
            getStartingEquipmentSets() {
                return java.util.List.of();
            }
            public String getProfessionName() {
                return "Enforcer";
            }
            public java.util.List<Specialty>
            getSpecialties() {
                return java.util.List.of();
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
}