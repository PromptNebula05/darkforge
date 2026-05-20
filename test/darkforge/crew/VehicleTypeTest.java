package darkforge.crew;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeTest {

    // =========================================
    // Enum completeness
    // =========================================

    @Test
    void sixVehiclesTotal() {
        assertEquals(6, VehicleType.values().length);
    }

    @Test
    void threeRovers() {
        long count = Arrays.stream(VehicleType.values())
                .filter(VehicleType::isRover).count();
        assertEquals(3, count);
    }

    @Test
    void threeShuttles() {
        long count = Arrays.stream(VehicleType.values())
                .filter(VehicleType::isShuttle).count();
        assertEquals(3, count);
    }

    @Test
    void roverAndShuttleMutuallyExclusive() {
        for (VehicleType v : VehicleType.values()) {
            assertNotEquals(v.isRover(), v.isShuttle(),
                    v.name() + " must be rover XOR shuttle");
        }
    }

    // =========================================
    // Rover stats (Ch. 6)
    // =========================================

    @Test
    void rhinoStats() {
        VehicleType v = VehicleType.RHINO;
        assertAll(
                () -> assertEquals("Rhino", v.getDisplayName()),
                () -> assertEquals(
                        VehicleType.Category.ROVER, v.getCategory()),
                () -> assertEquals(3, v.getManeuverability()),
                () -> assertEquals(2, v.getSpeed()),
                () -> assertEquals(9, v.getHull()),
                () -> assertEquals(6, v.getArmor()),
                () -> assertEquals(4, v.getBlightProtection()),
                () -> assertEquals(7, v.getSlots()),
                () -> assertEquals(5, v.getPassengers()),
                () -> assertEquals(1500, v.getCargo()),
                () -> assertEquals("Wheeled", v.getPropulsion())
        );
    }

    @Test
    void crocodileStats() {
        VehicleType v = VehicleType.CROCODILE;
        assertAll(
                () -> assertEquals("Crocodile", v.getDisplayName()),
                () -> assertEquals(
                        VehicleType.Category.ROVER, v.getCategory()),
                () -> assertEquals(2, v.getManeuverability()),
                () -> assertEquals(2, v.getSpeed()),
                () -> assertEquals(11, v.getHull()),
                () -> assertEquals(7, v.getArmor()),
                () -> assertEquals(3, v.getBlightProtection()),
                () -> assertEquals(6, v.getSlots()),
                () -> assertEquals(8, v.getPassengers()),
                () -> assertEquals(2000, v.getCargo()),
                () -> assertEquals("Tracked", v.getPropulsion())
        );
    }

    @Test
    void sphinxStats() {
        VehicleType v = VehicleType.SPHINX;
        assertAll(
                () -> assertEquals("Sphinx", v.getDisplayName()),
                () -> assertEquals(
                        VehicleType.Category.ROVER, v.getCategory()),
                () -> assertEquals(4, v.getManeuverability()),
                () -> assertEquals(3, v.getSpeed()),
                () -> assertEquals(7, v.getHull()),
                () -> assertEquals(5, v.getArmor()),
                () -> assertEquals(2, v.getBlightProtection()),
                () -> assertEquals(5, v.getSlots()),
                () -> assertEquals(6, v.getPassengers()),
                () -> assertEquals(1000, v.getCargo()),
                () -> assertEquals("Hover", v.getPropulsion())
        );
    }

    // =========================================
    // Shuttle stats (Ch. 11)
    // =========================================

    @Test
    void grasshopperStats() {
        VehicleType v = VehicleType.GRASSHOPPER;
        assertAll(
                () -> assertEquals("Grasshopper",
                        v.getDisplayName()),
                () -> assertEquals(
                        VehicleType.Category.SHUTTLE, v.getCategory()),
                () -> assertEquals(3, v.getManeuverability()),
                () -> assertEquals(4, v.getSpeed()),
                () -> assertEquals(14, v.getHull()),
                () -> assertEquals(6, v.getArmor()),
                () -> assertEquals(0, v.getBlightProtection()),
                () -> assertEquals(7, v.getSlots()),
                () -> assertEquals(6, v.getPassengers()),
                () -> assertEquals(3000, v.getCargo()),
                () -> assertEquals("Graviton", v.getPropulsion())
        );
    }

    @Test
    void owlStats() {
        VehicleType v = VehicleType.OWL;
        assertAll(
                () -> assertEquals("Owl", v.getDisplayName()),
                () -> assertEquals(
                        VehicleType.Category.SHUTTLE, v.getCategory()),
                () -> assertEquals(2, v.getManeuverability()),
                () -> assertEquals(3, v.getSpeed()),
                () -> assertEquals(16, v.getHull()),
                () -> assertEquals(7, v.getArmor()),
                () -> assertEquals(0, v.getBlightProtection()),
                () -> assertEquals(5, v.getSlots()),
                () -> assertEquals(8, v.getPassengers()),
                () -> assertEquals(4000, v.getCargo()),
                () -> assertEquals("Graviton", v.getPropulsion())
        );
    }

    @Test
    void mantaStats() {
        VehicleType v = VehicleType.MANTA;
        assertAll(
                () -> assertEquals("Manta", v.getDisplayName()),
                () -> assertEquals(
                        VehicleType.Category.SHUTTLE, v.getCategory()),
                () -> assertEquals(4, v.getManeuverability()),
                () -> assertEquals(4, v.getSpeed()),
                () -> assertEquals(12, v.getHull()),
                () -> assertEquals(5, v.getArmor()),
                () -> assertEquals(0, v.getBlightProtection()),
                () -> assertEquals(3, v.getSlots()),
                () -> assertEquals(5, v.getPassengers()),
                () -> assertEquals(2000, v.getCargo()),
                () -> assertEquals("Graviton", v.getPropulsion())
        );
    }

    // =========================================
    // Category constraints
    // =========================================

    @Test
    void roversHaveBlightProtection() {
        Arrays.stream(VehicleType.values())
                .filter(VehicleType::isRover)
                .forEach(v -> assertTrue(
                        v.getBlightProtection() > 0,
                        v.name() + " rover must have blight protection"));
    }

    @Test
    void shuttlesHaveNoBlightProtection() {
        Arrays.stream(VehicleType.values())
                .filter(VehicleType::isShuttle)
                .forEach(v -> assertEquals(0,
                        v.getBlightProtection(),
                        v.name() + " shuttle blight protection must be 0"));
    }

    @Test
    void allShuttlesUseGraviton() {
        Arrays.stream(VehicleType.values())
                .filter(VehicleType::isShuttle)
                .forEach(v -> assertEquals("Graviton",
                        v.getPropulsion(),
                        v.name() + " shuttle must use Graviton"));
    }

    // =========================================
    // toString
    // =========================================

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void toStringContainsDisplayName(VehicleType v) {
        assertTrue(v.toString()
                .contains(v.getDisplayName()));
    }

    @ParameterizedTest
    @EnumSource(VehicleType.class)
    void toStringContainsPropulsion(VehicleType v) {
        assertTrue(v.toString()
                .contains(v.getPropulsion()));
    }
}