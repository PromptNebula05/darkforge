package darkforge.collection;

import darkforge.model.*;
import darkforge.model.profession.Enforcer;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class EquippableTest {

    private Explorer explorer;
    private Weapon pistol;

    @BeforeEach
    void setUp() {
        explorer = new Enforcer(
                "Zara");
        pistol = new Weapon(
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
    @DisplayName("Cannot equip weapon"
            + " not in inventory")
    void testEquipRequiresInventory() {
        assertFalse(
                explorer.equip(pistol));
    }

    @Test
    @DisplayName("Equip weapon after"
            + " adding to inventory")
    void testEquipAfterAdd() {
        explorer.addItem(pistol);
        assertTrue(
                explorer.equip(pistol));
        assertTrue(
                explorer.isEquipped(pistol));
    }

    @Test
    @DisplayName("Unequip weapon")
    void testUnequip() {
        explorer.addItem(pistol);
        explorer.equip(pistol);
        assertTrue(
                explorer.unequip(pistol));
        assertFalse(
                explorer.isEquipped(pistol));
    }

    @Test
    @DisplayName("Max 3 weapons equipped")
    void testMaxWeaponSlots() {
        for (int i = 0; i < 4; i++) {
            Weapon w = new Weapon(
                    "W" + i, "Test",
                    0.5, 100,
                    TechLevel.ORDINARY,
                    false,
                    EquipmentWeight.LIGHT,
                    0, 1, 3,
                    Grip.ONE_HANDED,
                    "Short",
                    WeaponType.RANGED_PISTOL,
                    List.of());
            explorer.addItem(w);
            if (i < 3) {
                assertTrue(
                        explorer.equip(w));
            } else {
                assertFalse(
                        explorer.equip(w));
            }
        }
        assertEquals(3,
                explorer.getEquipped()
                        .size());
    }
}
