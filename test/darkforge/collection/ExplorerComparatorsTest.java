package darkforge.collection;

import darkforge.crew.CrewRole;
import darkforge.model.Attribute;
import darkforge.model.Equipment;
import darkforge.model.Explorer;
import darkforge.model.Specialty;
import darkforge.model.Talent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExplorerComparatorsTest {

    private Explorer highStr;
    private Explorer highAgl;
    private Explorer highLog;
    private Explorer balanced;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        // STR 5, AGL 2, LOG 2, PER 2, INS 2, EMP 2
        highStr = createExplorer("Tarek",
                "Enforcer", 5, 2, 2, 2, 2, 2);
        // STR 2, AGL 5, LOG 2, PER 2, INS 4, EMP 2
        highAgl = createExplorer("Kiran",
                "Roughneck", 2, 5, 2, 2, 4, 2);
        // STR 2, AGL 2, LOG 5, PER 3, INS 2, EMP 3
        highLog = createExplorer("Nala",
                "Scholar", 2, 2, 5, 3, 2, 3);
        // STR 3, AGL 3, LOG 3, PER 3, INS 3, EMP 3
        balanced = createExplorer("Sera",
                "Traveler", 3, 3, 3, 3, 3, 3);
    }

    // =========================================
    // byAttribute — descending
    // =========================================

    @Test
    void byStrengthSortsDescending() {
        List<Explorer> list = asList(
                balanced, highStr, highAgl, highLog);
        list.sort(ExplorerComparators
                .byAttribute(Attribute.STRENGTH));

        assertEquals("Tarek",
                list.get(0).getName());
        assertEquals("Sera",
                list.get(1).getName());
    }

    @Test
    void byAgilityPlacesHighAglFirst() {
        List<Explorer> list = asList(
                highStr, highLog, highAgl, balanced);
        list.sort(ExplorerComparators
                .byAttribute(Attribute.AGILITY));

        assertEquals("Kiran",
                list.get(0).getName());
    }

    // =========================================
    // byRoleFitness — descending
    // =========================================

    @Test
    void byDelverFitnessSortsCorrectly() {
        // DELVER: AGL + INS
        // highAgl: 5+4=9, balanced: 3+3=6,
        // highStr: 2+2=4, highLog: 2+2=4
        List<Explorer> list = asList(
                highStr, balanced, highLog, highAgl);
        list.sort(ExplorerComparators
                .byRoleFitness(CrewRole.DELVER));

        assertEquals("Kiran",
                list.get(0).getName(),
                "highAgl (AGL 5 + INS 4 = 9)"
                        + " should rank first for DELVER");
        assertEquals("Sera",
                list.get(1).getName(),
                "balanced (AGL 3 + INS 3 = 6)"
                        + " should rank second");
    }

    @Test
    void byScoutFitnessSortsCorrectly() {
        // SCOUT: PER + LOG
        // highLog: 3+5=8, balanced: 3+3=6,
        // highStr: 2+2=4, highAgl: 2+2=4
        List<Explorer> list = asList(
                highStr, highAgl, balanced, highLog);
        list.sort(ExplorerComparators
                .byRoleFitness(CrewRole.SCOUT));

        assertEquals("Nala",
                list.get(0).getName());
    }

    @Test
    void byBurrowerFitnessSortsCorrectly() {
        // BURROWER: STR + LOG
        // highStr: 5+2=7, highLog: 2+5=7,
        // balanced: 3+3=6, highAgl: 2+2=4
        List<Explorer> list = asList(
                highAgl, balanced, highStr, highLog);
        list.sort(ExplorerComparators
                .byRoleFitness(CrewRole.BURROWER));

        // highStr and highLog tied at 7
        int firstFitness = CrewRole.BURROWER
                .getRoleFitness(list.get(0));
        assertEquals(7, firstFitness);
    }

    // =========================================
    // byProfession — alphabetical
    // =========================================

    @Test
    void byProfessionSortsAlphabetically() {
        List<Explorer> list = asList(
                highAgl, highLog, highStr, balanced);
        list.sort(ExplorerComparators
                .byProfession());

        List<String> professions = list.stream()
                .map(Explorer::getProfessionName)
                .collect(Collectors.toList());

        assertEquals(
                List.of("Enforcer", "Roughneck",
                        "Scholar", "Traveler"),
                professions);
    }

    // =========================================
    // byName — alphabetical
    // =========================================

    @Test
    void byNameSortsAlphabetically() {
        List<Explorer> list = asList(
                balanced, highStr, highAgl, highLog);
        list.sort(ExplorerComparators.byName());

        assertEquals("Kiran",
                list.get(0).getName());
        assertEquals("Nala",
                list.get(1).getName());
        assertEquals("Sera",
                list.get(2).getName());
        assertEquals("Tarek",
                list.get(3).getName());
    }

    // =========================================
    // Derived stats — descending
    // =========================================

    @Test
    void byHealthSortsDescending() {
        // Health = STR + AGL
        // highStr: 5+2=7, highAgl: 2+5=7,
        // balanced: 3+3=6, highLog: 2+2=4
        List<Explorer> list = asList(
                highLog, balanced, highStr, highAgl);
        list.sort(ExplorerComparators.byHealth());

        int topHealth = list.get(0).getHealth();
        assertEquals(7, topHealth);
        assertEquals(4,
                list.get(3).getHealth());
    }

    @Test
    void byHopeSortsDescending() {
        // Hope = LOG + EMP
        // highLog: 5+3=8, balanced: 3+3=6,
        // highStr: 2+2=4, highAgl: 2+2=4
        List<Explorer> list = asList(
                highStr, highLog, balanced, highAgl);
        list.sort(ExplorerComparators.byHope());

        assertEquals("Nala",
                list.get(0).getName());
    }

    @Test
    void byHeartSortsDescending() {
        // Heart = INS + PER
        // highAgl: 4+2=6, balanced: 3+3=6,
        // highLog: 2+3=5, highStr: 2+2=4
        List<Explorer> list = asList(
                highStr, highLog, highAgl, balanced);
        list.sort(ExplorerComparators.byHeart());

        int topHeart = list.get(0).getHeart();
        assertEquals(6, topHeart);
    }

    // =========================================
    // Composition via thenComparing
    // =========================================

    @Test
    void byProfessionThenByName() {
        // Add a second Enforcer to test
        // secondary sort
        Explorer enforcer2 = createExplorer(
                "Amos", "Enforcer",
                4, 3, 2, 2, 2, 2);

        List<Explorer> list = asList(
                highStr, enforcer2, highLog,
                balanced, highAgl);
        list.sort(ExplorerComparators
                .byProfession()
                .thenComparing(
                        ExplorerComparators.byName()));

        // Enforcers first (alphabetical)
        assertEquals("Amos",
                list.get(0).getName());
        assertEquals("Tarek",
                list.get(1).getName());
        // Then Roughneck, Scholar, Traveler
        assertEquals("Kiran",
                list.get(2).getName());
        assertEquals("Nala",
                list.get(3).getName());
        assertEquals("Sera",
                list.get(4).getName());
    }

    @Test
    void byHealthThenByName() {
        // highStr and highAgl both have
        // Health 7 — tiebreak by name
        List<Explorer> list = asList(
                highStr, highAgl, balanced, highLog);
        list.sort(ExplorerComparators
                .byHealth()
                .thenComparing(
                        ExplorerComparators.byName()));

        // Both Health 7, Kiran < Tarek
        assertEquals("Kiran",
                list.get(0).getName());
        assertEquals("Tarek",
                list.get(1).getName());
    }

    // =========================================
    // Single explorer — trivial sort
    // =========================================

    @Test
    void singleExplorerSortIsTrivial() {
        List<Explorer> list = asList(highStr);
        list.sort(ExplorerComparators
                .byAttribute(Attribute.STRENGTH));
        assertEquals(1, list.size());
        assertEquals("Tarek",
                list.get(0).getName());
    }

    // =========================================
    // Equal values — stable ordering
    // =========================================

    @Test
    void equalAttributeValuesPreserveOrder() {
        // All have LOG 3
        Explorer a = createExplorer("Alpha",
                "Enforcer", 3, 3, 3, 3, 3, 3);
        Explorer b = createExplorer("Beta",
                "Scholar", 3, 3, 3, 3, 3, 3);
        Explorer c = createExplorer("Gamma",
                "Traveler", 3, 3, 3, 3, 3, 3);

        List<Explorer> list = asList(a, b, c);
        list.sort(ExplorerComparators
                .byAttribute(Attribute.LOGIC));

        // All tied — stable sort preserves
        // insertion order
        assertEquals("Alpha",
                list.get(0).getName());
        assertEquals("Beta",
                list.get(1).getName());
        assertEquals("Gamma",
                list.get(2).getName());
    }

    // =========================================
    // Helpers
    // =========================================

    private List<Explorer> asList(
            Explorer... explorers) {
        return new ArrayList<>(
                Arrays.asList(explorers));
    }

    private Explorer createExplorer(
            String name, String profession,
            int str, int agl, int log,
            int per, int ins, int emp) {
        Explorer explorer = new Explorer(
                name, "Test " + profession) {
            public Attribute getKeyAttribute() {
                return Attribute.AGILITY;
            }
            public List<Talent> getKeyTalents() {
                return List.of();
            }
            public List<List<Equipment>>
            getStartingEquipmentSets() {
                return List.of();
            }
            public String getProfessionName() {
                return profession;
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
}