package darkforge.cli;

import darkforge.data.GameDataProvider;
import darkforge.data.ProfessionData;
import darkforge.exception.*;
import darkforge.facade.*;
import darkforge.model.*;
import java.util.*;

/**
 * Step-by-step interactive console wizard that
 * guides the player through Explorer creation.
 *
 * Wraps FacadeCreation with Scanner-based input
 * collection. Uses exception-driven retry loops:
 * if the player enters an invalid attribute
 * distribution, InvalidAttributeDistribution-
 * Exception is caught, its getUserMessage() is
 * printed, and the prompt repeats.
 *
 * Design decisions:
 * - Scanner.nextLine() exclusively (never
 *   nextInt()) to avoid the classic newline-
 *   leftover bug.
 * - Number-or-name input for professions:
 *   accepts "3" or "Artist".
 * - Auto-generation option for names via
 *   NameGenerator when the player presses Enter.
 * - Façade delegation chain:
 *   CLI → FacadeDarkforge → FacadeCreation →
 *   ExplorerFactory / NameGenerator.
 */
public class ConsoleCreationWizard {
    private final Scanner scanner;
    private final FacadeDarkforge darkforge;

    public ConsoleCreationWizard(Scanner scanner) {
        this.scanner = scanner;
        this.darkforge =
                FacadeDarkforge.getTheInstance();
    }

    /**
     * Run the full Explorer creation wizard.
     * Prompts the player through each step and
     * returns a fully created Explorer, or null
     * if a final creation error occurs.
     *
     * @return the created Explorer, or null on
     *         unrecoverable creation failure
     */
    public Explorer run() {
        printBanner();
        String profession = promptProfession();
        String name = promptName(profession);
        Origin origin = promptOrigin();
        int specialtyIdx =
                promptSpecialty(profession);
        EnumMap<Attribute, Integer> attrs =
                promptAttributes(profession);
        int[] talentPts =
                promptTalentPoints(profession);
        String quirk =
                promptPersonalDetail("quirk");
        String keepsake =
                promptPersonalDetail("keepsake");
        String appearance =
                promptPersonalDetail("appearance");

        try {
            Explorer explorer =
                    darkforge.creationAccess()
                            .createExplorer(
                                    profession, origin,
                                    specialtyIdx, attrs,
                                    talentPts, quirk,
                                    keepsake, appearance,
                                    name);
            System.out.println(
                    "\n=== Explorer Created! ===");
            System.out.println(
                    darkforge.displayAccess()
                            .formatCharacterSheet(
                                    explorer));
            return explorer;
        } catch (DarkForgeException e) {
            System.out.println(
                    "Error: "
                            + e.getUserMessage());
            return null;
        }
    }

    // =========================================
    // Profession prompt — number-or-name input
    // =========================================

    /**
     * Prompt the player to choose a profession
     * by number (1-8) or by name. Loops until
     * a valid choice is entered.
     *
     * Profession list is loaded from
     * professions.json via GameDataProvider —
     * no hardcoding.
     */
    private String promptProfession() {
        List<String> valid =
                darkforge.creationAccess()
                        .getValidProfessions();
        while (true) {
            System.out.println(
                    "\nChoose a profession:");
            for (int i = 0;
                 i < valid.size(); i++) {
                System.out.printf(
                        "  %d. %s%n",
                        i + 1, valid.get(i));
            }
            System.out.print("> ");
            String input =
                    scanner.nextLine().trim();

            // Accept by number
            try {
                int idx =
                        Integer.parseInt(input) - 1;
                if (idx >= 0
                        && idx < valid.size()) {
                    return valid.get(idx);
                }
            } catch (
                    NumberFormatException ignored) {
            }

            // Accept by name (case-insensitive)
            for (String p : valid) {
                if (p.equalsIgnoreCase(input)) {
                    return p;
                }
            }

            System.out.println(
                    "Invalid choice. Enter 1-"
                            + valid.size()
                            + " or profession name.");
        }
    }

    // =========================================
    // Name prompt — auto-generation option
    // =========================================

    /**
     * Prompt for Explorer name. If the player
     * presses Enter without typing a name, a
     * random name is generated via the
     * NameGenerator through the Façade chain.
     */
    private String promptName(String profession) {
        System.out.println(
                "\nEnter name (or Enter to "
                        + "auto-generate):");
        System.out.print("> ");
        String input =
                scanner.nextLine().trim();
        if (input.isEmpty()) {
            String gen =
                    darkforge.creationAccess()
                            .generateName(profession);
            System.out.println(
                    "Generated: " + gen);
            return gen;
        }
        return input;
    }

    // =========================================
    // Origin prompt
    // =========================================

    /**
     * Prompt the player to choose an Origin by
     * number or press Enter for a random D66
     * roll. Origins are loaded from origins.json
     * via GameDataProvider.
     */
    private Origin promptOrigin() {
        List<Origin> origins =
                darkforge.creationAccess()
                        .getOrigins();
        while (true) {
            System.out.println(
                    "\nChoose an origin "
                            + "(or Enter to roll D66):");
            for (int i = 0;
                 i < origins.size(); i++) {
                System.out.printf(
                        "  %d. %s (D66: %d-%d)%n",
                        i + 1,
                        origins.get(i).getLocation(),
                        origins.get(i).getD66RangeLow(),
                        origins.get(i).getD66RangeHigh());
            }
            System.out.print("> ");
            String input =
                    scanner.nextLine().trim();

            // Auto-roll on empty input
            if (input.isEmpty()) {
                int d66Roll =
                        darkforge.mechanicsAccess()
                                .rollD66();
                Origin rolled =
                        GameDataProvider.getInstance()
                                .getOriginByD66(d66Roll);
                System.out.printf(
                        "Rolled D66: %d → %s%n",
                        d66Roll,
                        rolled.getLocation());
                return rolled;
            }

            // Accept by number
            try {
                int idx =
                        Integer.parseInt(input) - 1;
                if (idx >= 0
                        && idx < origins.size()) {
                    return origins.get(idx);
                }
            } catch (
                    NumberFormatException ignored) {
            }

            System.out.println(
                    "Invalid choice. Enter 1-"
                            + origins.size()
                            + " or press Enter to roll.");
        }
    }

    // =========================================
    // Specialty prompt
    // =========================================

    /**
     * Prompt the player to choose a specialty
     * for their profession. Specialties are
     * loaded from professions.json.
     *
     * @param profession the chosen profession
     * @return 0-based specialty index
     */
    private int promptSpecialty(
            String profession) {
        ProfessionData pd =
                GameDataProvider.getInstance()
                        .getProfession(profession);
        List<ProfessionData.SpecialtyData> specs =
                pd.getSpecialties();

        while (true) {
            System.out.println(
                    "\nChoose a specialty:");
            for (int i = 0;
                 i < specs.size(); i++) {
                System.out.printf(
                        "  %d. %s — %s%n",
                        i + 1,
                        specs.get(i).name(),
                        specs.get(i).description());
            }
            System.out.print("> ");
            String input =
                    scanner.nextLine().trim();

            try {
                int idx =
                        Integer.parseInt(input) - 1;
                if (idx >= 0
                        && idx < specs.size()) {
                    return idx;
                }
            } catch (
                    NumberFormatException ignored) {
            }

            // Accept by name (case-insensitive)
            for (int i = 0;
                 i < specs.size(); i++) {
                if (specs.get(i).name()
                        .equalsIgnoreCase(
                                input)) {
                    return i;
                }
            }

            System.out.println(
                    "Invalid choice. Enter 1-"
                            + specs.size()
                            + " or specialty name.");
        }
    }

    // =========================================
    // Attributes — exception-driven retry loop
    // =========================================

    /**
     * Prompt the player to distribute 24
     * attribute points across all attributes.
     * Uses an exception-driven retry loop:
     * if validateAttributes() throws
     * InvalidAttributeDistributionException,
     * getUserMessage() is printed and the
     * prompt repeats.
     *
     * This is the correct use of checked
     * exceptions — the compiler ensures this
     * catch block exists.
     */
    private EnumMap<Attribute, Integer>
    promptAttributes(String profession) {
        Attribute keyAttr =
                getKeyAttribute(profession);
        while (true) {
            System.out.println(
                    "\nDistribute 24 points "
                            + "(min 2, max 5, key "
                            + keyAttr.getDisplayName()
                            + " max 6):");
            EnumMap<Attribute, Integer> attrs =
                    new EnumMap<>(Attribute.class);
            boolean validInput = true;

            for (Attribute a :
                    Attribute.values()) {
                System.out.printf(
                        "  %s (%s): ",
                        a.getDisplayName(),
                        a.getAbbreviation());
                try {
                    attrs.put(a,
                            Integer.parseInt(
                                    scanner.nextLine()
                                            .trim()));
                } catch (
                        NumberFormatException e) {
                    System.out.println(
                            "Enter a number.");
                    validInput = false;
                    break;
                }
            }

            if (!validInput) continue;

            try {
                darkforge.mechanicsAccess()
                        .validateAttributes(
                                attrs, keyAttr);
                return attrs;
            } catch (
                    InvalidAttributeDistributionException
                            e) {
                System.out.println(
                        "\n" + e.getUserMessage());
                System.out.println(
                        "Try again.");
            }
        }
    }

    // =========================================
    // Talent points prompt
    // =========================================

    /**
     * Prompt the player to distribute 3 talent
     * points across the profession's key talents.
     * Uses an exception-driven retry: if
     * IncompatibleTalentException is thrown
     * during the final createExplorer() call,
     * the wizard's run() catches it. Here we
     * do basic total-check locally.
     *
     * @param profession the chosen profession
     * @return int[] of points per key talent
     */
    private int[] promptTalentPoints(
            String profession) {
        ProfessionData pd =
                GameDataProvider.getInstance()
                        .getProfession(profession);
        List<ProfessionData.TalentData> talents =
                pd.getTalents();

        while (true) {
            System.out.println(
                    "\nDistribute 3 talent points "
                            + "across key talents:");
            for (int i = 0;
                 i < talents.size(); i++) {
                System.out.printf(
                        "  %d. %s (max %d) — %s%n",
                        i + 1,
                        talents.get(i).name(),
                        talents.get(i).maxLevel(),
                        talents.get(i).effect());
            }

            int[] points =
                    new int[talents.size()];
            boolean validInput = true;

            for (int i = 0;
                 i < talents.size(); i++) {
                System.out.printf(
                        "  %s points: ",
                        talents.get(i).name());
                try {
                    points[i] =
                            Integer.parseInt(
                                    scanner.nextLine()
                                            .trim());
                } catch (
                        NumberFormatException e) {
                    System.out.println(
                            "Enter a number.");
                    validInput = false;
                    break;
                }
            }

            if (!validInput) continue;

            // Basic local validation: total
            // must be 3 and no negatives
            int total = 0;
            boolean hasNegative = false;
            for (int p : points) {
                total += p;
                if (p < 0) hasNegative = true;
            }

            if (hasNegative) {
                System.out.println(
                        "Points cannot be negative. "
                                + "Try again.");
                continue;
            }
            if (total != 3) {
                System.out.printf(
                        "Total must be 3, got %d. "
                                + "Try again.%n", total);
                continue;
            }

            return points;
        }
    }

    // =========================================
    // Personal detail prompt (quirk, keepsake,
    // appearance)
    // =========================================

    /**
     * Prompt for a personal detail. If the
     * player presses Enter, returns null so
     * ExplorerFactory will auto-roll from the
     * corresponding D66 table.
     *
     * @param detailName the detail type label
     *        (e.g. "quirk", "keepsake")
     * @return the entered text, or null for
     *         auto-roll
     */
    private String promptPersonalDetail(
            String detailName) {
        System.out.printf(
                "%nEnter %s (or Enter to "
                        + "auto-roll from D66 table):%n",
                detailName);
        System.out.print("> ");
        String input =
                scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.printf(
                    "%s will be rolled randomly.%n",
                    capitalize(detailName));
            return null;
        }
        return input;
    }

    // =========================================
    // Helpers
    // =========================================

    /**
     * Resolve the key attribute for a profession
     * from game data. The key attribute varies
     * by profession (Scholar → LOGIC,
     * Enforcer → STRENGTH, etc.) and is defined
     * in professions.json.
     */
    private Attribute getKeyAttribute(
            String profession) {
        ProfessionData pd =
                GameDataProvider.getInstance()
                        .getProfession(profession);
        return pd.getKeyAttribute();
    }

    /**
     * Capitalize the first letter of a string.
     */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase()
                + s.substring(1);
    }

    /**
     * Print the DARKFORGE creation wizard banner.
     */
    private void printBanner() {
        System.out.println(
                "\n" + "=".repeat(50));
        System.out.println(
                "  DARKFORGE v2.0 \u2014 Explorer "
                        + "Creation Wizard");
        System.out.println("=".repeat(50));
    }
}