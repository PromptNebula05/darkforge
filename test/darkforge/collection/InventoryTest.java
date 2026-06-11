package darkforge.collection;

import darkforge.model.Equipment;
import darkforge.model.EquipmentWeight;
import darkforge.model.GameEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory<Equipment> inventory;

    // Reusable test items
    private Equipment sword;
    private Equipment shield;
    private Equipment compass;
    private Equipment rope;

    @BeforeEach
    void setUp() {
        inventory = new Inventory<>("TestOwner", 3);
        sword = new Equipment("Dura Blade",
                "Powered heirloom blade", EquipmentWeight.REGULAR, 4, true);
        shield = new Equipment("Expandable Shield",
                "Block and dodge only", EquipmentWeight.HEAVY, 4);
        compass = new Equipment("Astro-Compass",
                "Navigate in flat space", EquipmentWeight.LIGHT, 2);
        rope = new Equipment("Climbing Kit, Basic",
                "Rope, pick, and hammer", EquipmentWeight.REGULAR, 2);
    }

    // =========================================
    // Capacity enforcement
    // =========================================

    @Test
    void addItemsUpToCapacity() {
        assertTrue(inventory.add(sword));
        assertTrue(inventory.add(shield));
        assertTrue(inventory.add(compass));
        assertEquals(3, inventory.size());
    }

    @Test
    void addReturnsFalseWhenFull() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(compass);
        assertFalse(inventory.add(rope),
                "add() should return false when at capacity");
        assertEquals(3, inventory.size());
    }

    // =========================================
    // remainingCapacity
    // =========================================

    @Test
    void remainingCapacityTracksCorrectly() {
        assertEquals(3, inventory.remainingCapacity());
        inventory.add(sword);
        assertEquals(2, inventory.remainingCapacity());
        inventory.add(shield);
        assertEquals(1, inventory.remainingCapacity());
        inventory.add(compass);
        assertEquals(0, inventory.remainingCapacity());
    }

    @Test
    void remainingCapacityIncreasesOnRemove() {
        inventory.add(sword);
        inventory.add(shield);
        assertEquals(1, inventory.remainingCapacity());
        inventory.remove(sword);
        assertEquals(2, inventory.remainingCapacity());
    }

    // =========================================
    // getByName — case-insensitive
    // =========================================

    @Test
    void getByNameExactMatch() {
        inventory.add(sword);
        assertEquals(sword,
                inventory.getByName("Dura Blade"));
    }

    @Test
    void getByNameCaseInsensitive() {
        inventory.add(sword);
        assertEquals(sword,
                inventory.getByName("dura blade"));
        assertEquals(sword,
                inventory.getByName("DURA BLADE"));
    }

    @Test
    void getByNameReturnsNullForNoMatch() {
        inventory.add(sword);
        assertNull(inventory.getByName("Nonexistent Item"));
    }

    // =========================================
    // findByName — partial match
    // =========================================

    @Test
    void findByNamePartialMatchReturnsMultiple() {
        // "Blade" and "Shield" won't match, but
        // items with shared substring will.
        Equipment navCompass = new Equipment("Compass",
                "Surface navigation tool", EquipmentWeight.TINY, 1);
        inventory = new Inventory<>("TestOwner", 5);
        inventory.add(compass);      // "Astro-Compass"
        inventory.add(navCompass);   // "Compass"
        inventory.add(sword);        // "Dura Blade"

        List<Equipment> results =
                inventory.findByName("compass");
        assertEquals(2, results.size());
        assertTrue(results.contains(compass));
        assertTrue(results.contains(navCompass));
    }

    @Test
    void findByNameNoMatchReturnsEmptyList() {
        inventory.add(sword);
        List<Equipment> results =
                inventory.findByName("zzz");
        assertTrue(results.isEmpty());
    }

    // =========================================
    // getAll — unmodifiable view
    // =========================================

    @Test
    void getAllReturnsAllItems() {
        inventory.add(sword);
        inventory.add(shield);
        List<Equipment> all = inventory.getAll();
        assertEquals(2, all.size());
        assertTrue(all.contains(sword));
        assertTrue(all.contains(shield));
    }

    @Test
    void getAllReturnsUnmodifiableView() {
        inventory.add(sword);
        List<Equipment> all = inventory.getAll();
        assertThrows(UnsupportedOperationException.class,
                () -> all.add(shield));
    }

    // =========================================
    // filter — predicate
    // =========================================

    @Test
    void filterReturnsCorrectSubset() {
        inventory = new Inventory<>("TestOwner", 5);
        inventory.add(sword);    // weapon
        inventory.add(shield);   // not weapon
        inventory.add(compass);  // not weapon

        List<Equipment> weapons =
                inventory.filter(Equipment::isWeapon);
        assertEquals(1, weapons.size());
        assertTrue(weapons.contains(sword));
    }

    @Test
    void filterWithNoMatchReturnsEmptyList() {
        inventory.add(shield);
        inventory.add(compass);

        List<Equipment> weapons =
                inventory.filter(Equipment::isWeapon);
        assertTrue(weapons.isEmpty());
    }

    // =========================================
    // sort — comparator reorders backing list
    // =========================================

    @Test
    void sortReordersBackingList() {
        inventory = new Inventory<>("TestOwner", 5);
        inventory.add(shield);   // "Expandable Shield"
        inventory.add(sword);    // "Dura Blade"
        inventory.add(compass);  // "Astro-Compass"

        // Sort alphabetically by name
        inventory.sort(Comparator.comparing(
                GameEntity::getName));

        List<Equipment> sorted = inventory.getAll();
        assertEquals("Astro-Compass",
                sorted.get(0).getName());
        assertEquals("Dura Blade",
                sorted.get(1).getName());
        assertEquals("Expandable Shield",
                sorted.get(2).getName());
    }

    // =========================================
    // addAll — wildcard bound
    // =========================================

    @Test
    void addAllWithWildcardBound() {
        // Inventory<GameEntity> accepts List<Equipment>
        // via Collection<? extends T>
        Inventory<GameEntity> entityInventory =
                new Inventory<>("Crew", 10);

        List<Equipment> gear =
                List.of(sword, shield, compass);
        entityInventory.addAll(gear);

        assertEquals(3, entityInventory.size());
        assertEquals(sword,
                entityInventory.getByName("Dura Blade"));
    }

    @Test
    void addAllRespectsCapacity() {
        inventory.add(sword);  // 1 of 3

        List<Equipment> extras =
                List.of(shield, compass, rope);
        inventory.addAll(extras);

        // Only 2 more should fit (capacity 3)
        assertEquals(3, inventory.size());
        assertTrue(inventory.contains(shield));
        assertTrue(inventory.contains(compass));
        assertFalse(inventory.contains(rope));
    }

    // =========================================
    // map — transform elements
    // =========================================

    @Test
    void mapTransformsElementsCorrectly() {
        inventory.add(sword);
        inventory.add(shield);
        inventory.add(compass);

        List<String> names =
                inventory.map(GameEntity::getName);

        assertEquals(3, names.size());
        assertTrue(names.contains("Dura Blade"));
        assertTrue(names.contains("Expandable Shield"));
        assertTrue(names.contains("Astro-Compass"));
    }

    // =========================================
    // Iterator — enhanced for-loop
    // =========================================

    @Test
    void iteratorWorksInEnhancedForLoop() {
        inventory.add(sword);
        inventory.add(shield);

        List<Equipment> collected = new ArrayList<>();
        for (Equipment item : inventory) {
            collected.add(item);
        }

        assertEquals(2, collected.size());
        assertTrue(collected.contains(sword));
        assertTrue(collected.contains(shield));
    }

    @Test
    void iteratorIsUnmodifiable() {
        inventory.add(sword);
        Iterator<Equipment> it = inventory.iterator();
        it.next();
        assertThrows(UnsupportedOperationException.class,
                it::remove);
    }

    // =========================================
    // Empty inventory edge cases
    // =========================================

    @Test
    void emptyInventoryIsEmpty() {
        assertTrue(inventory.isEmpty());
    }

    @Test
    void emptyInventorySizeIsZero() {
        assertEquals(0, inventory.size());
    }

    @Test
    void emptyInventoryFindByNameReturnsEmptyList() {
        List<Equipment> results =
                inventory.findByName("anything");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void emptyInventoryGetByNameReturnsNull() {
        assertNull(inventory.getByName("anything"));
    }

    @Test
    void emptyInventoryFilterReturnsEmptyList() {
        assertTrue(inventory.filter(
                e -> true).isEmpty());
    }

    // =========================================
    // Unlimited capacity (-1)
    // =========================================

    @Test
    void unlimitedCapacityNeverRejects() {
        Inventory<Equipment> unlimited =
                new Inventory<>("Unlimited", -1);

        for (int i = 0; i < 100; i++) {
            assertTrue(unlimited.add(
                    new Equipment("Item " + i,
                            "desc", EquipmentWeight.TINY)));
        }
        assertEquals(100, unlimited.size());
    }

    @Test
    void unlimitedCapacityRemainingIsMaxValue() {
        Inventory<Equipment> unlimited =
                new Inventory<>("Unlimited", -1);
        assertEquals(Integer.MAX_VALUE,
                unlimited.remainingCapacity());
    }

    // =========================================
    // remove
    // =========================================

    @Test
    void removeExistingItemReturnsTrue() {
        inventory.add(sword);
        assertTrue(inventory.remove(sword));
        assertEquals(0, inventory.size());
    }

    @Test
    void removeNonexistentItemReturnsFalse() {
        inventory.add(sword);
        assertFalse(inventory.remove(shield));
    }

    // =========================================
    // display
    // =========================================

    @Test
    void displayEmptyInventory() {
        String result = inventory.display();
        assertTrue(result.contains("empty"));
    }

    @Test
    void displayShowsSizeAndCapacity() {
        inventory.add(sword);
        inventory.add(shield);
        String result = inventory.display();
        assertTrue(result.contains("2/3"));
        assertTrue(result.contains("TestOwner"));
    }
}