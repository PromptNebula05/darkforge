package darkforge.crew;

import darkforge.model.*;
import darkforge.model.profession.Traveler;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api
        .Assertions.*;

class ExtendedCrewAnalyticsTest {

    @Test
    @DisplayName("Crew inventory value"
            + " sums correctly")
    void testCrewInventoryValue() {
        Crew crew = new Crew("Test");
        Explorer e = new Traveler(
                "Kira");
        e.addItem(new CharacterItem(
                "A", "A", 0.5, 100,
                "Tools",
                TechLevel.ORDINARY,
                false, 0));
        e.addItem(new CharacterItem(
                "B", "B", 1.0, 200,
                "Tools",
                TechLevel.ORDINARY,
                false, 0));
        crew.addMember(e);
        CrewAnalytics ca =
                new CrewAnalytics(crew);
        assertEquals(300,
                ca.getCrewInventoryValue());
    }
}
