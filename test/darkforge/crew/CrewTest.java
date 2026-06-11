package darkforge.crew;

import darkforge.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CrewTest {

    private Crew crew;
    private Explorer delverExplorer;
    private Explorer scoutExplorer;
    private Explorer burrowerExplorer;
    private Explorer guardExplorer;
    private Explorer archaeologistExplorer;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        crew = new Crew("Horizon Seekers");

        // High AGL+INS → best Delver
        delverExplorer = makeExplorer(
                "Kiran", 2, 5, 2, 2, 4, 2);
        // High PER+LOG → best Scout
        scoutExplorer = makeExplorer(
                "Nala", 2, 2, 5, 4, 2, 2);
        // High STR+LOG → best Burrower
        burrowerExplorer = makeExplorer(
                "Tarek", 5, 2, 4, 2, 2, 2);
        // High STR+PER → best Guard
        guardExplorer = makeExplorer(
                "Sera", 4, 2, 2, 5, 2, 2);
        // High LOG+INS → best Archaeologist
        archaeologistExplorer = makeExplorer(
                "Zara", 2, 2, 5, 2, 4, 2);
    }

    // =========================================
    // Roster management
    // =========================================

    @Test
    void addFiveMembersSucceeds() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addMember(burrowerExplorer);
        crew.addMember(guardExplorer);
        crew.addMember(archaeologistExplorer);
        assertEquals(5, crew.getCrewSize());
    }

    @Test
    void addSixthMemberThrowsException() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addMember(burrowerExplorer);
        crew.addMember(guardExplorer);
        crew.addMember(archaeologistExplorer);

        Explorer sixth = makeExplorer(
                "Extra", 3, 3, 3, 3, 3, 3);
        assertThrows(
                IllegalStateException.class,
                () -> crew.addMember(sixth));
    }

    @Test
    void removeMemberReducesSize() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.removeMember(delverExplorer);
        assertEquals(1, crew.getCrewSize());
    }

    @Test
    void removeMemberUnassignsRole() {
        crew.addMember(delverExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);
        crew.removeMember(delverExplorer);
        assertNull(crew.getAssignedExplorer(
                CrewRole.DELVER));
    }

    @Test
    void getMembersIsUnmodifiable() {
        crew.addMember(delverExplorer);
        assertThrows(
                UnsupportedOperationException.class,
                () -> crew.getMembers()
                        .add(scoutExplorer));
    }

    // =========================================
    // Role assignment (EnumMap)
    // =========================================

    @Test
    void assignAllMandatoryRoles() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addMember(burrowerExplorer);
        crew.addMember(guardExplorer);

        crew.assignRole(CrewRole.DELVER,
                delverExplorer);
        crew.assignRole(CrewRole.SCOUT,
                scoutExplorer);
        crew.assignRole(CrewRole.BURROWER,
                burrowerExplorer);
        crew.assignRole(CrewRole.GUARD,
                guardExplorer);

        assertTrue(
                crew.areAllMandatoryRolesFilled());
    }

    @Test
    void mandatoryRolesNotFilledWithoutAll() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);
        crew.assignRole(CrewRole.SCOUT,
                scoutExplorer);

        assertFalse(
                crew.areAllMandatoryRolesFilled());
    }

    @Test
    void assignNonMemberThrowsException() {
        Explorer outsider = makeExplorer(
                "Outsider", 3, 3, 3, 3, 3, 3);
        assertThrows(
                IllegalArgumentException.class,
                () -> crew.assignRole(
                        CrewRole.DELVER, outsider));
    }

    @Test
    void getAssignedRoleReverseLookup() {
        crew.addMember(delverExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);

        assertEquals(CrewRole.DELVER,
                crew.getAssignedRole(
                        delverExplorer));
    }

    @Test
    void getAssignedRoleReturnsNullIfUnassigned() {
        crew.addMember(delverExplorer);
        assertNull(
                crew.getAssignedRole(
                        delverExplorer));
    }

    @Test
    void getAssignedExplorerReturnsCorrect() {
        crew.addMember(scoutExplorer);
        crew.assignRole(CrewRole.SCOUT,
                scoutExplorer);
        assertEquals(scoutExplorer,
                crew.getAssignedExplorer(
                        CrewRole.SCOUT));
    }

    @Test
    void getRoleAssignmentsIsUnmodifiable() {
        crew.addMember(delverExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);
        assertThrows(
                UnsupportedOperationException.class,
                () -> crew.getRoleAssignments()
                        .put(CrewRole.SCOUT,
                                scoutExplorer));
    }

    // =========================================
    // suggestRoleAssignments
    // =========================================

    @Test
    void suggestRoleAssignmentsReturnsAllRoles() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addMember(burrowerExplorer);
        crew.addMember(guardExplorer);

        Map<CrewRole, List<Explorer>>
                suggestions =
                crew.suggestRoleAssignments();

        for (CrewRole role : CrewRole.values()) {
            assertTrue(
                    suggestions.containsKey(role),
                    "Should have suggestion for "
                            + role);
        }
    }

    @Test
    void suggestDelverRanksHighAglInsFirst() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addMember(burrowerExplorer);
        crew.addMember(guardExplorer);

        Map<CrewRole, List<Explorer>>
                suggestions =
                crew.suggestRoleAssignments();
        List<Explorer> delverRanked =
                suggestions.get(CrewRole.DELVER);

        // Kiran: AGL 5 + INS 4 = 9 (highest)
        assertEquals("Kiran",
                delverRanked.get(0).getName());
    }

    @Test
    void suggestScoutRanksHighPerLogFirst() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addMember(burrowerExplorer);
        crew.addMember(guardExplorer);

        Map<CrewRole, List<Explorer>>
                suggestions =
                crew.suggestRoleAssignments();
        List<Explorer> scoutRanked =
                suggestions.get(CrewRole.SCOUT);

        // Nala: PER 4 + LOG 5 = 9 (highest)
        assertEquals("Nala",
                scoutRanked.get(0).getName());
    }

    // =========================================
    // Supply management
    // =========================================

    @Test
    void addAndConsumeSupply() {
        crew.addSupply(20);
        assertTrue(crew.consumeSupply(15));
        assertEquals(5, crew.getTotalSupply());
    }

    @Test
    void consumeSupplyReturnsFalseWhenInsufficient() {
        crew.addSupply(5);
        assertFalse(crew.consumeSupply(10));
        assertEquals(5, crew.getTotalSupply(),
                "Supply should not change on"
                        + " failed consume");
    }

    @Test
    void supplyPerExplorerCalculation() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addSupply(20);
        // 20 / 2 = 10
        assertEquals(10,
                crew.getSupplyPerExplorer());
    }

    @Test
    void supplyPerExplorerZeroWhenNoMembers() {
        crew.addSupply(20);
        assertEquals(0,
                crew.getSupplyPerExplorer());
    }

    @Test
    void isLowOnSupplyTrueWhenPerExplorerLe3() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.addSupply(6);
        // 6 / 2 = 3 → low
        assertTrue(crew.isLowOnSupply());
    }

    @Test
    void isLowOnSupplyFalseWhenAbove3() {
        crew.addMember(delverExplorer);
        crew.addSupply(20);
        // 20 / 1 = 20 → not low
        assertFalse(crew.isLowOnSupply());
    }

    // =========================================
    // Crew maneuvers
    // =========================================

    @Test
    void learnManeuverDeductsCPAndAdds() {
        crew.addCrewPoints(10);
        crew.learnManeuver("Overwatch", 5);
        assertEquals(5, crew.getCrewPoints());
        assertTrue(crew.getAvailableManeuvers()
                .contains("Overwatch"));
    }

    @Test
    void learnManeuverInsufficientCPThrows() {
        crew.addCrewPoints(2);
        assertThrows(
                IllegalStateException.class,
                () -> crew.learnManeuver(
                        "Overwatch", 5));
    }

    @Test
    void availableManeuversIncludesRoleStarting() {
        crew.addMember(delverExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);

        Set<String> maneuvers =
                crew.getAvailableManeuvers();
        assertTrue(maneuvers.contains("Rally"),
                "Delver starting maneuver"
                        + " should be available");
    }

    @Test
    void availableManeuversIncludesBothTypes() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);
        crew.assignRole(CrewRole.SCOUT,
                scoutExplorer);
        crew.addCrewPoints(5);
        crew.learnManeuver("Overwatch", 5);

        Set<String> maneuvers =
                crew.getAvailableManeuvers();
        assertTrue(maneuvers.contains("Rally"));
        assertTrue(maneuvers.contains(
                "Situational Awareness"));
        assertTrue(
                maneuvers.contains("Overwatch"));
    }

    @Test
    void availableManeuversIsUnmodifiable() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> crew.getAvailableManeuvers()
                        .add("Hack"));
    }

    // =========================================
    // Bird & Vehicles
    // =========================================

    @Test
    void setBirdAndGet() {
        Bird ward = new Bird("Talon",
                BirdType.WARD, "Crimson",
                "Hooked beak", "Fierce");
        crew.setBird(ward);
        assertEquals(ward, crew.getBird());
    }

    @Test
    void birdDefaultsToNull() {
        assertNull(crew.getBird());
    }

    // =========================================
    // Display
    // =========================================

    @Test
    void toFormattedStringContainsCrewName() {
        String output =
                crew.toFormattedString();
        assertTrue(output.contains(
                "Horizon Seekers"));
    }

    @Test
    void toFormattedStringContainsMemberNames() {
        crew.addMember(delverExplorer);
        crew.addMember(scoutExplorer);
        String output =
                crew.toFormattedString();
        assertTrue(output.contains("Kiran"));
        assertTrue(output.contains("Nala"));
    }

    @Test
    void toFormattedStringContainsRoleLabels() {
        crew.addMember(delverExplorer);
        crew.assignRole(CrewRole.DELVER,
                delverExplorer);
        String output =
                crew.toFormattedString();
        assertTrue(output.contains("Delver"));
    }

    @Test
    void toSummaryContainsNameAndSize() {
        crew.addMember(delverExplorer);
        String summary = crew.toSummary();
        assertTrue(summary.contains(
                "Horizon Seekers"));
        assertTrue(summary.contains(
                "1 members"));
    }

    @Test
    void toSummaryShowsNoBirdWhenNull() {
        String summary = crew.toSummary();
        assertTrue(summary.contains(
                "None Bird: None"));
    }

    @Test
    void toSummaryShowsBirdWhenSet() {
        Bird guide = new Bird("Jade",
                BirdType.GUIDE, "Emerald",
                "Long tail", "Curious");
        crew.setBird(guide);
        String summary = crew.toSummary();
        assertTrue(
                summary.contains("Guide"));
        assertTrue(
                summary.contains("Jade"));
    }

    // =========================================
    // Getters
    // =========================================

    @Test
    void getNameReturnsConstructorName() {
        assertEquals("Horizon Seekers",
                crew.getName());
    }

    @Test
    void addCrewPointsAndGet() {
        crew.addCrewPoints(15);
        assertEquals(15, crew.getCrewPoints());
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