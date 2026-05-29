package darkforge.model;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class ArmorTest {

    @Test
    @DisplayName("Armor stores rating"
            + " and blight protection")
    void testArmorFields() {
        Armor suit = new Armor(
                "Delver's Suit",
                "Environmental suit",
                2.0, 5000,
                "Delving Suit",
                TechLevel.GUILD, false,
                EquipmentWeight.HEAVY,
                0, 3, 3, 2,
                List.of("Vacuum Resistant",
                        "Comlink"));
        assertEquals(3,
                suit.getArmorRating());
        assertEquals(3,
                suit.getBlightProtection());
        assertEquals(2,
                suit.getExtras());
        assertEquals(2,
                suit.getFeatures().size());
    }

    @Test
    @DisplayName("Armor item type"
            + " returns 'armor'")
    void testItemType() {
        Armor a = new Armor(
                "Flight Suit", "Basic",
                0.5, 200, "Armor",
                TechLevel.ORDINARY, false,
                EquipmentWeight.LIGHT,
                0, 2, 0, 0, List.of());
        assertEquals("armor",
                a.getItemType());
    }
}