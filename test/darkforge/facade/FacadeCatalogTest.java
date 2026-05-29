package darkforge.facade;

import darkforge.data.ItemCatalog;
import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class FacadeCatalogTest {

    private FacadeCatalog facade;

    @BeforeEach
    void setUp() {
        ItemCatalog catalog =
                new ItemCatalog(List.of(
                        new CharacterItem(
                                "Medkit", "Kit",
                                0.5, 400,
                                "Medicine",
                                TechLevel.ORDINARY,
                                false, 2),
                        new CharacterItem(
                                "Scanner", "Scan",
                                0.5, 800,
                                "Scanning",
                                TechLevel.GUILD,
                                false, 2)));
        // Same-package access to the
        // package-private constructor.
        facade = new FacadeCatalog(
                catalog);
    }

    @Test
    @DisplayName("Search finds items")
    void testSearch() {
        assertEquals(1,
                facade.search("med")
                        .size());
    }

    @Test
    @DisplayName("Catalog size correct")
    void testSize() {
        assertEquals(2,
                facade.getCatalogSize());
    }

    @Test
    @DisplayName("Tech level filter")
    void testTechFilter() {
        assertEquals(1,
                facade.filterByTechLevel(
                                TechLevel.GUILD)
                        .size());
    }
}
