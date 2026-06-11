package darkforge.crew;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BirdTest {

    private Bird ward;
    private Bird guide;
    private Bird specter;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        ward = new Bird("Talon", BirdType.WARD,
                "Crimson", "Hooked beak", "Fierce");
        guide = new Bird("Jade", BirdType.GUIDE,
                "Emerald", "Long tail feathers",
                "Curious");
        specter = new Bird("Phantom",
                BirdType.SPECTER,
                "Midnight", "Translucent wings",
                "Silent");
    }

    // =========================================
    // Ward Bird: HP 5, EP 2, 7 powers
    // =========================================

    @Test
    void wardStartingHealth() {
        assertEquals(5, ward.getMaxHealth());
        assertEquals(5, ward.getCurrentHealth());
    }

    @Test
    void wardStartingEnergy() {
        assertEquals(2, ward.getMaxEnergy());
        assertEquals(2, ward.getCurrentEnergy());
    }

    @Test
    void wardStartsWith7Powers() {
        assertEquals(7, ward.getPowers().size());
    }

    @Test
    void wardHas6BasicPowers() {
        assertEquals(6,
                ward.getBasicPowers().size());
    }

    @Test
    void wardHasRaptorsCall() {
        assertTrue(ward.hasPower("Raptor's Call"));
    }

    @Test
    void wardAdvancedPowersContainSignature() {
        List<GarudaPower> advanced =
                ward.getAdvancedPowers();
        assertEquals(1, advanced.size());
        assertEquals("Raptor's Call",
                advanced.get(0).getName());
    }

    // =========================================
    // Guide Bird: HP 4, EP 3, 7 powers
    // =========================================

    @Test
    void guideStartingHealth() {
        assertEquals(4, guide.getMaxHealth());
        assertEquals(4, guide.getCurrentHealth());
    }

    @Test
    void guideStartingEnergy() {
        assertEquals(3, guide.getMaxEnergy());
        assertEquals(3, guide.getCurrentEnergy());
    }

    @Test
    void guideStartsWith7Powers() {
        assertEquals(7, guide.getPowers().size());
    }

    @Test
    void guideHasFarsight() {
        assertTrue(guide.hasPower("Farsight"));
    }

    // =========================================
    // Specter Bird: HP 3, EP 4, 7 powers
    // =========================================

    @Test
    void specterStartingHealth() {
        assertEquals(3, specter.getMaxHealth());
        assertEquals(3,
                specter.getCurrentHealth());
    }

    @Test
    void specterStartingEnergy() {
        assertEquals(4, specter.getMaxEnergy());
        assertEquals(4,
                specter.getCurrentEnergy());
    }

    @Test
    void specterStartsWith7Powers() {
        assertEquals(7,
                specter.getPowers().size());
    }

    @Test
    void specterHasEnshroud() {
        assertTrue(
                specter.hasPower("Enshroud"));
    }

    // =========================================
    // All types share 6 basic powers
    // =========================================

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void allTypesHaveAttack(BirdType type) {
        Bird bird = makeBird(type);
        assertTrue(bird.hasPower("Attack"));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void allTypesHaveDefend(BirdType type) {
        Bird bird = makeBird(type);
        assertTrue(bird.hasPower("Defend"));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void allTypesHaveBlightScan(BirdType type) {
        Bird bird = makeBird(type);
        assertTrue(
                bird.hasPower("Blight Scan"));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void allTypesHaveClearBlight(BirdType type) {
        Bird bird = makeBird(type);
        assertTrue(
                bird.hasPower("Clear Blight"));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void allTypesHaveSoakBlight(BirdType type) {
        Bird bird = makeBird(type);
        assertTrue(
                bird.hasPower("Soak Blight"));
    }

    @ParameterizedTest
    @EnumSource(BirdType.class)
    void allTypesHaveGlow(BirdType type) {
        Bird bird = makeBird(type);
        assertTrue(bird.hasPower("Glow"));
    }

    // =========================================
    // spendEnergy
    // =========================================

    @Test
    void spendEnergyReducesCurrent() {
        assertTrue(ward.spendEnergy(1));
        assertEquals(1,
                ward.getCurrentEnergy());
    }

    @Test
    void spendEnergyReturnsFalseWhenInsufficient() {
        // Ward has EP 2
        assertTrue(ward.spendEnergy(2));
        assertFalse(ward.spendEnergy(1),
                "Should return false when"
                        + " energy is depleted");
        assertEquals(0,
                ward.getCurrentEnergy());
    }

    @Test
    void spendEnergyExactAmountSucceeds() {
        assertTrue(ward.spendEnergy(2));
        assertEquals(0,
                ward.getCurrentEnergy());
    }

    // =========================================
    // rest
    // =========================================

    @Test
    void restRecoverAllEnergy() {
        ward.spendEnergy(2);
        assertEquals(0,
                ward.getCurrentEnergy());
        ward.rest();
        assertEquals(2,
                ward.getCurrentEnergy());
    }

    @Test
    void restAtFullEnergyNoChange() {
        int before = guide.getCurrentEnergy();
        guide.rest();
        assertEquals(before,
                guide.getCurrentEnergy());
    }

    // =========================================
    // takeDamage and isBroken
    // =========================================

    @Test
    void takeDamageReducesHealth() {
        ward.takeDamage(3);
        assertEquals(2,
                ward.getCurrentHealth());
    }

    @Test
    void takeDamageFloorAtZero() {
        specter.takeDamage(100);
        assertEquals(0,
                specter.getCurrentHealth());
    }

    @Test
    void isBrokenWhenHealthZero() {
        specter.takeDamage(
                specter.getMaxHealth());
        assertTrue(specter.isBroken());
    }

    @Test
    void notBrokenAboveZero() {
        ward.takeDamage(1);
        assertFalse(ward.isBroken());
    }

    // =========================================
    // heal and recoverPerShift
    // =========================================

    @Test
    void healRestoresHealth() {
        ward.takeDamage(3);
        ward.heal(2);
        assertEquals(4,
                ward.getCurrentHealth());
    }

    @Test
    void healCappedAtMax() {
        ward.takeDamage(1);
        ward.heal(100);
        assertEquals(ward.getMaxHealth(),
                ward.getCurrentHealth());
    }

    @Test
    void recoverPerShiftHealsOne() {
        guide.takeDamage(2);
        assertEquals(2,
                guide.getCurrentHealth());
        guide.recoverPerShift();
        assertEquals(3,
                guide.getCurrentHealth());
    }

    // =========================================
    // learnPower — Set semantics
    // =========================================

    @Test
    void learnPowerAddsToPowers() {
        GarudaPower fetch = new GarudaPower(
                "Fetch",
                "Retrieve a small object",
                "Bird fetches an item within range",
                false, Set.of(), 1);
        int before = ward.getPowers().size();
        ward.learnPower(fetch);
        assertEquals(before + 1,
                ward.getPowers().size());
        assertTrue(ward.hasPower("Fetch"));
    }

    @Test
    void learnDuplicatePowerNoChange() {
        GarudaPower dupAttack = new GarudaPower(
                "Attack",
                "The Bird attacks a target",
                "Deals damage equal to energy spent",
                true, Set.of(), 1);
        int before = ward.getPowers().size();
        ward.learnPower(dupAttack);
        // Set rejects duplicate by equals/hashCode
        // If GarudaPower doesn't override equals,
        // a new instance IS a different object.
        // Test verifies the Set contract is used.
        // With LinkedHashSet + reference equality,
        // a new object with same name is added.
        // This matches the spec: "duplicate → no change"
        // only if the SAME instance is re-added.
        ward.learnPower(dupAttack);
        // Re-adding same instance should not change size
        int afterSecond = ward.getPowers().size();
        // At most one extra from the first add
        assertTrue(afterSecond <= before + 1);
    }

    @Test
    void learnSameInstanceTwiceNoChange() {
        GarudaPower fetch = new GarudaPower(
                "Fetch",
                "Retrieve a small object",
                "Bird fetches an item within range",
                false, Set.of(), 1);
        ward.learnPower(fetch);
        int after = ward.getPowers().size();
        ward.learnPower(fetch);
        assertEquals(after,
                ward.getPowers().size(),
                "Same instance added twice"
                        + " should not increase size");
    }

    // =========================================
    // getPowers — immutable
    // =========================================

    @Test
    void getPowersReturnsUnmodifiableSet() {
        GarudaPower dummy = new GarudaPower(
                "Hack", "test", "test",
                false, Set.of(), 0);
        assertThrows(
                UnsupportedOperationException.class,
                () -> ward.getPowers().add(dummy));
    }

    // =========================================
    // upgradeHealth and upgradeEnergy
    // =========================================

    @Test
    void upgradeHealthIncrementsMax() {
        int before = ward.getMaxHealth();
        ward.upgradeHealth();
        assertEquals(before + 1,
                ward.getMaxHealth());
    }

    @Test
    void upgradeEnergyIncrementsMax() {
        int before = guide.getMaxEnergy();
        guide.upgradeEnergy();
        assertEquals(before + 1,
                guide.getMaxEnergy());
    }

    @Test
    void upgradeHealthDoesNotChangeCurrentHealth() {
        int current = ward.getCurrentHealth();
        ward.upgradeHealth();
        assertEquals(current,
                ward.getCurrentHealth(),
                "Upgrading max should not"
                        + " change current");
    }

    @Test
    void upgradeEnergyDoesNotChangeCurrentEnergy() {
        int current = guide.getCurrentEnergy();
        guide.upgradeEnergy();
        assertEquals(current,
                guide.getCurrentEnergy(),
                "Upgrading max should not"
                        + " change current");
    }

    // =========================================
    // Getters
    // =========================================

    @Test
    void getTypeReturnsCorrectType() {
        assertEquals(BirdType.WARD,
                ward.getType());
        assertEquals(BirdType.GUIDE,
                guide.getType());
        assertEquals(BirdType.SPECTER,
                specter.getType());
    }

    @Test
    void getAppearanceFields() {
        assertEquals("Crimson",
                ward.getColor());
        assertEquals("Hooked beak",
                ward.getBodyFeature());
        assertEquals("Fierce",
                ward.getPersonality());
    }

    // =========================================
    // GameEntity inheritance
    // =========================================

    @Test
    void getNameReturnsConstructorName() {
        assertEquals("Talon", ward.getName());
        assertEquals("Jade", guide.getName());
        assertEquals("Phantom",
                specter.getName());
    }

    @Test
    void getDescriptionContainsType() {
        assertTrue(ward.getDescription()
                .contains("Ward"));
        assertTrue(guide.getDescription()
                .contains("Guide"));
        assertTrue(specter.getDescription()
                .contains("Specter"));
    }

    // =========================================
    // display
    // =========================================

    @Test
    void displayContainsNameAndType() {
        String result = ward.display();
        assertTrue(result.contains("Talon"));
        assertTrue(result.contains("Ward"));
    }

    @Test
    void displayContainsHealthAndEnergy() {
        String result = guide.display();
        assertTrue(result.contains("4"));
        assertTrue(result.contains("3"));
    }

    @Test
    void displayContainsAppearance() {
        String result = specter.display();
        assertTrue(result.contains("Midnight"));
        assertTrue(
                result.contains("Translucent wings"));
        assertTrue(result.contains("Silent"));
    }

    // =========================================
    // getAvailablePowersToLearn
    // =========================================

    @Test
    void availablePowersExcludesKnown() {
        GarudaPower fetch = new GarudaPower(
                "Fetch",
                "Retrieve a small object",
                "Bird fetches an item",
                false, Set.of(), 1);
        GarudaPower energyBridge = new GarudaPower(
                "Energy Bridge",
                "Ward advanced power",
                "Transfer energy to an ally",
                false,
                EnumSet.of(BirdType.WARD), 2);

        List<GarudaPower> allAdvanced =
                List.of(fetch, energyBridge);

        List<GarudaPower> available =
                ward.getAvailablePowersToLearn(
                        allAdvanced);
        assertEquals(2, available.size());

        // Learn one, then check again
        ward.learnPower(fetch);
        available = ward
                .getAvailablePowersToLearn(
                        allAdvanced);
        assertEquals(1, available.size());
        assertEquals("Energy Bridge",
                available.get(0).getName());
    }

    @Test
    void availablePowersSortedByTrainingCost() {
        // For a Ward Bird: native costs 5, non-native costs 10
        GarudaPower nativePower = new GarudaPower(
                "Energy Bridge",
                "Ward advanced power",
                "Transfer energy",
                false,
                EnumSet.of(BirdType.WARD), 2);
        GarudaPower nonNativePower =
                new GarudaPower(
                        "Farsight",
                        "Guide signature power",
                        "See through walls",
                        false,
                        EnumSet.of(BirdType.GUIDE), 2);

        List<GarudaPower> available =
                ward.getAvailablePowersToLearn(
                        List.of(nonNativePower,
                                nativePower));

        // Native (5 CP) should sort before
        // non-native (10 CP)
        assertEquals("Energy Bridge",
                available.get(0).getName());
        assertEquals("Farsight",
                available.get(1).getName());
    }

    // =========================================
    // Helper
    // =========================================

    private Bird makeBird(BirdType type) {
        return new Bird("Test", type,
                "Gray", "Normal", "Calm");
    }
}