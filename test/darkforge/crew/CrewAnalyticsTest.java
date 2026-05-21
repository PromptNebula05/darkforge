package darkforge.crew;

import darkforge.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CrewAnalyticsTest {

    private Crew crew;
    private CrewAnalytics analytics;
    private Explorer delver;
    private Explorer scout;
    private Explorer burrower;
    private Explorer guard;

    // =========================================
    // Setup: 4-member crew with known attrs
    // =========================================

    @BeforeEach
    void setUp() {
        crew = new Crew("Test Crew");

        // STR=2 AGL=5 LOG=2 PER=2 INS=4 EMP=2
        delver = makeExplorer("Kiran",
                2, 5, 2, 2, 4, 2);
        // STR=2 AGL=2 LOG=5 PER=4 INS=2 EMP=2
        scout = makeExplorer("Nala",
                2, 2, 5, 4, 2, 2);
        // STR=5 AGL=2 LOG=4 PER=2 INS=2 EMP=2
        burrower = makeExplorer("Tarek",
                5, 2, 4, 2, 2, 2);
        // STR=4 AGL=2 LOG=2 PER=5 INS=2 EMP=2
        guard = makeExplorer("Sera",
                4, 2, 2, 5, 2, 2);

        crew.addMember(delver);
        crew.addMember(scout);
        crew.addMember(burrower);
        crew.addMember(guard);

        analytics = new CrewAnalytics(crew);
    }

    // =========================================
    // Attribute averages
    // =========================================

    @Test
    void averageStrengthMatchesManual() {
        // (2+2+5+4)/4 = 3.25
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        assertEquals(3.25,
                avgs.get(Attribute.STRENGTH),
                0.001);
    }

    @Test
    void averageAgilityMatchesManual() {
        // (5+2+2+2)/4 = 2.75
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        assertEquals(2.75,
                avgs.get(Attribute.AGILITY),
                0.001);
    }

    @Test
    void averageLogicMatchesManual() {
        // (2+5+4+2)/4 = 3.25
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        assertEquals(3.25,
                avgs.get(Attribute.LOGIC),
                0.001);
    }

    @Test
    void averagePerceptionMatchesManual() {
        // (2+4+2+5)/4 = 3.25
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        assertEquals(3.25,
                avgs.get(Attribute.PERCEPTION),
                0.001);
    }

    @Test
    void averageInsightMatchesManual() {
        // (4+2+2+2)/4 = 2.5
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        assertEquals(2.5,
                avgs.get(Attribute.INSIGHT),
                0.001);
    }

    @Test
    void averageEmpathyMatchesManual() {
        // (2+2+2+2)/4 = 2.0
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        assertEquals(2.0,
                avgs.get(Attribute.EMPATHY),
                0.001);
    }

    @Test
    void allSixAttributesPresent() {
        EnumMap<Attribute, Double> avgs =
                analytics.getAttributeAverages();
        for (Attribute attr
                : Attribute.values()) {
            assertTrue(
                    avgs.containsKey(attr),
                    "Missing: " + attr);
        }
    }

    // =========================================
    // Talent coverage
    // =========================================

    @Test
    void talentCoverageCountsCombat() {
        delver.addTalent(new Talent(
                "Blademaster", "desc",
                TalentCategory.COMBAT,
                3, "effect"));
        scout.addTalent(new Talent(
                "Quick Draw", "desc",
                TalentCategory.COMBAT,
                3, "effect"));
        burrower.addTalent(new Talent(
                "Iron Fist", "desc",
                TalentCategory.COMBAT,
                3, "effect"));

        EnumMap<TalentCategory, Integer>
                coverage =
                analytics.getTalentCoverage();
        assertEquals(3,
                coverage.get(
                        TalentCategory.COMBAT));
    }

    @Test
    void talentCoverageZeroForKnowledge() {
        // No KNOWLEDGE talents added
        delver.addTalent(new Talent(
                "Blademaster", "desc",
                TalentCategory.COMBAT,
                3, "effect"));

        EnumMap<TalentCategory, Integer>
                coverage =
                analytics.getTalentCoverage();
        assertEquals(0,
                coverage.get(
                        TalentCategory.KNOWLEDGE));
    }

    @Test
    void coverageHasAllCategories() {
        EnumMap<TalentCategory, Integer>
                coverage =
                analytics.getTalentCoverage();
        for (TalentCategory cat
                : TalentCategory.values()) {
            assertTrue(
                    coverage.containsKey(cat),
                    "Missing: " + cat);
        }
    }

    @Test
    void weakCategoriesIncludesKnowledge() {
        delver.addTalent(new Talent(
                "Blademaster", "desc",
                TalentCategory.COMBAT,
                3, "effect"));

        List<TalentCategory> weak =
                analytics.getWeakCategories();
        assertTrue(
                weak.contains(
                        TalentCategory.KNOWLEDGE),
                "KNOWLEDGE should be weak");
        assertFalse(
                weak.contains(
                        TalentCategory.COMBAT),
                "COMBAT should not be weak");
    }

    @Test
    void strongCategoriesReturnsCombat() {
        delver.addTalent(new Talent(
                "Blademaster", "desc",
                TalentCategory.COMBAT,
                3, "effect"));
        scout.addTalent(new Talent(
                "Quick Draw", "desc",
                TalentCategory.COMBAT,
                3, "effect"));

        List<TalentCategory> strong =
                analytics.getStrongCategories();
        assertTrue(
                strong.contains(
                        TalentCategory.COMBAT));
    }

    @Test
    void totalTalentCountSumsAcressCrew() {
        delver.addTalent(new Talent(
                "A", "d", TalentCategory.COMBAT,
                3, "e"));
        scout.addTalent(new Talent(
                "B", "d", TalentCategory.SOCIAL,
                3, "e"));
        scout.addTalent(new Talent(
                "C", "d", TalentCategory.KNOWLEDGE,
                3, "e"));
        assertEquals(3,
                analytics.getTotalTalentCount());
    }

    // =========================================
    // Optimal role assignment
    // =========================================

    @Test
    void optimalAssignmentFillsAllMandatory() {
        Map<CrewRole, Explorer> optimal =
                analytics
                        .getOptimalRoleAssignment();

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
    void optimalDelverIsKiran() {
        // Kiran: AGL 5 + INS 4 = 9 (highest)
        Map<CrewRole, Explorer> optimal =
                analytics
                        .getOptimalRoleAssignment();
        assertEquals("Kiran",
                optimal.get(CrewRole.DELVER)
                        .getName());
    }

    @Test
    void optimalGuardIsSera() {
        // Sera: STR 4 + PER 5 = 9 (highest)
        Map<CrewRole, Explorer> optimal =
                analytics
                        .getOptimalRoleAssignment();
        assertEquals("Sera",
                optimal.get(CrewRole.GUARD)
                        .getName());
    }

    @Test
    void optimalNoDoubleAssignment() {
        Map<CrewRole, Explorer> optimal =
                analytics
                        .getOptimalRoleAssignment();
        Set<Explorer> assignedExplorers =
                new HashSet<>(
                        optimal.values());
        assertEquals(
                optimal.size(),
                assignedExplorers.size(),
                "No explorer should be assigned"
                        + " to two roles");
    }

    @Test
    void optimalWith4MembersHas4Roles() {
        Map<CrewRole, Explorer> optimal =
                analytics
                        .getOptimalRoleAssignment();
        assertEquals(4, optimal.size(),
                "4 members should fill"
                        + " 4 roles");
    }

    // =========================================
    // Empty crew edge case
    // =========================================

    @Test
    void emptyCrewAllAveragesZero() {
        Crew empty = new Crew("Empty");
        CrewAnalytics emptyAnalytics =
                new CrewAnalytics(empty);

        EnumMap<Attribute, Double> avgs =
                emptyAnalytics
                        .getAttributeAverages();
        for (Attribute attr
                : Attribute.values()) {
            assertEquals(0.0,
                    avgs.get(attr), 0.001,
                    attr + " should be 0.0");
        }
    }

    @Test
    void emptyCrewAllCoverageZero() {
        Crew empty = new Crew("Empty");
        CrewAnalytics emptyAnalytics =
                new CrewAnalytics(empty);

        EnumMap<TalentCategory, Integer>
                coverage = emptyAnalytics
                .getTalentCoverage();
        for (TalentCategory cat
                : TalentCategory.values()) {
            assertEquals(0,
                    coverage.get(cat),
                    cat + " should be 0");
        }
    }

    @Test
    void emptyCrewOptimalAssignmentEmpty() {
        Crew empty = new Crew("Empty");
        CrewAnalytics emptyAnalytics =
                new CrewAnalytics(empty);

        assertTrue(emptyAnalytics
                .getOptimalRoleAssignment()
                .isEmpty());
    }

    @Test
    void emptyCrewTotalTalentsZero() {
        Crew empty = new Crew("Empty");
        CrewAnalytics emptyAnalytics =
                new CrewAnalytics(empty);
        assertEquals(0,
                emptyAnalytics
                        .getTotalTalentCount());
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