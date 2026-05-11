package darkforge.mechanics;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class D6TableTest {

  private Map<Integer, String> buildFullMap() {
    Map<Integer, String> map = new HashMap<>();
    for (int i = 1; i <= 6; i++) {
      map.put(i, "Entry-" + i);
    }
    return map;
  }

  @Test
  void shouldLookupAllValidKeys() {
    D6Table<String> table = new D6Table<>(buildFullMap(), new Random(42));
    for (int i = 1; i <= 6; i++) {
      assertEquals("Entry-" + i, table.getResult(i));
    }
  }

  @Test
  void shouldRejectInvalidValues() {
    D6Table<String> table = new D6Table<>(buildFullMap(), new Random(42));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(0));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(7));
    assertThrows(IllegalArgumentException.class, () -> table.getResult(-1));
  }

  @Test
  void shouldRejectIncompleteMap() {
    Map<Integer, String> incomplete = buildFullMap();
    incomplete.remove(3);
    assertThrows(IllegalArgumentException.class, () -> new D6Table<>(incomplete, new Random(42)));
  }

  @Test
  void shouldAlwaysRollValidValues() {
    D6Table<String> table = new D6Table<>(buildFullMap(), new Random(42));
    for (int i = 0; i < 1000; i++) {
      int result = table.roll();
      assertTrue(result >= 1 && result <= 6,
          "Rolled invalid value: " + result);
    }
  }

  @Test
  void shouldHave6Entries() {
    D6Table<String> table = new D6Table<>(buildFullMap(), new Random(42));
    assertEquals(6, table.size());
  }
}
