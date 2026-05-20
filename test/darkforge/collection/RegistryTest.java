package darkforge.collection;

import darkforge.model.Equipment;
import darkforge.model.EquipmentWeight;
import darkforge.model.Talent;
import darkforge.model.TalentCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RegistryTest {

    private Registry<String, Equipment> registry;

    private Equipment sword;
    private Equipment shield;
    private Equipment compass;
    private Equipment rope;
    private Equipment lantern;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        registry = new Registry<>();
        sword = new Equipment("Dura Blade",
                "Powered heirloom blade",
                EquipmentWeight.REGULAR, 4, true);
        shield = new Equipment("Expandable Shield",
                "Block and dodge only",
                EquipmentWeight.HEAVY, 4);
        compass = new Equipment("Astro-Compass",
                "Navigate in flat space",
                EquipmentWeight.LIGHT, 2);
        rope = new Equipment("Climbing Kit",
                "Rope, pick, and hammer",
                EquipmentWeight.REGULAR, 2);
        lantern = new Equipment("Blight Lantern",
                "Illumination device",
                EquipmentWeight.LIGHT, 1);

        // 5 items across 3 keys
        registry.register("Weapons", sword);
        registry.register("Defense", shield);
        registry.register("Tools", compass);
        registry.register("Tools", rope);
        registry.register("Tools", lantern);
    }

    // =========================================
    // getByKey — correct items per key
    // =========================================

    @Test
    void getByKeyReturnsCorrectItems() {
        List<Equipment> weapons =
                registry.getByKey("Weapons");
        assertEquals(1, weapons.size());
        assertTrue(weapons.contains(sword));
    }

    @Test
    void getByKeyReturnsMultipleItems() {
        List<Equipment> tools =
                registry.getByKey("Tools");
        assertEquals(3, tools.size());
        assertTrue(tools.contains(compass));
        assertTrue(tools.contains(rope));
        assertTrue(tools.contains(lantern));
    }

    @Test
    void getByKeyDefenseReturnsShield() {
        List<Equipment> defense =
                registry.getByKey("Defense");
        assertEquals(1, defense.size());
        assertTrue(defense.contains(shield));
    }

    // =========================================
    // getByKey — absent key returns empty
    // =========================================

    @Test
    void getByKeyAbsentKeyReturnsEmptyList() {
        List<Equipment> result =
                registry.getByKey("Nonexistent");
        assertNotNull(result,
                "Absent key must return empty list,"
                        + " not null");
        assertTrue(result.isEmpty());
    }

    // =========================================
    // getByKey — unmodifiable
    // =========================================

    @Test
    void getByKeyReturnsUnmodifiableList() {
        List<Equipment> weapons =
                registry.getByKey("Weapons");
        assertThrows(
                UnsupportedOperationException.class,
                () -> weapons.add(shield));
    }

    // =========================================
    // getAll — flattened
    // =========================================

    @Test
    void getAllReturnsAllFiveItems() {
        List<Equipment> all = registry.getAll();
        assertEquals(5, all.size());
        assertTrue(all.contains(sword));
        assertTrue(all.contains(shield));
        assertTrue(all.contains(compass));
        assertTrue(all.contains(rope));
        assertTrue(all.contains(lantern));
    }

    // =========================================
    // getAll(predicate) — filtered
    // =========================================

    @Test
    void getAllFilteredReturnsCorrectSubset() {
        List<Equipment> weapons =
                registry.getAll(
                        Equipment::isWeapon);
        assertEquals(1, weapons.size());
        assertTrue(weapons.contains(sword));
    }

    @Test
    void getAllFilteredByWeightAcrossKeys() {
        List<Equipment> lightItems =
                registry.getAll(
                        e -> e.getWeight()
                                == EquipmentWeight.LIGHT);
        assertEquals(2, lightItems.size());
        assertTrue(lightItems.contains(compass));
        assertTrue(lightItems.contains(lantern));
    }

    @Test
    void getAllFilteredNoMatchReturnsEmpty() {
        List<Equipment> heavy =
                registry.getAll(
                        e -> e.getWeight()
                                == EquipmentWeight.TINY);
        assertTrue(heavy.isEmpty());
    }

    // =========================================
    // keys
    // =========================================

    @Test
    void keysReturnsAllThreeKeys() {
        Set<String> keys = registry.keys();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("Weapons"));
        assertTrue(keys.contains("Defense"));
        assertTrue(keys.contains("Tools"));
    }

    @Test
    void keysIsUnmodifiable() {
        Set<String> keys = registry.keys();
        assertThrows(
                UnsupportedOperationException.class,
                () -> keys.add("Hack"));
    }

    // =========================================
    // countByKey
    // =========================================

    @Test
    void countByKeyMatchesListSizes() {
        assertEquals(1,
                registry.countByKey("Weapons"));
        assertEquals(1,
                registry.countByKey("Defense"));
        assertEquals(3,
                registry.countByKey("Tools"));
    }

    @Test
    void countByKeyAbsentKeyReturnsZero() {
        assertEquals(0,
                registry.countByKey("Nonexistent"));
    }

    // =========================================
    // totalCount
    // =========================================

    @Test
    void totalCountMatchesAllItems() {
        assertEquals(5, registry.totalCount());
    }

    // =========================================
    // Empty registry
    // =========================================

    @Test
    void emptyRegistryTotalCountIsZero() {
        Registry<String, Equipment> empty =
                new Registry<>();
        assertEquals(0, empty.totalCount());
    }

    @Test
    void emptyRegistryKeysIsEmpty() {
        Registry<String, Equipment> empty =
                new Registry<>();
        assertTrue(empty.keys().isEmpty());
    }

    @Test
    void emptyRegistryGetAllReturnsEmpty() {
        Registry<String, Equipment> empty =
                new Registry<>();
        assertTrue(empty.getAll().isEmpty());
    }

    // =========================================
    // Multiple generic instantiations
    // =========================================

    @Test
    void registryWithEnumKeyAndTalentValue() {
        // Demonstrates Registry<TalentCategory, Talent>
        // in the same test class
        Registry<TalentCategory, Talent> talentReg =
                new Registry<>();

        Talent combat1 = new Talent(
                "Sharpshooter", "Precise aim",
                TalentCategory.COMBAT, 3,
                "+1 base die when firing a long barreled gun");
        Talent combat2 = new Talent(
                "Quick Draw", "Fast weapon switch",
                TalentCategory.COMBAT, 2,
                "+1 base die for fast weapon switching");
        Talent knowledge1 = new Talent(
                "Smart", "Quick learner",
                TalentCategory.KNOWLEDGE, 3,
                "You can push any roll based on Logic twice");

        talentReg.register(
                TalentCategory.COMBAT, combat1);
        talentReg.register(
                TalentCategory.COMBAT, combat2);
        talentReg.register(
                TalentCategory.KNOWLEDGE, knowledge1);

        assertEquals(2, talentReg.countByKey(
                TalentCategory.COMBAT));
        assertEquals(1, talentReg.countByKey(
                TalentCategory.KNOWLEDGE));
        assertEquals(3, talentReg.totalCount());

        List<Talent> combatTalents =
                talentReg.getByKey(
                        TalentCategory.COMBAT);
        assertEquals(2, combatTalents.size());
        assertTrue(combatTalents.contains(
                combat1));
        assertTrue(combatTalents.contains(
                combat2));
    }

    @Test
    void registryWithEquipmentWeightKey() {
        // Demonstrates Registry<EquipmentWeight, Equipment>
        Registry<EquipmentWeight, Equipment>
                byWeight = new Registry<>();

        byWeight.register(
                EquipmentWeight.REGULAR, sword);
        byWeight.register(
                EquipmentWeight.REGULAR, rope);
        byWeight.register(
                EquipmentWeight.HEAVY, shield);
        byWeight.register(
                EquipmentWeight.LIGHT, compass);
        byWeight.register(
                EquipmentWeight.LIGHT, lantern);

        assertEquals(2, byWeight.countByKey(
                EquipmentWeight.REGULAR));
        assertEquals(1, byWeight.countByKey(
                EquipmentWeight.HEAVY));
        assertEquals(2, byWeight.countByKey(
                EquipmentWeight.LIGHT));
        assertEquals(0, byWeight.countByKey(
                EquipmentWeight.TINY));
        assertEquals(5, byWeight.totalCount());
    }
}