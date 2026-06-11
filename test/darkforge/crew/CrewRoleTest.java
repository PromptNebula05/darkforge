package darkforge.crew;

import darkforge.model.Attribute;
import darkforge.model.Explorer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CrewRoleTest {

    // =========================================
    // Enum completeness
    // =========================================

    @Test
    void allFiveRolesPresent() {
        CrewRole[] roles = CrewRole.values();
        assertEquals(5, roles.length);
        assertNotNull(CrewRole.valueOf("DELVER"));
        assertNotNull(CrewRole.valueOf("SCOUT"));
        assertNotNull(CrewRole.valueOf("BURROWER"));
        assertNotNull(CrewRole.valueOf("GUARD"));
        assertNotNull(CrewRole.valueOf("ARCHAEOLOGIST"));
    }

    // =========================================
    // isOptional
    // =========================================

    @Test
    void onlyArchaeologistIsOptional() {
        for (CrewRole role : CrewRole.values()) {
            if (role == CrewRole.ARCHAEOLOGIST) {
                assertTrue(role.isOptional(),
                        "ARCHAEOLOGIST should be optional");
            } else {
                assertFalse(role.isOptional(),
                        role.name() + " should be mandatory");
            }
        }
    }

    // =========================================
    // Display names
    // =========================================

    @Test
    void displayNamesMatchExpected() {
        assertEquals("Delver", CrewRole.DELVER.getDisplayName());
        assertEquals("Scout", CrewRole.SCOUT.getDisplayName());
        assertEquals("Burrower", CrewRole.BURROWER.getDisplayName());
        assertEquals("Guard", CrewRole.GUARD.getDisplayName());
        assertEquals("Archaeologist",
                CrewRole.ARCHAEOLOGIST.getDisplayName());
    }

    // =========================================
    // Attribute pairs
    // =========================================

    @Test
    void delverAttributes() {
        assertEquals(Attribute.AGILITY,
                CrewRole.DELVER.getPrimaryAttribute());
        assertEquals(Attribute.INSIGHT,
                CrewRole.DELVER.getSecondaryAttribute());
    }

    @Test
    void scoutAttributes() {
        assertEquals(Attribute.PERCEPTION,
                CrewRole.SCOUT.getPrimaryAttribute());
        assertEquals(Attribute.LOGIC,
                CrewRole.SCOUT.getSecondaryAttribute());
    }

    @Test
    void burrowerAttributes() {
        assertEquals(Attribute.STRENGTH,
                CrewRole.BURROWER.getPrimaryAttribute());
        assertEquals(Attribute.LOGIC,
                CrewRole.BURROWER.getSecondaryAttribute());
    }

    @Test
    void guardAttributes() {
        assertEquals(Attribute.STRENGTH,
                CrewRole.GUARD.getPrimaryAttribute());
        assertEquals(Attribute.PERCEPTION,
                CrewRole.GUARD.getSecondaryAttribute());
    }

    @Test
    void archaeologistAttributes() {
        assertEquals(Attribute.LOGIC,
                CrewRole.ARCHAEOLOGIST.getPrimaryAttribute());
        assertEquals(Attribute.INSIGHT,
                CrewRole.ARCHAEOLOGIST.getSecondaryAttribute());
    }

    // =========================================
    // Starting maneuvers
    // =========================================

    @Test
    void startingManeuvers() {
        assertEquals("Rally",
                CrewRole.DELVER.getStartingManeuver());
        assertEquals("Situational Awareness",
                CrewRole.SCOUT.getStartingManeuver());
        assertEquals("Destabilize",
                CrewRole.BURROWER.getStartingManeuver());
        assertEquals("Flank",
                CrewRole.GUARD.getStartingManeuver());
        assertEquals("Analyze",
                CrewRole.ARCHAEOLOGIST.getStartingManeuver());
    }

    // =========================================
    // getRoleFitness
    // =========================================

    @Test
    void roleFitnessSumsCorrectAttributes() {
        Explorer explorer = createTestExplorer();

        // Delver: AGL(4) + INS(3) = 7
        assertEquals(7, CrewRole.DELVER.getRoleFitness(explorer));
        // Scout: PER(5) + LOG(2) = 7
        assertEquals(7, CrewRole.SCOUT.getRoleFitness(explorer));
        // Burrower: STR(3) + LOG(2) = 5
        assertEquals(5, CrewRole.BURROWER.getRoleFitness(explorer));
        // Guard: STR(3) + PER(5) = 8
        assertEquals(8, CrewRole.GUARD.getRoleFitness(explorer));
        // Archaeologist: LOG(2) + INS(3) = 5
        assertEquals(5,
                CrewRole.ARCHAEOLOGIST.getRoleFitness(explorer));
    }

    // =========================================
    // toString
    // =========================================

    @ParameterizedTest
    @EnumSource(CrewRole.class)
    void toStringContainsDisplayName(CrewRole role) {
        assertTrue(role.toString()
                .contains(role.getDisplayName()));
    }

    @Test
    void toStringShowsOptionalTag() {
        assertTrue(CrewRole.ARCHAEOLOGIST.toString()
                .contains("optional"));
        assertTrue(CrewRole.DELVER.toString()
                .contains("mandatory"));
    }

    // =========================================
    // Test helper — anonymous Explorer stub
    // =========================================

    private Explorer createTestExplorer() {
        Explorer explorer = new Explorer("Test", "Stub") {
            public Attribute getKeyAttribute() {
                return Attribute.AGILITY;
            }
            public java.util.List<darkforge.model.Talent> getKeyTalents() {
                return java.util.List.of();
            }
            public java.util.List<java.util.List<darkforge.model.Equipment>>
            getStartingEquipmentSets() {
                return java.util.List.of();
            }
            public String getProfessionName() {
                return "TestProfession";
            }
            public java.util.List<darkforge.model.Specialty>
            getSpecialties() {
                return java.util.List.of();
            }
        };

        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        attrs.put(Attribute.STRENGTH, 3);
        attrs.put(Attribute.AGILITY, 4);
        attrs.put(Attribute.LOGIC, 2);
        attrs.put(Attribute.PERCEPTION, 5);
        attrs.put(Attribute.INSIGHT, 3);
        attrs.put(Attribute.EMPATHY, 2);
        explorer.setAttributes(attrs);

        return explorer;
    }
}