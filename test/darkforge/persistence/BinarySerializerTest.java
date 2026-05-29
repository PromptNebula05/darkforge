package darkforge.persistence;

import darkforge.data.ItemCatalog;
import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class BinarySerializerTest {

    @Test
    @DisplayName("Binary roundtrip"
            + " preserves items")
    void testRoundtrip() throws Exception {
        ItemCatalog original =
                new ItemCatalog(List.of(
                        new CharacterItem(
                                "Medkit", "Kit",
                                0.5, 400,
                                "Medicine",
                                TechLevel.ORDINARY,
                                false, 2)));
        BinaryCatalogSerializer ser =
                new BinaryCatalogSerializer();
        Path tmp = Files.createTempFile(
                "darkforge-test", ".bin");
        ser.serialize(original, tmp);
        ItemCatalog loaded =
                ser.deserialize(tmp);
        assertEquals(1, loaded.size());
        assertEquals("Medkit",
                loaded.getAll().get(0)
                        .getName());
        Files.delete(tmp);
    }
}
