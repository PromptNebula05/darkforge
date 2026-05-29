package darkforge.model;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class WeaponTest {

    private Weapon vulcanRifle;

    @BeforeEach
    void setUp() {
        vulcanRifle = new Weapon(
                "Vulcan Rifle",
                "Standard rifle",
                1.0, 700,
                TechLevel.ORDINARY,
                false,
                EquipmentWeight.REGULAR,
                1, 3, 2,
                Grip.TWO_HANDED,
                "Medium-Long",
                WeaponType.RANGED_RIFLE,
                List.of());
    }

    @Test
    @DisplayName("Weapon damage and"
            + " crit accessible")
    void testWeaponStats() {
        assertEquals(3,
                vulcanRifle.getDamage());
        assertEquals(2,
                vulcanRifle
                        .getCritThreshold());
        assertEquals(
                Grip.TWO_HANDED,
                vulcanRifle.getGrip());
    }

    @Test
    @DisplayName("Weapon item type"
            + " returns 'weapon'")
    void testItemType() {
        assertEquals("weapon",
                vulcanRifle.getItemType());
    }

    @Test
    @DisplayName("Weapon features list"
            + " immutable")
    void testFeaturesImmutable() {
        Weapon autofire = new Weapon(
                "Fusillard", "Burst",
                1.0, 2000,
                TechLevel.GUILD, false,
                EquipmentWeight.REGULAR,
                2, 2, 2,
                Grip.TWO_HANDED,
                "Short-Medium",
                WeaponType.RANGED_RIFLE,
                List.of("Autofire"));
        assertEquals(1,
                autofire.getFeatures()
                        .size());
        assertEquals("Autofire",
                autofire.getFeatures()
                        .get(0));
    }
}