package darkforge.crew;

import darkforge.model.*;
import darkforge.model.profession.Enforcer;
import darkforge.model.profession.Traveler;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class CrewInventorySearchTest {

    @Test
    @DisplayName("Cross-entity search"
            + " finds items")
    void testCrossEntitySearch() {
        Crew crew = new Crew(
                "Test Crew");
        Explorer e = new Traveler(
                "Kira");
        e.addItem(new CharacterItem(
                "Medkit", "Kit",
                0.5, 400, "Medicine",
                TechLevel.ORDINARY,
                false, 2));
        crew.addMember(e);

        List<Item> found =
                CrewInventorySearch
                        .searchAllInventories(
                                crew, "med");
        assertEquals(1, found.size());
    }

    @Test
    @DisplayName("Total crew item"
            + " value")
    void testTotalValue() {
        Crew crew = new Crew(
                "Test Crew");
        Explorer e = new Enforcer(
                "Zara");
        e.addItem(new CharacterItem(
                "Toolkit", "Tools",
                1.0, 300, "Tools",
                TechLevel.ORDINARY,
                false, 2));
        crew.addMember(e);
        assertEquals(300,
                CrewInventorySearch
                        .getTotalCrewItemValue(
                                crew));
    }
}
