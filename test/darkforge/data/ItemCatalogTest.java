package darkforge.data;

import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class ItemCatalogTest {

    private ItemCatalog catalog;

    @BeforeEach
    void setUp() {
        catalog = new ItemCatalog(List.of(
                new CharacterItem(
                        "Medkit", "Kit",
                        0.5, 400,
                        "Medicine & Drugs",
                        TechLevel.ORDINARY,
                        false, 2),
                new Weapon(
                        "Vulcan Rifle", "Rifle",
                        1.0, 700,
                        TechLevel.ORDINARY,
                        false,
                        EquipmentWeight.REGULAR,
                        1, 3, 2,
                        Grip.TWO_HANDED,
                        "Medium-Long",
                        WeaponType.RANGED_RIFLE,
                        List.of())));
    }

    @Test
    @DisplayName("Catalog search by name")
    void testSearch() {
        assertEquals(1,
                catalog.search("vulcan")
                        .size());
    }

    @Test
    @DisplayName("Filter by type Weapon")
    void testFilterByType() {
        List<Weapon> weapons =
                catalog.filterByType(
                        Weapon.class);
        assertEquals(1,
                weapons.size());
    }

    @Test
    @DisplayName("Category stats")
    void testCategoryStats() {
        var stats =
                catalog.getCategoryStats();
        assertTrue(
                stats.containsKey(
                        "Medicine & Drugs"));
    }

    @Test
    @DisplayName("Cost range filter")
    void testCostRange() {
        assertEquals(1,
                catalog.filterByCostRange(
                        500, 1000).size());
    }
}
