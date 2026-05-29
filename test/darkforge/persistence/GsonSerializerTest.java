package darkforge.persistence;

import darkforge.data.ItemCatalog;
import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class GsonSerializerTest {

    @Test
    @DisplayName("Gson roundtrip"
            + " preserves VehicleModule")
    void testModuleRoundtrip() {
        VehicleModule vm =
                new VehicleModule(
                        "Airlock", "Pressurized",
                        1, 2, "utility",
                        "Crew transfer.",
                        TechLevel.ORDINARY,
                        false, null, false);
        ItemCatalog cat =
                new ItemCatalog(
                        List.of(vm));
        GsonCatalogSerializer ser =
                new GsonCatalogSerializer();
        String json = ser.serialize(cat);
        ItemCatalog loaded =
                ser.deserialize(json);
        assertEquals(1, loaded.size());
        assertTrue(
                loaded.getAll().get(0)
                        instanceof VehicleModule);
    }
}
