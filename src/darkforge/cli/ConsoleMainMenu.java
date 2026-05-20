package darkforge.cli;

import darkforge.exception.*;
import darkforge.facade.*;
import darkforge.model.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Top-level interactive menu loop that ties all
 * CLI functionality together: create, save, load,
 * search, view, and quit.
 *
 * Demonstrates the full Façade delegation chain:
 * every menu option routes through FacadeDarkforge
 * to the appropriate sub-Façade. Also demonstrates
 * exception handling at the highest level —
 * IOException for file operations and
 * CharacterCorruptionException for corrupted saves
 * are caught here and displayed to the player.
 *
 * Exception handling at the menu level:
 * - handleLoad(): three-tier catch —
 *   IOException (file not readable),
 *   CharacterCorruptionException (file corrupt),
 *   NumberFormatException (bad menu input) —
 *   each with a different recovery message.
 * - handleSave(): catches only IOException —
 *   serialization cannot fail if Explorer valid.
 * - handleCreate(): delegates exception handling
 *   to ConsoleCreationWizard (retry internally).
 */
public class ConsoleMainMenu {
    private final Scanner scanner;
    private final FacadeDarkforge darkforge;
    private final ConsoleCreationWizard wizard;
    private final List<Explorer> sessionExplorers;

    public ConsoleMainMenu(Scanner scanner) {
        this.scanner = scanner;
        this.darkforge =
                FacadeDarkforge.getTheInstance();
        this.wizard =
                new ConsoleCreationWizard(scanner);
        this.sessionExplorers = new ArrayList<>();
    }

    /**
     * Run the main menu loop until the player
     * chooses to quit.
     */
    public void run() {
        System.out.println("\n"
                + darkforge.getVersion()
                + " — Main Menu");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("> ");
            String choice =
                    scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleCreate();
                case "2" -> handleSave();
                case "3" -> handleLoad();
                case "4" -> handleViewAll();
                case "5" -> handleSearch();
                case "6" -> handleDelete();
                case "0", "Q", "q" -> {
                    System.out.println(
                            "Farewell, Explorer.");
                    running = false;
                }
                default -> System.out.println(
                        "Invalid option.");
            }
        }
    }

    // =========================================
    // Menu display
    // =========================================

    private void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println(
                "  1. Create New Explorer");
        System.out.println(
                "  2. Save Explorer to File");
        System.out.println(
                "  3. Load Explorer from File");
        System.out.println(
                "  4. View All Explorers");
        System.out.println(
                "  5. Search Explorers");
        System.out.println(
                "  6. Delete Save File");
        System.out.println("  0. Quit");
    }

    // =========================================
    // 1. Create — delegates to wizard
    // =========================================

    /**
     * Delegate to ConsoleCreationWizard, which
     * handles all exception-driven retry loops
     * internally. If creation succeeds, add to
     * session list.
     */
    private void handleCreate() {
        Explorer e = wizard.run();
        if (e != null) {
            sessionExplorers.add(e);
        }
    }

    // =========================================
    // 2. Save — IOException catch
    // =========================================

    /**
     * Save a session Explorer to a .darkforge.json
     * file. Catches only IOException — the
     * serializer cannot fail if the Explorer is
     * valid (all fields guaranteed by factory).
     */
    private void handleSave() {
        if (sessionExplorers.isEmpty()) {
            System.out.println(
                    "No Explorers to save.");
            return;
        }
        Explorer selected =
                selectExplorer("save");
        if (selected == null) return;

        try {
            Path path =
                    darkforge.persistenceAccess()
                            .saveExplorer(selected);
            System.out.println(
                    "Saved to: " + path);
        } catch (IOException ex) {
            System.out.println(
                    "Save failed: "
                            + ex.getMessage());
        }
    }

    // =========================================
    // 3. Load — three-tier catch pattern
    // =========================================

    /**
     * Load an Explorer from a .darkforge.json
     * file. Demonstrates three-tier catch:
     * - IOException: file not readable
     * - CharacterCorruptionException: file
     *   corrupt (uses getUserMessage())
     * - NumberFormatException: bad menu input
     */
    private void handleLoad() {
        try {
            List<Path> saves =
                    darkforge.persistenceAccess()
                            .listSaves();
            if (saves.isEmpty()) {
                System.out.println(
                        "No save files found.");
                return;
            }

            System.out.println(
                    "\nSaved files:");
            for (int i = 0;
                 i < saves.size(); i++) {
                System.out.printf(
                        "  %d. %s%n",
                        i + 1,
                        saves.get(i)
                                .getFileName());
            }
            System.out.print("Choose file: ");
            int idx = Integer.parseInt(
                    scanner.nextLine().trim()) - 1;

            if (idx < 0
                    || idx >= saves.size()) {
                System.out.println(
                        "Invalid selection.");
                return;
            }

            Explorer loaded =
                    darkforge.persistenceAccess()
                            .loadExplorer(
                                    saves.get(idx));
            sessionExplorers.add(loaded);
            System.out.println(
                    "Loaded: "
                            + loaded.getName());
            System.out.println(
                    darkforge.displayAccess()
                            .formatCharacterSheet(
                                    loaded));

        } catch (IOException ex) {
            System.out.println(
                    "Load failed: "
                            + ex.getMessage());
        } catch (
                CharacterCorruptionException ex) {
            System.out.println(
                    "\u26a0 "
                            + ex.getUserMessage());
            System.out.println(
                    "Choose a different file.");
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Invalid number.");
        }
    }

    // =========================================
    // 4. View All
    // =========================================

    private void handleViewAll() {
        if (sessionExplorers.isEmpty()) {
            System.out.println(
                    "No Explorers in session.");
            return;
        }
        for (Explorer e : sessionExplorers) {
            System.out.println(
                    darkforge.displayAccess()
                            .formatCharacterSheet(e));
        }
    }

    // =========================================
    // 5. Search — name-based search via Façade
    // =========================================

    /**
     * Search session Explorers by name using
     * SearchUtil through the Façade chain.
     * Displays compact summary cards for
     * matching results.
     */
    private void handleSearch() {
        if (sessionExplorers.isEmpty()) {
            System.out.println(
                    "No Explorers in session.");
            return;
        }
        System.out.print("Search name: ");
        String query =
                scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println(
                    "Enter a search term.");
            return;
        }

        List<Explorer> results =
                darkforge.creationAccess()
                        .searchByName(
                                sessionExplorers, query);
        if (results.isEmpty()) {
            System.out.println("No matches.");
        } else {
            System.out.printf(
                    "%d match(es):%n",
                    results.size());
            for (Explorer e : results) {
                System.out.println(
                        "  " + darkforge
                                .displayAccess()
                                .formatSummary(e));
            }
        }
    }

    // =========================================
    // 6. Delete — select, confirm, delete
    // =========================================

    /**
     * List saved files, let the player select
     * one, confirm deletion, then delete.
     * Catches IOException if the directory
     * cannot be listed or the file cannot be
     * deleted.
     */
    private void handleDelete() {
        try {
            List<Path> saves =
                    darkforge.persistenceAccess()
                            .listSaves();
            if (saves.isEmpty()) {
                System.out.println(
                        "No save files found.");
                return;
            }

            System.out.println(
                    "\nSaved files:");
            for (int i = 0;
                 i < saves.size(); i++) {
                System.out.printf(
                        "  %d. %s%n",
                        i + 1,
                        saves.get(i)
                                .getFileName());
            }
            System.out.print(
                    "Choose file to delete: ");
            int idx = Integer.parseInt(
                    scanner.nextLine().trim()) - 1;

            if (idx < 0
                    || idx >= saves.size()) {
                System.out.println(
                        "Invalid selection.");
                return;
            }

            Path target = saves.get(idx);
            System.out.printf(
                    "Delete '%s'? (y/n): ",
                    target.getFileName());
            String confirm =
                    scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("y")) {
                boolean deleted =
                        darkforge.persistenceAccess()
                                .deleteExplorer(target);
                if (deleted) {
                    System.out.println(
                            "Deleted: "
                                    + target.getFileName());
                } else {
                    System.out.println(
                            "File not found.");
                }
            } else {
                System.out.println(
                        "Deletion cancelled.");
            }

        } catch (IOException ex) {
            System.out.println(
                    "Error: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Invalid number.");
        }
    }

    // =========================================
    // Helper — select an Explorer from session
    // =========================================

    /**
     * Display the session Explorer list and
     * prompt the player to select one by number.
     *
     * @param action verb for the prompt
     *        (e.g. "save", "view")
     * @return the selected Explorer, or null
     *         if input is invalid
     */
    private Explorer selectExplorer(
            String action) {
        System.out.println(
                "\nSelect Explorer to "
                        + action + ":");
        for (int i = 0;
             i < sessionExplorers.size();
             i++) {
            System.out.printf("  %d. %s%n",
                    i + 1,
                    sessionExplorers.get(i)
                            .getName());
        }
        System.out.print("> ");
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim()) - 1;
            if (idx < 0
                    || idx
                    >= sessionExplorers
                    .size()) {
                System.out.println(
                        "Invalid selection.");
                return null;
            }
            return sessionExplorers.get(idx);
        } catch (NumberFormatException e) {
            System.out.println(
                    "Invalid number.");
            return null;
        }
    }
}