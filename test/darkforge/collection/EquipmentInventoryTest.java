package darkforge.collection;

import darkforge.model.Equipment;
import darkforge.model.EquipmentWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EquipmentInventoryTest {

    // STR 3 → max weight = 3 + 4 = 7.0
    private EquipmentInventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new EquipmentInventory("TestExplorer", 7);
    }

    // =========================================
    // Helper
    // =========================================

    private Equipment item(String name,
                           EquipmentWeight weight, boolean weapon) {
        return new Equipment(name, "desc",
                weight, 0, weapon);
    }

    private Equipment item(String name,
                           EquipmentWeight weight) {
        return new Equipment(name, "desc", weight);
    }

    // =========================================
    // Max weight from STR + 4
    // =========================================

    @Test
    void maxWeightMatchesStrengthPlusFour() {
        assertEquals(7.0, inventory.getMaxWeight());
    }

    // =========================================
    // REGULAR items (1.0 each)
    // =========================================

    @Test
    void sevenRegularItemsFillInventory() {
        for (int i = 1; i <= 7; i++) {
            assertTrue(inventory.add(
                            item("Regular " + i,
                                    EquipmentWeight.REGULAR)),
                    "Item " + i + " should be accepted");
        }
        assertEquals(7.0, inventory.getCurrentWeight(),
                0.001);
    }

    @Test
    void eighthRegularItemRejected() {
        for (int i = 1; i <= 7; i++) {
            inventory.add(item("Regular " + i,
                    EquipmentWeight.REGULAR));
        }
        assertFalse(inventory.add(
                        item("Regular 8",
                                EquipmentWeight.REGULAR)),
                "8th REGULAR should be rejected at 7.0 max");
        assertEquals(7, inventory.size());
    }

    // =========================================
    // HEAVY + LIGHT mix
    // =========================================

    @Test
    void threeHeavyPlusTwoLightFillInventory() {
        // 3 HEAVY (3 × 2.0 = 6.0) + 2 LIGHT (2 × 0.5 = 1.0) = 7.0
        for (int i = 1; i <= 3; i++) {
            assertTrue(inventory.add(
                    item("Heavy " + i,
                            EquipmentWeight.HEAVY)));
        }
        for (int i = 1; i <= 2; i++) {
            assertTrue(inventory.add(
                    item("Light " + i,
                            EquipmentWeight.LIGHT)));
        }
        assertEquals(7.0, inventory.getCurrentWeight(),
                0.001);
    }

    @Test
    void additionalItemRejectedAfterHeavyLightFull() {
        for (int i = 1; i <= 3; i++) {
            inventory.add(item("Heavy " + i,
                    EquipmentWeight.HEAVY));
        }
        for (int i = 1; i <= 2; i++) {
            inventory.add(item("Light " + i,
                    EquipmentWeight.LIGHT));
        }
        // Even a LIGHT item (0.5) would exceed 7.0
        assertFalse(inventory.add(
                item("Light Extra",
                        EquipmentWeight.LIGHT)));
    }

    // =========================================
    // TINY items (0.0 weight)
    // =========================================

    @Test
    void tinyItemsNeverCauseOverflow() {
        // Fill to max weight first
        for (int i = 1; i <= 7; i++) {
            inventory.add(item("Regular " + i,
                    EquipmentWeight.REGULAR));
        }
        assertEquals(7.0, inventory.getCurrentWeight(),
                0.001);

        // TINY items (0.0) should still be accepted
        for (int i = 1; i <= 10; i++) {
            assertTrue(inventory.add(
                            item("Tiny " + i,
                                    EquipmentWeight.TINY)),
                    "TINY item " + i
                            + " should always be accepted");
        }
    }

    @Test
    void tinyOnlyInventoryWeightIsZero() {
        for (int i = 1; i <= 20; i++) {
            inventory.add(item("Tiny " + i,
                    EquipmentWeight.TINY));
        }
        assertEquals(0.0, inventory.getCurrentWeight(),
                0.001);
    }

    // =========================================
    // isOverEncumbered
    // =========================================

    @Test
    void notOverEncumberedAtCapacity() {
        for (int i = 1; i <= 7; i++) {
            inventory.add(item("Regular " + i,
                    EquipmentWeight.REGULAR));
        }
        // Exactly at max, not over
        assertFalse(inventory.isOverEncumbered());
    }

    @Test
    void notOverEncumberedWhenEmpty() {
        assertFalse(inventory.isOverEncumbered());
    }

    @Test
    void notOverEncumberedBelowCapacity() {
        inventory.add(item("Sword",
                EquipmentWeight.REGULAR));
        assertFalse(inventory.isOverEncumbered());
    }

    // =========================================
    // getWeaponsAtHand — max 3
    // =========================================

    @Test
    void weaponsAtHandReturnsOnlyWeapons() {
        inventory.add(item("Sword",
                EquipmentWeight.LIGHT, true));
        inventory.add(item("Shield",
                EquipmentWeight.LIGHT, false));
        inventory.add(item("Dagger",
                EquipmentWeight.LIGHT, true));

        List<Equipment> weapons =
                inventory.getWeaponsAtHand();
        assertEquals(2, weapons.size());
        assertTrue(weapons.stream().allMatch(
                Equipment::isWeapon));
    }

    @Test
    void weaponsAtHandCapsAtThree() {
        // Add 5 weapons (all TINY so weight isn't an issue)
        for (int i = 1; i <= 5; i++) {
            inventory.add(item("Weapon " + i,
                    EquipmentWeight.TINY, true));
        }
        List<Equipment> weapons =
                inventory.getWeaponsAtHand();
        assertEquals(3, weapons.size(),
                "Max 3 weapons at hand per Ch. 6");
    }

    @Test
    void weaponsAtHandEmptyWhenNoWeapons() {
        inventory.add(item("Compass",
                EquipmentWeight.LIGHT, false));
        inventory.add(item("Rope",
                EquipmentWeight.REGULAR, false));

        assertTrue(inventory.getWeaponsAtHand()
                .isEmpty());
    }

    // =========================================
    // Mixed weight arithmetic
    // =========================================

    @Test
    void mixedWeightsArithmeticCorrect() {
        // TINY(0.0) + LIGHT(0.5) + REGULAR(1.0) + HEAVY(2.0) = 3.5
        inventory.add(item("Trinket",
                EquipmentWeight.TINY));
        inventory.add(item("Lockpick",
                EquipmentWeight.LIGHT));
        inventory.add(item("Medkit",
                EquipmentWeight.REGULAR));
        inventory.add(item("Cannon",
                EquipmentWeight.HEAVY));

        assertEquals(3.5, inventory.getCurrentWeight(),
                0.001);
        assertEquals(4, inventory.size());
        assertFalse(inventory.isOverEncumbered());
    }

    @Test
    void allWeightValuesMatchSpec() {
        assertEquals(0.0,
                EquipmentWeight.TINY.getWeightValue());
        assertEquals(0.5,
                EquipmentWeight.LIGHT.getWeightValue());
        assertEquals(1.0,
                EquipmentWeight.REGULAR.getWeightValue());
        assertEquals(2.0,
                EquipmentWeight.HEAVY.getWeightValue());
    }

    @Test
    void weightDecreasesOnRemove() {
        Equipment heavy = item("Heavy Pack",
                EquipmentWeight.HEAVY);
        inventory.add(heavy);
        inventory.add(item("Light Tool",
                EquipmentWeight.LIGHT));
        assertEquals(2.5, inventory.getCurrentWeight(),
                0.001);

        inventory.remove(heavy);
        assertEquals(0.5, inventory.getCurrentWeight(),
                0.001);
    }

    // =========================================
    // Inherited Inventory<Equipment> behavior
    // =========================================

    @Test
    void findByNameInherited() {
        inventory.add(item("Star Compass",
                EquipmentWeight.LIGHT));
        inventory.add(item("Star Map",
                EquipmentWeight.TINY));
        inventory.add(item("Rope",
                EquipmentWeight.REGULAR));

        List<Equipment> results =
                inventory.findByName("star");
        assertEquals(2, results.size());
    }

    @Test
    void filterInherited() {
        inventory.add(item("Pistol",
                EquipmentWeight.LIGHT, true));
        inventory.add(item("Compass",
                EquipmentWeight.TINY, false));
        inventory.add(item("Rifle",
                EquipmentWeight.REGULAR, true));

        List<Equipment> nonWeapons =
                inventory.filter(e -> !e.isWeapon());
        assertEquals(1, nonWeapons.size());
        assertEquals("Compass",
                nonWeapons.get(0).getName());
    }

    @Test
    void getAllReturnsUnmodifiableView() {
        inventory.add(item("Sword",
                EquipmentWeight.REGULAR));
        List<Equipment> all = inventory.getAll();
        assertThrows(
                UnsupportedOperationException.class,
                () -> all.add(item("Hack",
                        EquipmentWeight.TINY)));
    }

    @Test
    void getByNameInherited() {
        Equipment compass = item("Star Compass",
                EquipmentWeight.LIGHT);
        inventory.add(compass);
        assertEquals(compass,
                inventory.getByName("star compass"));
    }
}