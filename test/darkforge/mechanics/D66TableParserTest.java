package darkforge.mechanics;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for D66TableParser text-to-map parsing.
 * Verifies single entries, ranges, blank/comment skipping,
 * and rejection of invalid format lines.
 */
class D66TableParserTest {

    @Test
    void shouldParseSingleEntry() {
        Map<Integer, String> result =
                D66TableParser.parse("11  First Entry");
        assertEquals("First Entry", result.get(11));
    }

    @Test
    void shouldParseRange() {
        Map<Integer, String> result =
                D66TableParser.parse("11-12  Shared Entry");
        assertEquals("Shared Entry", result.get(11));
        assertEquals("Shared Entry", result.get(12));
    }

    @Test
    void shouldParseMultipleLines() {
        String input = "11  Entry A\n12  Entry B\n13  Entry C";
        Map<Integer, String> result = D66TableParser.parse(input);
        assertEquals(3, result.size());
        assertEquals("Entry A", result.get(11));
        assertEquals("Entry B", result.get(12));
        assertEquals("Entry C", result.get(13));
    }

    @Test
    void shouldSkipBlankLines() {
        String input = "11  Entry A\n\n12  Entry B";
        Map<Integer, String> result = D66TableParser.parse(input);
        assertEquals(2, result.size());
    }

    @Test
    void shouldSkipCommentLines() {
        String input = "# Comment\n11  Entry A\n# Another\n12  Entry B";
        Map<Integer, String> result = D66TableParser.parse(input);
        assertEquals(2, result.size());
    }

    @Test
    void shouldRejectInvalidFormatLine() {
        assertThrows(IllegalArgumentException.class, () ->
                D66TableParser.parse("This is not a valid line"));
    }

    @Test
    void shouldTrimEntryValues() {
        Map<Integer, String> result =
                D66TableParser.parse("11    Trimmed Entry   ");
        assertEquals("Trimmed Entry", result.get(11));
    }

    @Test
    void shouldParseFullD66Table() {
        StringBuilder sb = new StringBuilder();
        for (int tens = 1; tens <= 6; tens++) {
            for (int ones = 1; ones <= 6; ones++) {
                int key = tens * 10 + ones;
                sb.append(key).append("  Entry-")
                        .append(key).append("\n");
            }
        }
        Map<Integer, String> result =
                D66TableParser.parse(sb.toString());
        assertEquals(36, result.size());
    }

    @Test
    void shouldSkipInvalidD66ValuesInRange() {
        Map<Integer, String> result =
                D66TableParser.parse("15-22  Range Entry");
        assertTrue(result.containsKey(15));
        assertTrue(result.containsKey(16));
        assertTrue(result.containsKey(21));
        assertTrue(result.containsKey(22));
        assertFalse(result.containsKey(17),
                "17 is not a valid D66 value");
        assertFalse(result.containsKey(20),
                "20 is not a valid D66 value");
    }

    @Test
    void shouldHandleEntriesWithSpecialCharacters() {
        Map<Integer, String> result =
                D66TableParser.parse(
                        "11  Among the Hulks & Wrecks");
        assertTrue(result.get(11).contains("Hulks"));
    }
}