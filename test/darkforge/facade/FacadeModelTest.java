package darkforge.facade;

import darkforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FacadeModel sub-facade.
 * Verifies profession listing, talent category access,
 * and attribute retrieval via the facade layer.
 */
class FacadeModelTest {

    private final FacadeModel facade =
            FacadeDarkforge.getTheInstance().modelAccess();

    @Test
    void shouldReturnAllEightProfessionNames() {
        List<String> names =
                facade.getAvailableProfessions();
        assertEquals(8, names.size());
        assertTrue(names.contains("Scholar"));
        assertTrue(names.contains("Enforcer"));
        assertTrue(names.contains("Artist"));
        assertTrue(names.contains("Esoteric"));
        assertTrue(names.contains("OddJobber"));
        assertTrue(names.contains("Roughneck"));
        assertTrue(names.contains("Scoundrel"));
        assertTrue(names.contains("Traveler"));
    }

    @Test
    void shouldReturnAllSixAttributeNames() {
        List<String> attrs = facade.getAttributeNames();
        assertEquals(6, attrs.size());
    }

    @Test
    void shouldReturnTalentCategories() {
        TalentCategory[] categories =
                facade.getTalentCategories();
        assertTrue(categories.length > 0,
                "Should have talent categories");
    }

    @Test
    void shouldReturnAttributeByName() {
        Attribute attr =
                facade.getAttributeByName("LOGIC");
        assertNotNull(attr);
        assertEquals(Attribute.LOGIC, attr);
    }

    @Test
    void shouldReturnNullForUnknownAttributeName() {
        Attribute attr =
                facade.getAttributeByName("WIZARDRY");
        assertNull(attr);
    }
}