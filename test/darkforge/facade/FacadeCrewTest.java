package darkforge.facade;

import darkforge.crew.*;
import darkforge.data.GameDataProvider;
import darkforge.model.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeCrew singleton — builder,
 * role suggestions, talent browsing, Garuda
 * powers, and persistence delegation.
 */
class FacadeCrewTest {

    private FacadeCrew facade;

    @BeforeAll
    static void initData() {
        GameDataProvider.getTheInstance()
                .initialize();
    }

    @BeforeEach
    void setUp() {
        facade = FacadeCrew.getTheInstance();
    }

    // =========================================
    // Singleton
    // =========================================

    @Test
    void singletonReturnsSameInstance() {
        assertSame(
                FacadeCrew.getTheInstance(),
                FacadeCrew.getTheInstance());
    }

    // =========================================
    // Builder
    // =========================================

    @Test
    void newCrewBuilderReturnsInstance() {
        assertNotNull(
                facade.newCrewBuilder());
    }

    @Test
    void newCrewBuilderReturnsFreshInstance() {
        CrewBuilder a =
                facade.newCrewBuilder();
        CrewBuilder b =
                facade.newCrewBuilder();
        assertNotSame(a, b);
    }

    // =========================================
    // Role suggestions
    // =========================================

    @Test
    void suggestRolesReturnsAllRoles() {
        Crew crew = buildMinimalCrew();
        Map<CrewRole, List<Explorer>>
                suggestions =
                facade.suggestRoles(crew);

        for (CrewRole role
                : CrewRole.values()) {
            assertTrue(
                    suggestions.containsKey(role),
                    "Missing: " + role);
            assertFalse(
                    suggestions.get(role)
                            .isEmpty(),
                    role + " needs candidates");
        }
    }

    @Test
    void optimalAssignmentFillsMandatory() {
        Crew crew = buildMinimalCrew();
        Map<CrewRole, Explorer> optimal =
                facade.getOptimalAssignment(crew);

        for (CrewRole role
                : CrewRole.values()) {
            if (!role.isOptional()) {
                assertTrue(
                        optimal.containsKey(role),
                        role + " should be"
                                + " assigned");
            }
        }
    }

    @Test
    void optimalAssignmentNoDuplicates() {
        Crew crew = buildMinimalCrew();
        Map<CrewRole, Explorer> optimal =
                facade.getOptimalAssignment(crew);

        Set<Explorer> assigned =
                new HashSet<>(optimal.values());
        assertEquals(optimal.size(),
                assigned.size());
    }

    // =========================================
    // Talent browsing
    // =========================================

    @Test
    void browseTalentsReturnsNonNull() {
        List<Talent> talents =
                facade.browseTalents(
                        TalentCategory.COMBAT);
        assertNotNull(talents);
    }

    @Test
    void searchTalentsNonexistentIsEmpty() {
        List<Talent> results =
                facade.searchTalents(
                        "zzznonexistent");
        assertTrue(results.isEmpty());
    }

    // =========================================
    // Garuda powers
    // =========================================

    @Test
    void getAvailablePowersNonEmpty() {
        for (BirdType type
                : BirdType.values()) {
            List<GarudaPower> powers =
                    facade.getAvailablePowers(
                            type);
            assertNotNull(powers);
            assertFalse(powers.isEmpty(),
                    type + " needs powers");
        }
    }

    @Test
    void getAvailablePowersAdvancedOnly() {
        List<GarudaPower> powers =
                facade.getAvailablePowers(
                        BirdType.WARD);
        for (GarudaPower p : powers) {
            assertFalse(p.isBasic(),
                    p.getName()
                            + " should be advanced");
        }
    }

    // =========================================
    // Persistence delegation
    // =========================================

    @Test
    void listCrewSavesReturnsNonNull()
            throws Exception {
        List<java.nio.file.Path> saves =
                facade.listCrewSaves();
        assertNotNull(saves);
    }

    // =========================================
    // Helper
    // =========================================

    private Crew buildMinimalCrew() {
        Crew crew = new Crew("Test Crew");
        String[] names = {
                "Kiran", "Nala",
                "Tarek", "Sera"};
        int[][] stats = {
                {2, 5, 2, 2, 4, 2},
                {2, 2, 5, 4, 2, 2},
                {5, 2, 4, 2, 2, 2},
                {4, 2, 2, 5, 2, 2}};

        for (int i = 0; i < 4; i++) {
            crew.addMember(makeExplorer(
                    names[i], stats[i]));
        }
        return crew;
    }

    private Explorer makeExplorer(
            String name, int[] s) {
        Explorer e = new Explorer(
                name, "Test") {
            public Attribute getKeyAttribute() {
                return Attribute.AGILITY;
            }
            public List<Talent>
            getKeyTalents() {
                return List.of();
            }
            public List<List<Equipment>>
            getStartingEquipmentSets() {
                return List.of();
            }
            public String
            getProfessionName() {
                return "Enforcer";
            }
            public List<Specialty>
            getSpecialties() {
                return List.of();
            }
        };
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        attrs.put(Attribute.STRENGTH, s[0]);
        attrs.put(Attribute.AGILITY, s[1]);
        attrs.put(Attribute.LOGIC, s[2]);
        attrs.put(
                Attribute.PERCEPTION, s[3]);
        attrs.put(Attribute.INSIGHT, s[4]);
        attrs.put(Attribute.EMPATHY, s[5]);
        e.setAttributes(attrs);
        return e;
    }
}