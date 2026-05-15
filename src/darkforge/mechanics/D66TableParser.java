package darkforge.mechanics;

import java.util.*;
import java.util.regex.*;

/**
 * Parses text-format D66 tables (as they appear in
 * the Coriolis rulebook) into key-value maps.
 *
 * Each line: "D66_VALUE  ENTRY_TEXT" or
 * "D66_LOW-D66_HIGH  ENTRY_TEXT" for ranges.
 *
 */
public class D66TableParser {
    private D66TableParser() {}

    private static final Pattern LINE_PATTERN =
            Pattern.compile(
                    "^(\\d{2})(?:-(\\d{2}))?\\s+(.+)$");

    /**
     * Parse text-format D66 table into a map.
     * @param tableText multiline string with one
     *        entry per line
     * @return map of D66 values to entry text
     * @throws IllegalArgumentException if a line
     *         cannot be parsed
     */
    public static Map<Integer, String> parse(
            String tableText) {
        Map<Integer, String> result = new HashMap<>();
        String[] lines = tableText.trim()
                .split("\\n");

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()
                    || trimmed.startsWith("#")) {
                continue; // skip blanks + comments
            }

            Matcher m =
                    LINE_PATTERN.matcher(trimmed);
            if (!m.matches()) {
                throw new IllegalArgumentException(
                        "Cannot parse D66 line: "
                                + trimmed);
            }

            int low = Integer.parseInt(m.group(1));
            String value = m.group(3).trim();

            if (m.group(2) != null) {
                int high =
                        Integer.parseInt(m.group(2));
                for (int d66 = low;
                     d66 <= high; d66++) {
                    if (isValidD66(d66)) {
                        result.put(d66, value);
                    }
                }
            } else {
                result.put(low, value);
            }
        }
        return result;
    }

    /**
     * Check if a value is a valid D66 result
     * (both digits 1-6).
     */
    private static boolean isValidD66(int value) {
        int tens = value / 10;
        int ones = value % 10;
        return tens >= 1 && tens <= 6
                && ones >= 1 && ones <= 6;
    }
}