package darkforge.persistence;

import darkforge.data.ItemCatalog;
import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

@DisplayName("All 3 serialization"
        + " approaches produce identical"
        + " roundtrips")
class SerializationComparisonTest {

    private ItemCatalog original;

    @BeforeEach
    void setUp() {
        original = new ItemCatalog(
                List.of(
                        new Weapon(
                                "Test Gun", "TG",
                                1.0, 500,
                                TechLevel.ORDINARY,
                                false,
                                EquipmentWeight.REGULAR,
                                1, 3, 2,
                                Grip.TWO_HANDED,
                                "Medium",
                                WeaponType.RANGED_RIFLE,
                                List.of("Autofire")),
                        new Armor(
                                "Test Suit", "TS",
                                2.0, 1000,
                                "Armor",
                                TechLevel.GUILD,
                                false,
                                EquipmentWeight.HEAVY,
                                0, 4, 1, 1,
                                List.of("Bulky")),
                        new VehicleModule(
                                "Test Mod", "TM",
                                1, 3, "sensor",
                                "+1 sensor",
                                TechLevel.GUILD,
                                false, null, false),
                        new CargoItem(
                                "Fuel", "Extra",
                                400, "supplies",
                                800,
                                TechLevel.ORDINARY)));
    }

    @Test
    @DisplayName("All approaches"
            + " preserve item count")
    void testAllPreserveCount()
            throws Exception {
        // Binary
        var bin =
                new BinaryCatalogSerializer();
        Path tmp = Files.createTempFile(
                "ser-comp", ".bin");
        bin.serialize(original, tmp);
        assertEquals(4,
                bin.deserialize(tmp).size());
        Files.delete(tmp);

        // JSON
        var json =
                new JsonCatalogSerializer();
        String jsonStr =
                json.serialize(original);
        assertEquals(4,
                json.deserialize(jsonStr)
                        .size());

        // Gson
        var gson =
                new GsonCatalogSerializer();
        String gsonStr =
                gson.serialize(original);
        assertEquals(4,
                gson.deserialize(gsonStr)
                        .size());
    }
}
