package darkforge.crew;

import darkforge.model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api
        .Assertions.*;

class VehicleInventoryTest {

    private Vehicle rhino;

    @BeforeEach
    void setUp() {
        rhino = Vehicle.createRhino(
                "Desert Runner",
                "Sandstorm");
    }

    @Test
    @DisplayName("Vehicle adds cargo")
    void testAddCargo() {
        CargoItem fuel = new CargoItem(
                "Fuel", "Extra fuel",
                100, "supplies",
                800, TechLevel.ORDINARY);
        assertTrue(
                rhino.addItem(fuel));
        assertEquals(1,
                rhino.getAllItems().size());
    }

    @Test
    @DisplayName("Vehicle equips module")
    void testEquipModule() {
        VehicleModule m =
                new VehicleModule(
                        "Armor Plating I",
                        "Hull armor",
                        1, 2, "armor",
                        "Armor +1.",
                        TechLevel.ORDINARY,
                        false, null, false);
        assertTrue(rhino.equip(m));
        assertEquals(1,
                rhino.getEquipped().size());
    }

    @Test
    @DisplayName("Display shows stats")
    void testDisplay() {
        String display =
                rhino.display();
        assertTrue(display.contains(
                "Desert Runner"));
        assertTrue(display.contains(
                "Hull"));
    }
}
