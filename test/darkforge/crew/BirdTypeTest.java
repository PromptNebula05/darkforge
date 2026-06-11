package darkforge.crew;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class BirdTypeTest {

    // =========================================
    // Enum completeness
    // =========================================

    @Test
    void allThreeTypesPresent() {
        BirdType[] types = BirdType.values();
        assertEquals(3, types.length);
        assertNotNull(BirdType.valueOf("WARD"));
        assertNotNull(BirdType.valueOf("GUIDE"));
        assertNotNull(BirdType.valueOf("SPECTER"));
    }

    // =========================================
    // Starting stats
    // =========================================

    @Test
    void wardStats() {
        assertEquals("Ward", BirdType.WARD.getDisplayName());
        assertEquals(5, BirdType.WARD.getStartingHealth());
        assertEquals(2, BirdType.WARD.getStartingEnergy());
    }

    @Test
    void guideStats() {
        assertEquals("Guide", BirdType.GUIDE.getDisplayName());
        assertEquals(4, BirdType.GUIDE.getStartingHealth());
        assertEquals(3, BirdType.GUIDE.getStartingEnergy());
    }

    @Test
    void specterStats() {
        assertEquals("Specter", BirdType.SPECTER.getDisplayName());
        assertEquals(3, BirdType.SPECTER.getStartingHealth());
        assertEquals(4, BirdType.SPECTER.getStartingEnergy());
    }

    // =========================================
    // Health + Energy inverse relationship
    // =========================================

    @Test
    void healthPlusEnergyTotalIsSeven() {
        for (BirdType type : BirdType.values()) {
            assertEquals(7,
                    type.getStartingHealth()
                            + type.getStartingEnergy(),
                    type.name()
                            + " health + energy should total 7");
        }
    }

    // =========================================
    // Special powers
    // =========================================

    @Test
    void wardSpecialPower() {
        assertEquals("Raptor's Call",
                BirdType.WARD.getSpecialPower());
    }

    @Test
    void guideSpecialPower() {
        assertEquals("Farsight",
                BirdType.GUIDE.getSpecialPower());
    }

    @Test
    void specterSpecialPower() {
        assertEquals("Enshroud",
                BirdType.SPECTER.getSpecialPower());
    }

    @Test
    void eachTypeHasUniqueSpecialPower() {
        String ward = BirdType.WARD.getSpecialPower();
        String guide = BirdType.GUIDE.getSpecialPower();
        String specter = BirdType.SPECTER.getSpecialPower();
        assertNotEquals(ward, guide);
        assertNotEquals(ward, specter);
        assertNotEquals(guide, specter);
    }

    // =========================================
    // toString
    // =========================================

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void toStringContainsDisplayName(BirdType type) {
        assertTrue(type.toString()
                .contains(type.getDisplayName()));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void toStringContainsSpecialPower(BirdType type) {
        assertTrue(type.toString()
                .contains(type.getSpecialPower()));
    }
}