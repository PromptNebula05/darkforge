package darkforge.data;

import darkforge.model.*;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class CatalogAnalyticsTest {

    private CatalogAnalytics analytics;

    @BeforeEach
    void setUp() {
        ItemCatalog catalog =
                new ItemCatalog(List.of(
                        new CharacterItem(
                                "Cheap", "C",
                                0.5, 100, "Tools",
                                TechLevel.ORDINARY,
                                false, 0),
                        new CharacterItem(
                                "Expensive", "E",
                                2.0, 5000, "Tools",
                                TechLevel.GUILD,
                                false, 3)));
        analytics =
                new CatalogAnalytics(catalog);
    }

    @Test
    @DisplayName("Top N returns correct"
            + " order")
    void testTopN() {
        var top = analytics
                .getTopNByCost(1);
        assertEquals("Expensive",
                top.get(0).getName());
    }

    @Test
    @DisplayName("Partition by budget")
    void testPartition() {
        var parts = analytics
                .partitionByAffordability(
                        1000);
        assertEquals(1,
                parts.get(true).size());
        assertEquals(1,
                parts.get(false).size());
    }

    @Test
    @DisplayName("Report contains stats")
    void testReport() {
        String report = analytics
                .generateCatalogReport();
        assertTrue(report.contains(
                "Total items: 2"));
    }
}
