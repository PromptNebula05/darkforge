package darkforge.data;

import darkforge.model.Talent;
import darkforge.model.TalentCategory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TalentRegistryTest {

    private TalentRegistry registry;

    // =========================================
    // Test data
    // =========================================

    private Talent blademaster;
    private Talent quickDraw;
    private Talent diplomat;
    private Talent streetSmart;
    private Talent fieldMedic;
    private Talent researcher;
    private Talent shadowWalk;

    @BeforeEach
    void setUp() {
        blademaster = talent("Blademaster",
                "Melee combat expert",
                TalentCategory.COMBAT,
                "Extra damage in melee");
        quickDraw = talent("Quick Draw",
                "Fast weapon draw",
                TalentCategory.COMBAT,
                "Draw weapon as free action");
        diplomat = talent("Diplomat",
                "Skilled negotiator",
                TalentCategory.SOCIAL,
                "Bonus to negotiation rolls");
        streetSmart = talent("Street Smart",
                "Knows the underworld",
                TalentCategory.SOCIAL,
                "Smart navigation of underworld");
        fieldMedic = talent("Field Medic",
                "Emergency healing",
                TalentCategory.RECOVERY,
                "Heal one extra HP per rest");
        researcher = talent("Researcher",
                "Academic knowledge",
                TalentCategory.KNOWLEDGE,
                "Bonus to research rolls");
        shadowWalk = talent("Shadow Walk",
                "Move unseen in shadows",
                TalentCategory.STEALTH_MOBILITY,
                "Stealth bonus in darkness");

        List<Talent> talents = List.of(
                blademaster, quickDraw, diplomat,
                streetSmart, fieldMedic, researcher,
                shadowWalk);

        // Profession -> talent name mapping
        Map<String, List<String>> profMap =
                new LinkedHashMap<>();
        profMap.put("Enforcer", List.of(
                "Blademaster", "Quick Draw",
                "Field Medic"));
        profMap.put("Scholar", List.of(
                "Researcher", "Diplomat",
                "Field Medic"));
        profMap.put("Roughneck", List.of(
                "Blademaster", "Street Smart",
                "Shadow Walk"));

        registry = new TalentRegistry(
                talents, profMap);
    }

    // =========================================
    // getByCategory
    // =========================================

    @Test
    void getByCategoryCombatReturns2() {
        List<Talent> combat = registry
                .getByCategory(TalentCategory.COMBAT);
        assertEquals(2, combat.size());
    }

    @Test
    void getByCategoryCombatContainsExpected() {
        List<Talent> combat = registry
                .getByCategory(TalentCategory.COMBAT);
        List<String> names = combat.stream()
                .map(Talent::getName)
                .toList();
        assertTrue(
                names.contains("Blademaster"));
        assertTrue(
                names.contains("Quick Draw"));
    }

    @Test
    void getByCategorySocialReturns2() {
        List<Talent> social = registry
                .getByCategory(TalentCategory.SOCIAL);
        assertEquals(2, social.size());
    }

    @Test
    void getByCategoryRecoveryReturns1() {
        assertEquals(1, registry
                .getByCategory(
                        TalentCategory.RECOVERY)
                .size());
    }

    @Test
    void getByCategoryEmptyForUnusedCategory() {
        List<Talent> equipment = registry
                .getByCategory(
                        TalentCategory.EQUIPMENT);
        assertTrue(equipment.isEmpty());
    }

    // =========================================
    // getEligibleForProfession
    // =========================================

    @Test
    void scholarEligibleReturns3() {
        List<Talent> eligible = registry
                .getEligibleForProfession("Scholar");
        assertEquals(3, eligible.size());
    }

    @Test
    void scholarEligibleContainsResearcher() {
        List<Talent> eligible = registry
                .getEligibleForProfession("Scholar");
        List<String> names = eligible.stream()
                .map(Talent::getName)
                .toList();
        assertTrue(
                names.contains("Researcher"));
        assertTrue(
                names.contains("Diplomat"));
        assertTrue(
                names.contains("Field Medic"));
    }

    @Test
    void enforcerEligibleContainsCombatTalents() {
        List<Talent> eligible = registry
                .getEligibleForProfession(
                        "Enforcer");
        List<String> names = eligible.stream()
                .map(Talent::getName)
                .toList();
        assertTrue(
                names.contains("Blademaster"));
        assertTrue(
                names.contains("Quick Draw"));
    }

    @Test
    void unknownProfessionReturnsEmpty() {
        List<Talent> eligible = registry
                .getEligibleForProfession(
                        "NonExistent");
        assertTrue(eligible.isEmpty());
    }

    @Test
    void scholarAndEnforcerShareFieldMedic() {
        List<Talent> scholarTalents = registry
                .getEligibleForProfession("Scholar");
        List<Talent> enforcerTalents = registry
                .getEligibleForProfession(
                        "Enforcer");

        boolean scholarHas = scholarTalents
                .stream()
                .anyMatch(t -> t.getName()
                        .equals("Field Medic"));
        boolean enforcerHas = enforcerTalents
                .stream()
                .anyMatch(t -> t.getName()
                        .equals("Field Medic"));

        assertTrue(scholarHas && enforcerHas,
                "Field Medic should be eligible"
                        + " for both Scholar and"
                        + " Enforcer");
    }

    // =========================================
    // search (partial match)
    // =========================================

    @Test
    void searchSmartFindsStreetSmart() {
        List<Talent> results =
                registry.search("smart");
        assertFalse(results.isEmpty());
        assertTrue(results.stream()
                .anyMatch(t -> t.getName()
                        .equals("Street Smart")));
    }

    @Test
    void searchSmartAlsoMatchesEffect() {
        // "Street Smart" has "Smart navigation"
        // in effect
        List<Talent> results =
                registry.search("smart");
        assertTrue(results.stream()
                .anyMatch(t -> t.getName()
                        .equals("Street Smart")));
    }

    @Test
    void searchIsCaseInsensitive() {
        List<Talent> upper =
                registry.search("BLADE");
        List<Talent> lower =
                registry.search("blade");
        assertEquals(upper.size(),
                lower.size());
        assertFalse(upper.isEmpty());
    }

    @Test
    void searchMatchesDescription() {
        // "Researcher" has "Academic knowledge"
        List<Talent> results =
                registry.search("academic");
        assertFalse(results.isEmpty());
        assertTrue(results.stream()
                .anyMatch(t -> t.getName()
                        .equals("Researcher")));
    }

    @Test
    void searchMatchesEffect() {
        // "Field Medic" has "Heal one extra HP"
        List<Talent> results =
                registry.search("extra HP");
        assertFalse(results.isEmpty());
        assertTrue(results.stream()
                .anyMatch(t -> t.getName()
                        .equals("Field Medic")));
    }

    @Test
    void searchNoMatchReturnsEmpty() {
        List<Talent> results =
                registry.search("zzzznonexistent");
        assertTrue(results.isEmpty());
    }

    // =========================================
    // getCategoryCounts
    // =========================================

    @Test
    void categoryCountsSumToTotal() {
        Map<TalentCategory, Integer> counts =
                registry.getCategoryCounts();
        int sum = counts.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        assertEquals(registry.size(), sum);
    }

    @Test
    void categoryCountsCombatIs2() {
        assertEquals(2,
                registry.getCategoryCounts()
                        .get(TalentCategory.COMBAT));
    }

    @Test
    void categoryCountsEquipmentIs0() {
        assertEquals(0,
                registry.getCategoryCounts()
                        .get(TalentCategory.EQUIPMENT));
    }

    @Test
    void categoryCountsHasAllCategories() {
        Map<TalentCategory, Integer> counts =
                registry.getCategoryCounts();
        for (TalentCategory cat
                : TalentCategory.values()) {
            assertTrue(
                    counts.containsKey(cat),
                    "Missing category: " + cat);
        }
    }

    // =========================================
    // getTalentByName
    // =========================================

    @Test
    void getTalentByNameExact() {
        Talent t = registry.getTalentByName(
                "Blademaster");
        assertNotNull(t);
        assertEquals(TalentCategory.COMBAT,
                t.getCategory());
    }

    @Test
    void getTalentByNameCaseInsensitive() {
        assertNotNull(registry.getTalentByName(
                "blademaster"));
    }

    @Test
    void getTalentByNameNonexistentReturnsNull() {
        assertNull(registry.getTalentByName(
                "nonexistent"));
    }

    // =========================================
    // Accessors
    // =========================================

    @Test
    void sizeReturns7() {
        assertEquals(7, registry.size());
    }

    @Test
    void getAllTalentsReturns7() {
        assertEquals(7,
                registry.getAllTalents().size());
    }

    @Test
    void getAllTalentsIsUnmodifiable() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> registry.getAllTalents()
                        .add(talent("Hack", "test",
                                TalentCategory.GENERAL,
                                "test")));
    }

    // =========================================
    // Helper
    // =========================================

    private static Talent talent(
            String name, String description,
            TalentCategory category,
            String effect) {
        return new Talent(name, description,
                category, 3, effect);
    }
}