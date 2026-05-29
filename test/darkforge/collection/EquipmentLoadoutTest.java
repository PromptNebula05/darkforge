package darkforge.collection;

import darkforge.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api
        .Assertions.*;

class EquipmentLoadoutTest {

    @Test
    @DisplayName("Loadout enforces max")
    void testMaxSlots() {
        EquipmentLoadout<VehicleModule>
                loadout =
                new EquipmentLoadout<>(
                        "Test", 2);
        VehicleModule m1 =
                new VehicleModule(
                        "M1", "Mod",
                        1, 2, "armor",
                        "Armor +1",
                        TechLevel.ORDINARY,
                        false, null, false);
        VehicleModule m2 =
                new VehicleModule(
                        "M2", "Mod",
                        1, 3, "sensor",
                        "+1 sensor",
                        TechLevel.GUILD,
                        false, null, false);
        VehicleModule m3 =
                new VehicleModule(
                        "M3", "Mod",
                        1, 4, "utility",
                        "Extra",
                        TechLevel.GUILD,
                        false, null, false);
        assertTrue(loadout.equip(m1));
        assertTrue(loadout.equip(m2));
        assertFalse(loadout.equip(m3));
        assertTrue(loadout.isFull());
    }

    @Test
    @DisplayName("Negative max throws")
    void testNegativeMax() {
        assertThrows(
                IllegalArgumentException
                        .class,
                () -> new EquipmentLoadout<>(
                        "Bad", -1));
    }
}
