package darkforge.mechanics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SelectableTest {

    private D6Table<String> d6Table;
    private D66Table<String> d66Table;

    // =========================================
    // Setup
    // =========================================

    @BeforeEach
    void setUp() {
        Map<Integer, String> d6Map = new HashMap<>();
        for (int i = 1; i <= 6; i++) {
            d6Map.put(i, "D6-" + i);
        }
        d6Table = new D6Table<>(d6Map, new Random(42));

        Map<Integer, String> d66Map = new HashMap<>();
        for (int tens = 1; tens <= 6; tens++) {
            for (int ones = 1; ones <= 6; ones++) {
                d66Map.put(tens * 10 + ones,
                        "D66-" + tens + ones);
            }
        }
        d66Table = new D66Table<>(d66Map, new Random(42));
    }

    // =========================================
    // D6Table implements Selectable<T>
    // =========================================

    @Test
    void d6TableAssignableToSelectable() {
        Selectable<String> selectable = d6Table;
        assertNotNull(selectable);
    }

    @Test
    void d6TableSelectReturnsCorrectValue() {
        Selectable<String> selectable = d6Table;
        assertEquals("D6-3", selectable.select(3));
        assertEquals("D6-1", selectable.select(1));
        assertEquals("D6-6", selectable.select(6));
    }

    @Test
    void d6TableSelectDelegatesToGetResult() {
        for (int i = 1; i <= 6; i++) {
            assertEquals(d6Table.getResult(i),
                    d6Table.select(i));
        }
    }

    // =========================================
    // D66Table implements Selectable<T>
    // =========================================

    @Test
    void d66TableAssignableToSelectable() {
        Selectable<String> selectable = d66Table;
        assertNotNull(selectable);
    }

    @Test
    void d66TableSelectReturnsCorrectValue() {
        Selectable<String> selectable = d66Table;
        assertEquals("D66-11", selectable.select(11));
        assertEquals("D66-34", selectable.select(34));
        assertEquals("D66-66", selectable.select(66));
    }

    @Test
    void d66TableSelectDelegatesToGetResult() {
        for (int key : D66Table.getValidKeys()) {
            assertEquals(d66Table.getResult(key),
                    d66Table.select(key));
        }
    }

    // =========================================
    // Polymorphic dispatch
    // =========================================

    @Test
    void polymorphicDispatchWithD6Table() {
        String result = selectFromTable(d6Table, 4);
        assertEquals("D6-4", result);
    }

    @Test
    void polymorphicDispatchWithD66Table() {
        String result = selectFromTable(d66Table, 25);
        assertEquals("D66-25", result);
    }

    @Test
    void polymorphicDispatchBothInSameMethod() {
        List<Selectable<String>> tables =
                List.of(d6Table, d66Table);

        // Both can be stored and iterated as Selectable<String>
        assertEquals(2, tables.size());
        assertNotNull(tables.get(0).select(1));
        assertNotNull(tables.get(1).select(11));
    }

    // Helper: accepts any Selectable<String>
    private String selectFromTable(
            Selectable<String> table, int value) {
        return table.select(value);
    }

    // =========================================
    // allValues()
    // =========================================

    @Test
    void d6TableAllValuesReturns6() {
        Collection<String> values =
                d6Table.allValues();
        assertEquals(6, values.size());
    }

    @Test
    void d66TableAllValuesReturns36() {
        Collection<String> values =
                d66Table.allValues();
        assertEquals(36, values.size());
    }

    @Test
    void d6TableAllValuesContainsAllEntries() {
        Collection<String> values =
                d6Table.allValues();
        for (int i = 1; i <= 6; i++) {
            assertTrue(values.contains("D6-" + i));
        }
    }

    @Test
    void d66TableAllValuesContainsAllEntries() {
        Collection<String> values =
                d66Table.allValues();
        for (int tens = 1; tens <= 6; tens++) {
            for (int ones = 1; ones <= 6; ones++) {
                assertTrue(values.contains(
                        "D66-" + tens + ones));
            }
        }
    }

    @Test
    void d6TableAllValuesIsUnmodifiable() {
        Collection<String> values =
                d6Table.allValues();
        assertThrows(
                UnsupportedOperationException.class,
                () -> values.add("hack"));
    }

    @Test
    void d66TableAllValuesIsUnmodifiable() {
        Collection<String> values =
                d66Table.allValues();
        assertThrows(
                UnsupportedOperationException.class,
                () -> values.add("hack"));
    }

    // =========================================
    // selectRandom(Random) — deterministic
    // =========================================

    @Test
    void d6TableSelectRandomWithSeedIsDeterministic() {
        long seed = 99L;
        String first =
                d6Table.selectRandom(new Random(seed));
        String second =
                d6Table.selectRandom(new Random(seed));
        assertEquals(first, second,
                "Same seed must produce same result");
    }

    @Test
    void d66TableSelectRandomWithSeedIsDeterministic() {
        long seed = 99L;
        String first =
                d66Table.selectRandom(new Random(seed));
        String second =
                d66Table.selectRandom(new Random(seed));
        assertEquals(first, second,
                "Same seed must produce same result");
    }

    @Test
    void d6TableSelectRandomReturnsValidEntry() {
        for (int i = 0; i < 100; i++) {
            String result =
                    d6Table.selectRandom(new Random(i));
            assertTrue(
                    d6Table.allValues().contains(result),
                    "selectRandom returned unknown value: "
                            + result);
        }
    }

    @Test
    void d66TableSelectRandomReturnsValidEntry() {
        for (int i = 0; i < 100; i++) {
            String result =
                    d66Table.selectRandom(new Random(i));
            assertTrue(
                    d66Table.allValues().contains(result),
                    "selectRandom returned unknown value: "
                            + result);
        }
    }

    // =========================================
    // selectRandom() (no-arg) returns valid
    // =========================================

    @Test
    void d6TableSelectRandomNoArgReturnsValidEntry() {
        for (int i = 0; i < 50; i++) {
            String result = d6Table.selectRandom();
            assertTrue(
                    d6Table.allValues().contains(result));
        }
    }

    @Test
    void d66TableSelectRandomNoArgReturnsValidEntry() {
        for (int i = 0; i < 50; i++) {
            String result = d66Table.selectRandom();
            assertTrue(
                    d66Table.allValues().contains(result));
        }
    }

    // =========================================
    // Both still implement Rollable (regression)
    // =========================================

    @Test
    void d6TableStillImplementsRollable() {
        assertInstanceOf(Rollable.class, d6Table);
    }

    @Test
    void d66TableStillImplementsRollable() {
        assertInstanceOf(Rollable.class, d66Table);
    }
}