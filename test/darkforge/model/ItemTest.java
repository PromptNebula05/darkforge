package darkforge.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api
        .Assertions.*;

class ItemTest {

    @Test
    @DisplayName("CharacterItem stores"
            + " name, cost, weight, tech")
    void testCharacterItemFields() {
        CharacterItem ci =
                new CharacterItem(
                        "Medkit", "Field kit",
                        0.5, 400,
                        "Medicine & Drugs",
                        TechLevel.ORDINARY,
                        false, 2);
        assertEquals("Medkit",
                ci.getName());
        assertEquals(400,
                ci.getCost());
        assertEquals(
                EquipmentWeight.LIGHT,
                ci.getWeightClass());
        assertEquals(
                TechLevel.ORDINARY,
                ci.getTechLevel());
        assertFalse(ci.isRestricted());
    }

    @Test
    @DisplayName("CargoItem stores"
            + " supply points")
    void testCargoItemFields() {
        CargoItem ci = new CargoItem(
                "Fuel", "Extra fuel",
                400, "supplies",
                800, TechLevel.ORDINARY);
        assertEquals(400,
                ci.getSupplyPoints());
        assertEquals("supplies",
                ci.getCargoType());
    }
}