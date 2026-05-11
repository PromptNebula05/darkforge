package darkforge.mechanics;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class D66TableTest {

  private Map<Integer, String> buildFullMap() {
    Map<Integer, String> map = new HashMap<>();
    for (int tens = 1; tens <= 6; tens++) {
      for (int ones = 1; ones <= 6; ones++) {
        map.put(tens * 10 + ones, "Entry-" + tens + ones);
      }
    }
    return map;
  }

  @Test
  void shouldLookupAllValidKeys() {
    D66Table<String> table = new D66Table<>(buildFullMap(), new Random(42));
    for (int key : D66Table.getValidKeys()) {
      assertNotNull(table.getResult(key));
    }
  }

  @Test
  void shouldRejectInvalidD66Values() {
    D66Table<String> table = new D66Table<>(buildFullMap(), new Random(42));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(17));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(70));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(0));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(-1));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(77));
  }

  @Test
  void shouldRejectIncompleteMap() {
    Map<Integer, String> incomplete = buildFullMap();
    incomplete.remove(34);
    assertThrows(IllegalArgumentException.class, () -> new D66Table<>(incomplete, new Random(42)));
  }

  @Test
  void shouldAlwaysRollValidD66Values() {
    D66Table<String> table = new D66Table<>(buildFullMap(), new Random(42));
    Set<Integer> validKeys = D66Table.getValidKeys();
    for (int i = 0; i < 1000; i++) {
      int result = table.roll();
      assertTrue(validKeys.contains(result),
          "Rolled invalid D66 value: " + result);
    }
  }

  @Test
  void shouldHave36Entries() {
    D66Table<String> table = new D66Table<>(buildFullMap(), new Random(42));
    assertEquals(36, table.size());
  }
}
