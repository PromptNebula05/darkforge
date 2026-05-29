package darkforge.model;

import darkforge.crew.VehicleType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api
        .Assertions.*;

class VehicleModuleTest {

    @Test
    @DisplayName("Module stores CP cost"
            + " and slot cost")
    void testModuleFields() {
        VehicleModule vm =
                new VehicleModule(
                        "Armor Plating I",
                        "Hull reinforcement",
                        1, 2, "armor",
                        "Armor +1.",
                        TechLevel.ORDINARY,
                        false, null, false);
        assertEquals(1,
                vm.getSlotCost());
        assertEquals(2,
                vm.getCpCost());
        assertEquals("armor",
                vm.getModuleType());
    }

    @Test
    @DisplayName("Shuttle module not"
            + " compatible with rovers")
    void testShuttleCompatibility() {
        VehicleModule shuttle =
                new VehicleModule(
                        "Composite Plating",
                        "Shuttle armor",
                        1, 3, "armor",
                        "Armor +1.",
                        TechLevel.GUILD,
                        false, null, true);
        assertTrue(
                shuttle.isShuttleUpgrade());
    }
}
