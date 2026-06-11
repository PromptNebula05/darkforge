package darkforge.data;

import darkforge.crew.BirdType;
import darkforge.crew.GarudaPower;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GarudaPowerRegistryTest {

    private GarudaPowerRegistry registry;

    // =========================================
    // All 18 powers from Ch. 5
    // =========================================

    @BeforeEach
    void setUp() {
        List<GarudaPower> powers =
                new ArrayList<>();

        // --- 6 basic (universal) ---
        powers.add(basic("Attack",
                "Deals damage", 1));
        powers.add(basic("Defend",
                "Absorbs damage", 1));
        powers.add(basic("Clear Blight",
                "Removes contamination", 1));
        powers.add(basic("Blight Scan",
                "Detects Blight", 0));
        powers.add(basic("Soak Blight",
                "Takes Blight damage", 1));
        powers.add(basic("Glow",
                "Illuminates area", 1));

        // --- 2 Ward-only ---
        powers.add(advanced("Raptor's Call",
                "Rally allies", 2,
                EnumSet.of(BirdType.WARD)));
        powers.add(advanced("Energy Bridge",
                "Transfer energy to ally", 2,
                EnumSet.of(BirdType.WARD)));

        // --- 2 Guide-only ---
        powers.add(advanced("Farsight",
                "See through obstacles", 2,
                EnumSet.of(BirdType.GUIDE)));
        powers.add(advanced("Illusionary Veil",
                "Create illusions", 2,
                EnumSet.of(BirdType.GUIDE)));

        // --- 2 Specter-only ---
        powers.add(advanced("Enshroud",
                "Cloak in shadow", 2,
                EnumSet.of(BirdType.SPECTER)));
        powers.add(advanced("Soul Delve",
                "Probe a mind", 3,
                EnumSet.of(BirdType.SPECTER)));

        // --- 6 universal advanced ---
        powers.add(advanced("Fetch",
                "Retrieve an item", 1, Set.of()));
        powers.add(advanced(
                "Dimensional Flitting",
                "Short-range teleport", 2,
                Set.of()));
        powers.add(advanced("Flicker Field",
                "Defensive shimmer", 2, Set.of()));
        powers.add(advanced("Glyph Warden",
                "Set protective glyph", 2,
                Set.of()));
        powers.add(advanced("Phoenix Engine",
                "Revive from broken", 3,
                Set.of()));
        powers.add(advanced("Delve Dream",
                "Shared vision", 2, Set.of()));

        registry =
                new GarudaPowerRegistry(powers);
    }

    // =========================================
    // getBasicPowers
    // =========================================

    @Test
    void basicPowersReturnsExactly6() {
        assertEquals(6,
                registry.getBasicPowers().size());
    }

    @Test
    void allBasicPowersAreBasic() {
        assertTrue(registry.getBasicPowers()
                .stream()
                .allMatch(GarudaPower::isBasic));
    }

    @Test
    void basicPowersContainsExpectedNames() {
        Set<String> names = registry
                .getBasicPowers().stream()
                .map(GarudaPower::getName)
                .collect(Collectors.toSet());
        assertTrue(names.contains("Attack"));
        assertTrue(names.contains("Defend"));
        assertTrue(
                names.contains("Clear Blight"));
        assertTrue(
                names.contains("Blight Scan"));
        assertTrue(
                names.contains("Soak Blight"));
        assertTrue(names.contains("Glow"));
    }

    // =========================================
    // getAllAdvancedPowers
    // =========================================

    @Test
    void advancedPowersReturnsExactly12() {
        assertEquals(12,
                registry.getAllAdvancedPowers()
                        .size());
    }

    @Test
    void noAdvancedPowerIsBasic() {
        assertTrue(registry
                .getAllAdvancedPowers().stream()
                .noneMatch(GarudaPower::isBasic));
    }

    // =========================================
    // getAdvancedPowersFor(WARD)
    // =========================================

    @Test
    void wardAdvancedIncludesRaptorsCall() {
        List<String> names = advancedNamesFor(
                BirdType.WARD);
        assertTrue(
                names.contains("Raptor's Call"));
    }

    @Test
    void wardAdvancedIncludesEnergyBridge() {
        List<String> names = advancedNamesFor(
                BirdType.WARD);
        assertTrue(
                names.contains("Energy Bridge"));
    }

    @Test
    void wardAdvancedIncludesAllUniversals() {
        List<String> names = advancedNamesFor(
                BirdType.WARD);
        assertTrue(names.contains("Fetch"));
        assertTrue(names.contains(
                "Dimensional Flitting"));
        assertTrue(
                names.contains("Flicker Field"));
        assertTrue(
                names.contains("Glyph Warden"));
        assertTrue(
                names.contains("Phoenix Engine"));
        assertTrue(
                names.contains("Delve Dream"));
    }

    @Test
    void wardAdvancedTotalIs8() {
        // 2 Ward-only + 6 universal
        assertEquals(8,
                registry.getAdvancedPowersFor(
                        BirdType.WARD).size());
    }

    // =========================================
    // getAdvancedPowersFor(GUIDE)
    // =========================================

    @Test
    void guideAdvancedIncludesFarsight() {
        List<String> names = advancedNamesFor(
                BirdType.GUIDE);
        assertTrue(
                names.contains("Farsight"));
    }

    @Test
    void guideAdvancedIncludesIllusionaryVeil() {
        List<String> names = advancedNamesFor(
                BirdType.GUIDE);
        assertTrue(names.contains(
                "Illusionary Veil"));
    }

    @Test
    void guideAdvancedIncludesAllUniversals() {
        List<String> names = advancedNamesFor(
                BirdType.GUIDE);
        assertTrue(names.contains("Fetch"));
        assertTrue(names.contains(
                "Dimensional Flitting"));
        assertTrue(
                names.contains("Flicker Field"));
        assertTrue(
                names.contains("Glyph Warden"));
        assertTrue(
                names.contains("Phoenix Engine"));
        assertTrue(
                names.contains("Delve Dream"));
    }

    @Test
    void guideAdvancedTotalIs8() {
        assertEquals(8,
                registry.getAdvancedPowersFor(
                        BirdType.GUIDE).size());
    }

    // =========================================
    // getAdvancedPowersFor(SPECTER)
    // =========================================

    @Test
    void specterAdvancedIncludesEnshroud() {
        List<String> names = advancedNamesFor(
                BirdType.SPECTER);
        assertTrue(
                names.contains("Enshroud"));
    }

    @Test
    void specterAdvancedIncludesSoulDelve() {
        List<String> names = advancedNamesFor(
                BirdType.SPECTER);
        assertTrue(
                names.contains("Soul Delve"));
    }

    @Test
    void specterAdvancedIncludesAllUniversals() {
        List<String> names = advancedNamesFor(
                BirdType.SPECTER);
        assertTrue(names.contains("Fetch"));
        assertTrue(names.contains(
                "Dimensional Flitting"));
        assertTrue(
                names.contains("Flicker Field"));
        assertTrue(
                names.contains("Glyph Warden"));
        assertTrue(
                names.contains("Phoenix Engine"));
        assertTrue(
                names.contains("Delve Dream"));
    }

    @Test
    void specterAdvancedTotalIs8() {
        assertEquals(8,
                registry.getAdvancedPowersFor(
                        BirdType.SPECTER).size());
    }

    // =========================================
    // getPowerByName
    // =========================================

    @Test
    void getPowerByNameFarsight() {
        GarudaPower farsight =
                registry.getPowerByName("Farsight");
        assertNotNull(farsight);
        assertEquals("Farsight",
                farsight.getName());
        assertFalse(farsight.isBasic());
        assertTrue(farsight.getNativeTypes()
                .contains(BirdType.GUIDE));
    }

    @Test
    void getPowerByNameCaseInsensitive() {
        assertNotNull(
                registry.getPowerByName("farsight"));
        assertNotNull(
                registry.getPowerByName("FARSIGHT"));
    }

    @Test
    void getPowerByNameNonexistentReturnsNull() {
        assertNull(
                registry.getPowerByName(
                        "nonexistent"));
    }

    @Test
    void getPowerByNameAttackReturnsBasic() {
        GarudaPower attack =
                registry.getPowerByName("Attack");
        assertNotNull(attack);
        assertTrue(attack.isBasic());
    }

    // =========================================
    // Total size
    // =========================================

    @Test
    void totalSizeIs18() {
        assertEquals(18, registry.size());
    }

    @Test
    void getAllPowersReturns18() {
        assertEquals(18,
                registry.getAllPowers().size());
    }

    @Test
    void getAllPowersIsUnmodifiable() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> registry.getAllPowers()
                        .add(basic("Hack", "test", 0)));
    }

    // =========================================
    // No type-exclusive overlap
    // =========================================

    @Test
    void wardExclusivesNotInGuide() {
        List<String> guideNames =
                advancedNamesFor(BirdType.GUIDE);
        assertFalse(guideNames.contains(
                "Raptor's Call"));
        assertFalse(guideNames.contains(
                "Energy Bridge"));
    }

    @Test
    void wardExclusivesNotInSpecter() {
        List<String> specterNames =
                advancedNamesFor(BirdType.SPECTER);
        assertFalse(specterNames.contains(
                "Raptor's Call"));
        assertFalse(specterNames.contains(
                "Energy Bridge"));
    }

    @Test
    void guideExclusivesNotInWard() {
        List<String> wardNames =
                advancedNamesFor(BirdType.WARD);
        assertFalse(wardNames.contains(
                "Farsight"));
        assertFalse(wardNames.contains(
                "Illusionary Veil"));
    }

    @Test
    void specterExclusivesNotInGuide() {
        List<String> guideNames =
                advancedNamesFor(BirdType.GUIDE);
        assertFalse(guideNames.contains(
                "Enshroud"));
        assertFalse(guideNames.contains(
                "Soul Delve"));
    }

    // =========================================
    // Helpers
    // =========================================

    private List<String> advancedNamesFor(
            BirdType type) {
        return registry
                .getAdvancedPowersFor(type).stream()
                .map(GarudaPower::getName)
                .collect(Collectors.toList());
    }

    private static GarudaPower basic(
            String name, String effect,
            int energyCost) {
        return new GarudaPower(name,
                name + " (basic)", effect,
                true, Set.of(), energyCost);
    }

    private static GarudaPower advanced(
            String name, String effect,
            int energyCost,
            Set<BirdType> nativeTypes) {
        return new GarudaPower(name,
                name + " (advanced)", effect,
                false, nativeTypes, energyCost);
    }
}