package darkforge.cli;

import darkforge.crew.Crew;
import darkforge.exception.*;
import darkforge.facade.*;
import darkforge.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Top-level interactive menu loop. Delegates all
 * operations through FacadeDarkforge to the
 * appropriate sub-Façade.
 */
public class ConsoleMainMenu {
    private final Scanner scanner;
    private final FacadeDarkforge darkforge;
    private final ConsoleCreationWizard wizard;
    private final ConsoleCrewWizard crewWizard;
    private final List<Explorer> sessionExplorers;
    private final List<Crew> sessionCrews;

    public ConsoleMainMenu(Scanner scanner) {
        this.scanner = scanner;
        this.darkforge =
                FacadeDarkforge.getTheInstance();
        this.wizard =
                new ConsoleCreationWizard(scanner);
        this.crewWizard =
                new ConsoleCrewWizard(scanner);
        this.sessionExplorers =
                new ArrayList<>();
        this.sessionCrews =
                new ArrayList<>();
    }

    public void run() {
        System.out.println("\n"
                + darkforge.getVersion()
                + " \u2014 Main Menu");

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
                case "7" ->
                        handleCrewWizard();
                case "8" ->
                        handleBrowseTalents();
                case "0", "Q", "q" -> {
                    System.out.println(
                            "Farewell,"
                                    + " Explorer.");
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
                " 1. Create New Explorer");
        System.out.println(
                " 2. Save Explorer to File");
        System.out.println(
                " 3. Load Explorer from File");
        System.out.println(
                " 4. View All Explorers");
        System.out.println(
                " 5. Search Explorers");
        System.out.println(
                " 6. Delete Save File");
        System.out.println(
                " 7. Create/Manage Crew");
        System.out.println(
                " 8. Browse Talents");
        System.out.println(" 0. Quit");
    }

    // =========================================
    // 1. Create
    // =========================================

    private void handleCreate() {
        Explorer e = wizard.run();
        if (e != null) {
            sessionExplorers.add(e);
        }
    }

    // =========================================
    // 2. Save
    // =========================================

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
    // 3. Load — three-tier catch
    // =========================================

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
                        " %d. %s%n",
                        i + 1,
                        saves.get(i)
                                .getFileName());
            }
            System.out.print(
                    "Choose file: ");
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;

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
                CharacterCorruptionException
                        ex) {
            System.out.println(
                    "\u26a0 "
                            + ex.getUserMessage());
            System.out.println(
                    "Choose a different file.");
        } catch (
                NumberFormatException ex) {
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
    // 5. Search
    // =========================================

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
                                sessionExplorers,
                                query);
        if (results.isEmpty()) {
            System.out.println(
                    "No matches.");
        } else {
            System.out.printf(
                    "%d match(es):%n",
                    results.size());
            for (Explorer e : results) {
                System.out.println(
                        " " + darkforge
                                .displayAccess()
                                .formatSummary(e));
            }
        }
    }

    // =========================================
    // 6. Delete
    // =========================================

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
                        " %d. %s%n",
                        i + 1,
                        saves.get(i)
                                .getFileName());
            }
            System.out.print(
                    "Choose file to delete: ");
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;

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

            if (confirm
                    .equalsIgnoreCase("y")) {
                boolean deleted =
                        darkforge
                                .persistenceAccess()
                                .deleteExplorer(
                                        target);
                if (deleted) {
                    System.out.println(
                            "Deleted: "
                                    + target
                                    .getFileName());
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
                    "Error: "
                            + ex.getMessage());
        } catch (
                NumberFormatException ex) {
            System.out.println(
                    "Invalid number.");
        }
    }

    // =========================================
    // 7. Create/Manage Crew
    // =========================================

    private void handleCrewWizard() {
        Crew crew = crewWizard.run();
        if (crew != null) {
            sessionCrews.add(crew);
            for (Explorer m
                    : crew.getMembers()) {
                if (!sessionExplorers
                        .contains(m)) {
                    sessionExplorers.add(m);
                }
            }
        }
    }

    // =========================================
    // 8. Browse Talents
    // =========================================

    private void handleBrowseTalents() {
        System.out.println(
                "\n--- Browse Talents ---");
        System.out.println(
                " 1. Browse by Category");
        System.out.println(
                " 2. Search by Name");
        System.out.print("> ");
        String choice =
                scanner.nextLine().trim();

        if (choice.equals("1")) {
            browseTalentsByCategory();
        } else if (choice.equals("2")) {
            searchTalentsByName();
        } else {
            System.out.println(
                    "Invalid option.");
        }
    }

    private void browseTalentsByCategory() {
        TalentCategory[] cats =
                TalentCategory.values();
        System.out.println(
                "\nSelect category:");
        for (int i = 0;
             i < cats.length; i++) {
            System.out.printf(
                    " %d. %s%n",
                    i + 1, cats[i].name());
        }
        System.out.print("> ");
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (idx < 0
                    || idx >= cats.length) {
                System.out.println(
                        "Invalid selection.");
                return;
            }
            List<Talent> talents =
                    darkforge.crewAccess()
                            .browseTalents(
                                    cats[idx]);
            if (talents.isEmpty()) {
                System.out.println(
                        "No talents in "
                                + cats[idx].name()
                                + ".");
            } else {
                System.out.printf(
                        "%n%s talents:%n",
                        cats[idx].name());
                for (Talent t : talents) {
                    System.out.printf(
                            "  \u2022 %s"
                                    + " (max lvl %d)"
                                    + " \u2014 %s%n",
                            t.getName(),
                            t.getMaxLevel(),
                            t.getEffect());
                }
            }
        } catch (
                NumberFormatException e) {
            System.out.println(
                    "Invalid number.");
        }
    }

    private void searchTalentsByName() {
        System.out.print("Search: ");
        String query =
                scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println(
                    "Enter a search term.");
            return;
        }
        List<Talent> results =
                darkforge.crewAccess()
                        .searchTalents(query);
        if (results.isEmpty()) {
            System.out.println(
                    "No matches.");
        } else {
            System.out.printf(
                    "%d result(s):%n",
                    results.size());
            for (Talent t : results) {
                System.out.printf(
                        "  \u2022 %s [%s]"
                                + " (max lvl %d)"
                                + " \u2014 %s%n",
                        t.getName(),
                        t.getCategory().name(),
                        t.getMaxLevel(),
                        t.getEffect());
            }
        }
    }

    // =========================================
    // Helper — select Explorer from session
    // =========================================

    private Explorer selectExplorer(
            String action) {
        System.out.println(
                "\nSelect Explorer to "
                        + action + ":");
        for (int i = 0;
             i < sessionExplorers.size();
             i++) {
            System.out.printf(" %d. %s%n",
                    i + 1,
                    sessionExplorers.get(i)
                            .getName());
        }
        System.out.print("> ");
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (idx < 0
                    || idx
                    >= sessionExplorers
                    .size()) {
                System.out.println(
                        "Invalid selection.");
                return null;
            }
            return sessionExplorers.get(idx);
        } catch (
                NumberFormatException e) {
            System.out.println(
                    "Invalid number.");
            return null;
        }
    }
}