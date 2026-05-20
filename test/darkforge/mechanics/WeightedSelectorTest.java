package darkforge.mechanics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WeightedSelectorTest {

    private WeightedSelector<String> selector;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        // Weights: A=1.0 (10%), B=2.0 (20%), C=7.0 (70%)
        Map<String, Double> weights =
                new LinkedHashMap<>();
        weights.put("A", 1.0);
        weights.put("B", 2.0);
        weights.put("C", 7.0);
        selector = new WeightedSelector<>(weights);
    }

    // =========================================
    // Selectable<T> compliance
    // =========================================

    @Test
    void implementsSelectable() {
        assertInstanceOf(Selectable.class, selector);
    }

    @Test
    void assignableToSelectableReference() {
        Selectable<String> ref = selector;
        assertNotNull(ref);
        assertNotNull(ref.selectRandom(new Random(42)));
    }

    // =========================================
    // Does NOT implement Rollable
    // =========================================

    @Test
    void doesNotImplementRollable() {
        assertFalse(selector instanceof Rollable,
                "WeightedSelector must not implement Rollable "
                        + "\u2014 it samples from a probability "
                        + "distribution, not a dice roll");
    }

    // =========================================
    // size and allValues
    // =========================================

    @Test
    void sizeReturnsItemCount() {
        assertEquals(3, selector.size());
    }

    @Test
    void allValuesReturnsAllItems() {
        Collection<String> values =
                selector.allValues();
        assertEquals(3, values.size());
        assertTrue(values.contains("A"));
        assertTrue(values.contains("B"));
        assertTrue(values.contains("C"));
    }

    @Test
    void allValuesIsUnmodifiable() {
        Collection<String> values =
                selector.allValues();
        assertThrows(
                UnsupportedOperationException.class,
                () -> values.add("hack"));
    }

    // =========================================
    // select(int) — deterministic
    // =========================================

    @Test
    void selectSameValueAlwaysReturnsSameItem() {
        String first = selector.select(50);
        String second = selector.select(50);
        assertEquals(first, second);
    }

    @Test
    void selectReturnsValidItem() {
        Collection<String> valid =
                selector.allValues();
        for (int v = 0; v <= 100; v += 5) {
            assertTrue(valid.contains(
                            selector.select(v)),
                    "select(" + v
                            + ") returned unknown value");
        }
    }

    // =========================================
    // selectRandom(Random) — deterministic seed
    // =========================================

    @Test
    void selectRandomWithSeedIsDeterministic() {
        long seed = 42L;
        String first =
                selector.selectRandom(new Random(seed));
        String second =
                selector.selectRandom(new Random(seed));
        assertEquals(first, second,
                "Same seed must produce same result");
    }

    @Test
    void selectRandomReturnsValidItem() {
        Collection<String> valid =
                selector.allValues();
        for (int i = 0; i < 100; i++) {
            String result =
                    selector.selectRandom(new Random(i));
            assertTrue(valid.contains(result),
                    "selectRandom returned unknown: "
                            + result);
        }
    }

    // =========================================
    // Statistical distribution (10K runs)
    // =========================================

    @Test
    void distributionWithinExpectedRange() {
        // A=10%, B=20%, C=70% with \u00b15% tolerance
        int runs = 10_000;
        Map<String, Integer> counts =
                new HashMap<>();
        counts.put("A", 0);
        counts.put("B", 0);
        counts.put("C", 0);

        Random rng = new Random(12345L);
        for (int i = 0; i < runs; i++) {
            String result =
                    selector.selectRandom(rng);
            counts.merge(result, 1, Integer::sum);
        }

        double aPercent =
                counts.get("A") / (double) runs;
        double bPercent =
                counts.get("B") / (double) runs;
        double cPercent =
                counts.get("C") / (double) runs;

        assertTrue(aPercent > 0.05 && aPercent < 0.15,
                "A expected ~10%, got "
                        + (aPercent * 100) + "%");
        assertTrue(bPercent > 0.15 && bPercent < 0.25,
                "B expected ~20%, got "
                        + (bPercent * 100) + "%");
        assertTrue(cPercent > 0.65 && cPercent < 0.75,
                "C expected ~70%, got "
                        + (cPercent * 100) + "%");
    }

    // =========================================
    // Single—item selector
    // =========================================

    @Test
    void singleItemAlwaysReturnsThatItem() {
        Map<String, Double> single =
                Map.of("Only", 1.0);
        WeightedSelector<String> solo =
                new WeightedSelector<>(single);

        assertEquals(1, solo.size());
        for (int i = 0; i < 50; i++) {
            assertEquals("Only",
                    solo.selectRandom(new Random(i)));
        }
    }

    // =========================================
    // Polymorphic dispatch
    // =========================================

    @Test
    void polymorphicDispatchWithD6AndWeighted() {
        Map<Integer, String> d6Map = new HashMap<>();
        for (int i = 1; i <= 6; i++) {
            d6Map.put(i, "D6—" + i);
        }
        Selectable<String> d6 =
                new D6Table<>(d6Map, new Random(42));
        Selectable<String> weighted = selector;

        // Both stored as Selectable<String>
        List<Selectable<String>> tables =
                List.of(d6, weighted);
        assertEquals(2, tables.size());

        // Both respond to selectRandom
        for (Selectable<String> table : tables) {
            assertNotNull(table.selectRandom(
                    new Random(1)));
        }
    }

    // =========================================
    // selectRandom() no—arg returns valid
    // =========================================

    @Test
    void selectRandomNoArgReturnsValidItem() {
        Collection<String> valid =
                selector.allValues();
        for (int i = 0; i < 50; i++) {
            assertTrue(valid.contains(
                    selector.selectRandom()));
        }
    }
}