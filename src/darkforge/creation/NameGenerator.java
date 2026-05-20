package darkforge.creation;

import darkforge.data.GameDataProvider;
import darkforge.mechanics.D66Table;
import java.util.*;

/**
 * Generates random Explorer names from profession-
 * specific D66 name tables (Coriolis Ch. 2).
 * Name data loaded from JSON via GameDataProvider.
 */
public class NameGenerator {
    private final Map<String, D66Table<String>>
            firstNameTables;
    private final Map<String, D66Table<String>>
            lastNameTables;
    private final Random rng;
    private final GameDataProvider data;

    public NameGenerator(Random rng,
                         GameDataProvider data) {
        this.rng = rng;
        this.data = data;
        this.firstNameTables = new HashMap<>();
        this.lastNameTables = new HashMap<>();
        initializeTables();
    }

    /**
     * Generate a random full name for a profession.
     * @param profession the profession name
     * @return formatted "Firstname Lastname" string
     */
    public String generateName(String profession) {
        D66Table<String> firstTable =
                firstNameTables.get(
                        profession.toLowerCase());
        D66Table<String> lastTable =
                lastNameTables.get(
                        profession.toLowerCase());
        if (firstTable == null
                || lastTable == null) {
            return "Unknown Explorer";
        }

        String first = rollOnTable(firstTable);
        String last = rollOnTable(lastTable);

        return formatName(first)
                + " " + formatName(last);
    }

    /** Roll on a D66 table and return the result. */
    private <T> T rollOnTable(D66Table<T> table) {
        table.roll();
        return table.getResult(
                table.getLastRollValue());
    }

    /**
     * Generate multiple name suggestions.
     * @param profession the profession name
     * @param count number of names to generate
     * @return list of formatted names
     */
    public List<String> generateNames(
            String profession, int count) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            names.add(generateName(profession));
        }
        return names;
    }

    /**
     * Proper-case a name: first letter uppercase,
     * rest as-is. Handles hyphenated names
     * ("al-farouk" → "Al-Farouk").
     */
    private String formatName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        String trimmed = name.trim();
        if (trimmed.contains("-")) {
            String[] parts = trimmed.split("-");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append("-");
                sb.append(capitalize(parts[i]));
            }
            return sb.toString();
        }
        return capitalize(trimmed);
    }

    private String capitalize(String s) {
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase()
                + s.substring(1);
    }

    /**
     * Populate name tables from Coriolis Ch. 2 data.
     * Each profession has 36 first names and
     * 36 last names indexed by D66.
     */
    private void initializeTables() {
        // Name tables loaded from JSON resources
        // (names/*.json) via GameDataProvider.
        // Each profession has 36 first names and
        // 36 last names indexed by D66.
        for (String prof :
                data.getValidProfessionNames()) {
            String key = prof.toLowerCase();
            Map<Integer, String> firstNames =
                    data.getFirstNames(prof);
            Map<Integer, String> lastNames =
                    data.getLastNames(prof);
            if (firstNames != null
                    && !firstNames.isEmpty()) {
                firstNameTables.put(key,
                        new D66Table<>(firstNames));
            }
            if (lastNames != null
                    && !lastNames.isEmpty()) {
                lastNameTables.put(key,
                        new D66Table<>(lastNames));
            }
        }
    }
}