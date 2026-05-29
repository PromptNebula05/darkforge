package darkforge.collection;

import darkforge.model.*;
import darkforge.model.profession.Traveler;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class InventoryHolderTest {

    private Explorer explorer;
    private CharacterItem medkit;
    private Weapon vulcan;

    @BeforeEach
    void setUp() {
        explorer = new Traveler(
                "Kira");
        medkit = new CharacterItem(
                "Medkit", "Kit",
                0.5, 400,
                "Medicine & Drugs",
                TechLevel.ORDINARY,
                false, 2);
        vulcan = new Weapon(
                "Vulcan Pistol", "Sidearm",
                0.5, 400,
                TechLevel.ORDINARY, false,
                EquipmentWeight.LIGHT,
                1, 2, 2,
                Grip.ONE_HANDED, "Short",
                WeaponType.RANGED_PISTOL,
                List.of());
    }

    @Test
    @DisplayName("addItem adds to"
            + " inventory")
    void testAddItem() {
        assertTrue(
                explorer.addItem(medkit));
        assertEquals(1,
                explorer.getAllItems()
                        .size());
    }

    @Test
    @DisplayName("removeItem removes"
            + " from inventory")
    void testRemoveItem() {
        explorer.addItem(medkit);
        assertTrue(
                explorer.removeItem(medkit));
        assertEquals(0,
                explorer.getAllItems()
                        .size());
    }

    @Test
    @DisplayName("searchItems filters"
            + " with predicate")
    void testSearchItems() {
        explorer.addItem(medkit);
        explorer.addItem(vulcan);
        List<CharacterItem> found =
                explorer.searchItems(i ->
                        i.getCost() == 400);
        assertEquals(2, found.size());
    }
}
