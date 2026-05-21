package darkforge.crew;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GarudaPowerTest {

    // =========================================
    // Sample powers
    // =========================================

    private GarudaPower attack;       // basic, universal
    private GarudaPower blightScan;   // basic, energy 0
    private GarudaPower raptorsCall;  // advanced, Ward-only
    private GarudaPower farsight;     // advanced, Guide-only
    private GarudaPower enshroud;     // advanced, Specter-only
    private GarudaPower fetch;        // advanced, universal

    @BeforeEach
    void setUp() {
        attack = new GarudaPower(
                "Attack",
                "The Bird attacks a target",
                "Deals damage equal to energy spent",
                true, Set.of(), 1);

        blightScan = new GarudaPower(
                "Blight Scan",
                "Scan for Blight contamination",
                "Detects Blight in the area",
                true, Set.of(), 0);

        raptorsCall = new GarudaPower(
                "Raptor's Call",
                "Ward signature power",
                "Rally allies with a piercing cry",
                false,
                EnumSet.of(BirdType.WARD), 2);

        farsight = new GarudaPower(
                "Farsight",
                "Guide signature power",
                "See through walls and obstacles",
                false,
                EnumSet.of(BirdType.GUIDE), 2);

        enshroud = new GarudaPower(
                "Enshroud",
                "Specter signature power",
                "Cloak allies in shadow",
                false,
                EnumSet.of(BirdType.SPECTER), 2);

        fetch = new GarudaPower(
                "Fetch",
                "Retrieve a small object",
                "Bird fetches an item within range",
                false, Set.of(), 1);
    }

    // =========================================
    // isBasic
    // =========================================

    @Test
    void attackIsBasic() {
        assertTrue(attack.isBasic());
    }

    @Test
    void blightScanIsBasic() {
        assertTrue(blightScan.isBasic());
    }

    @Test
    void raptorsCallIsNotBasic() {
        assertFalse(raptorsCall.isBasic());
    }

    @Test
    void fetchIsNotBasic() {
        assertFalse(fetch.isBasic());
    }

    // =========================================
    // isNativeFor — universal powers
    // =========================================

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void basicUniversalIsNativeForAllTypes(
            BirdType type) {
        assertTrue(attack.isNativeFor(type),
                "Basic universal power should be"
                        + " native for " + type);
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void advancedUniversalIsNativeForAllTypes(
            BirdType type) {
        assertTrue(fetch.isNativeFor(type),
                "Advanced universal power should"
                        + " be native for " + type);
    }

    // =========================================
    // isNativeFor — type-specific powers
    // =========================================

    @Test
    void raptorsCallNativeForWardOnly() {
        assertTrue(raptorsCall.isNativeFor(
                BirdType.WARD));
        assertFalse(raptorsCall.isNativeFor(
                BirdType.GUIDE));
        assertFalse(raptorsCall.isNativeFor(
                BirdType.SPECTER));
    }

    @Test
    void farsightNativeForGuideOnly() {
        assertFalse(farsight.isNativeFor(
                BirdType.WARD));
        assertTrue(farsight.isNativeFor(
                BirdType.GUIDE));
        assertFalse(farsight.isNativeFor(
                BirdType.SPECTER));
    }

    @Test
    void enshroudNativeForSpecterOnly() {
        assertFalse(enshroud.isNativeFor(
                BirdType.WARD));
        assertFalse(enshroud.isNativeFor(
                BirdType.GUIDE));
        assertTrue(enshroud.isNativeFor(
                BirdType.SPECTER));
    }

    // =========================================
    // getTrainingCost
    // =========================================

    @Test
    void nativeTrainingCostIs5() {
        assertEquals(5, raptorsCall
                .getTrainingCost(BirdType.WARD));
        assertEquals(5, farsight
                .getTrainingCost(BirdType.GUIDE));
        assertEquals(5, enshroud
                .getTrainingCost(BirdType.SPECTER));
    }

    @Test
    void nonNativeTrainingCostIs10() {
        assertEquals(10, raptorsCall
                .getTrainingCost(BirdType.GUIDE));
        assertEquals(10, raptorsCall
                .getTrainingCost(BirdType.SPECTER));
        assertEquals(10, farsight
                .getTrainingCost(BirdType.WARD));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void universalTrainingCostAlways5(
            BirdType type) {
        assertEquals(5,
                fetch.getTrainingCost(type),
                "Universal advanced power should"
                        + " cost 5 CP for all types");
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void basicTrainingCostAlways5(
            BirdType type) {
        assertEquals(5,
                attack.getTrainingCost(type),
                "Basic power should cost 5 CP"
                        + " for all types");
    }

    // =========================================
    // Energy cost
    // =========================================

    @Test
    void blightScanCostsZeroEnergy() {
        assertEquals(0,
                blightScan.getEnergyCost());
    }

    @Test
    void attackCostsOneEnergy() {
        assertEquals(1,
                attack.getEnergyCost());
    }

    @Test
    void raptorsCallCostsTwoEnergy() {
        assertEquals(2,
                raptorsCall.getEnergyCost());
    }

    // =========================================
    // Getters
    // =========================================

    @Test
    void getEffectReturnsCorrectString() {
        assertEquals(
                "Deals damage equal to energy spent",
                attack.getEffect());
    }

    @Test
    void getNativeTypesEmptyForUniversal() {
        assertTrue(
                attack.getNativeTypes().isEmpty());
        assertTrue(
                fetch.getNativeTypes().isEmpty());
    }

    @Test
    void getNativeTypesContainsCorrectType() {
        assertTrue(raptorsCall.getNativeTypes()
                .contains(BirdType.WARD));
        assertEquals(1,
                raptorsCall.getNativeTypes().size());
    }

    @Test
    void nativeTypesIsUnmodifiable() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> raptorsCall.getNativeTypes()
                        .add(BirdType.GUIDE));
    }

    // =========================================
    // display
    // =========================================

    @Test
    void displayContainsNameAndEnergy() {
        String result = attack.display();
        assertTrue(result.contains("Attack"));
        assertTrue(result.contains("Energy: 1"));
    }

    @Test
    void displayUniversalShowsUniversalTag() {
        String result = fetch.display();
        assertTrue(result.contains("Universal"));
    }

    @Test
    void displayTypeSpecificShowsType() {
        String result = raptorsCall.display();
        assertTrue(result.contains("WARD"));
    }

    @Test
    void displayContainsEffect() {
        String result = farsight.display();
        assertTrue(result.contains(
                "See through walls and obstacles"));
    }

    // =========================================
    // GameEntity inheritance
    // =========================================

    @Test
    void getNameReturnsCorrectName() {
        assertEquals("Raptor's Call",
                raptorsCall.getName());
    }

    @Test
    void getDescriptionReturnsCorrectDesc() {
        assertEquals(
                "Ward signature power",
                raptorsCall.getDescription());
    }

    @Test
    void toStringDelegatesToDisplay() {
        assertEquals(raptorsCall.display(),
                raptorsCall.toString());
    }
}