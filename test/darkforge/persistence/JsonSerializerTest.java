package darkforge.persistence;

import darkforge.data.ItemCatalog;
import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class JsonSerializerTest {

    @Test
    @DisplayName("JSON roundtrip"
            + " preserves weapon")
    void testWeaponRoundtrip() {
        Weapon w = new Weapon(
                "Coiler", "EM gun",
                0.5, 800,
                TechLevel.GUILD, false,
                EquipmentWeight.LIGHT,
                2, 2, 2,
                Grip.ONE_HANDED,
                "Short-Medium",
                WeaponType.RANGED_PISTOL,
                List.of());
        ItemCatalog cat =
                new ItemCatalog(List.of(w));
        JsonCatalogSerializer ser =
                new JsonCatalogSerializer();
        String json = ser.serialize(cat);
        ItemCatalog loaded =
                ser.deserialize(json);
        assertEquals(1, loaded.size());
        assertTrue(
                loaded.getAll().get(0)
                        instanceof Weapon);
        Weapon restored =
                (Weapon) loaded.getAll()
                        .get(0);
        assertEquals(2,
                restored.getDamage());
    }

    @Test
    @DisplayName("JSON preserves"
            + " polymorphic types")
    void testPolymorphicTypes() {
        ItemCatalog cat =
                new ItemCatalog(List.of(
                        new CharacterItem(
                                "Tool", "T",
                                1.0, 300, "Tools",
                                TechLevel.ORDINARY,
                                false, 2),
                        new Armor(
                                "Suit", "A",
                                1.0, 500, "Armor",
                                TechLevel.ORDINARY,
                                false,
                                EquipmentWeight.REGULAR,
                                0, 3, 0, 0,
                                List.of())));
        JsonCatalogSerializer ser =
                new JsonCatalogSerializer();
        String json = ser.serialize(cat);
        ItemCatalog loaded =
                ser.deserialize(json);
        assertEquals(2, loaded.size());
        assertTrue(
                loaded.getAll().get(1)
                        instanceof Armor);
    }
}
