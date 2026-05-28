package darkforge.cli;

import darkforge.persistence.SerializationBenchmark;
import darkforge.crew.Crew;
import darkforge.crew.Vehicle;
import darkforge.exception.*;
import darkforge.facade.FacadeDarkforge;
import darkforge.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Top-level interactive menu loop.
 *
 * Responsibilities:
 * - Own the interactive loop and input validation
 * - Delegate domain operations to FacadeDarkforge sub-facades
 * - Track in-session explorers and crews created/loaded during runtime
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
        this.darkforge = FacadeDarkforge.getTheInstance();

        this.wizard = new ConsoleCreationWizard(scanner);
        this.crewWizard = new ConsoleCrewWizard(scanner);

        this.sessionExplorers = new ArrayList<>();
        this.sessionCrews = new ArrayList<>();
    }

    public void run() {
        System.out.println("\n"
                + darkforge.getVersion()
                + " — Main Menu");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("> ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> handleCreate();
                case "2" -> handleSave();
                case "3" -> handleLoad();
                case "4" -> handleViewAll();
                case "5" -> handleSearch();
                case "6" -> handleDelete();
                case "7" -> handleCrewWizard();
                case "8" -> handleBrowseTalents();

                case "9" -> browseEquipmentCatalog();
                case "10" -> addCatalogItemToExplorer();
                case "11" -> installVehicleModule();
                case "12" -> runSerializationBenchmark();

                case "0", "Q", "q" -> {
                    System.out.println("Farewell, Explorer.");
                    running = false;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    // =========================================
    // Menu display
    // =========================================

    private void printMenu() {
        System.out.println("\n--- Menu ---");
        System.out.println(" 1. Create New Explorer");
        System.out.println(" 2. Save Explorer to File");
        System.out.println(" 3. Load Explorer from File");
        System.out.println(" 4. View All Explorers");
        System.out.println(" 5. Search Explorers");
        System.out.println(" 6. Delete Save File");
        System.out.println(" 7. Create/Manage Crew");
        System.out.println(" 8. Browse Talents");
        System.out.println(" 9.  Browse Equipment Catalog");
        System.out.println(" 10. Add Catalog Item to Explorer");
        System.out.println(" 11. Install Vehicle Module");
        System.out.println(" 12. Serialization Benchmark");
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
            System.out.println("No Explorers to save.");
            return;
        }

        Explorer selected = selectExplorer("save");
        if (selected == null) return;

        try {
            Path path = darkforge.persistenceAccess()
                    .saveExplorer(selected);
            System.out.println("Saved to: " + path);
        } catch (IOException ex) {
            System.out.println("Save failed: " + ex.getMessage());
        }
    }

    // =========================================
    // 3. Load — three-tier catch
    // =========================================

    private void handleLoad() {
        try {
            List<Path> saves = darkforge.persistenceAccess().listSaves();
            if (saves.isEmpty()) {
                System.out.println("No save files found.");
                return;
            }

            System.out.println("\nSaved files:");
            for (int i = 0; i < saves.size(); i++) {
                System.out.printf(" %d. %s%n",
                        i + 1,
                        saves.get(i).getFileName());
            }

            System.out.print("Choose file: ");
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= saves.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Explorer loaded = darkforge.persistenceAccess()
                    .loadExplorer(saves.get(idx));

            sessionExplorers.add(loaded);

            System.out.println("Loaded: " + loaded.getName());
            System.out.println(
                    darkforge.displayAccess()
                            .formatCharacterSheet(loaded)
            );
        } catch (IOException ex) {
            System.out.println("Load failed: " + ex.getMessage());
        } catch (CharacterCorruptionException ex) {
            System.out.println("⚠ " + ex.getUserMessage());
            System.out.println("Choose a different file.");
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
        }
    }

    // =========================================
    // 4. View All
    // =========================================

    private void handleViewAll() {
        if (sessionExplorers.isEmpty()) {
            System.out.println("No Explorers in session.");
            return;
        }

        for (Explorer e : sessionExplorers) {
            System.out.println(
                    darkforge.displayAccess()
                            .formatCharacterSheet(e)
            );
        }
    }

    // =========================================
    // 5. Search
    // =========================================

    private void handleSearch() {
        if (sessionExplorers.isEmpty()) {
            System.out.println("No Explorers in session.");
            return;
        }

        System.out.print("Search name: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("Enter a search term.");
            return;
        }

        List<Explorer> results =
                darkforge.creationAccess()
                        .searchByName(sessionExplorers, query);

        if (results.isEmpty()) {
            System.out.println("No matches.");
            return;
        }

        System.out.printf("%d match(es):%n", results.size());
        for (Explorer e : results) {
            System.out.println(" " + darkforge.displayAccess().formatSummary(e));
        }
    }

    // =========================================
    // 6. Delete
    // =========================================

    private void handleDelete() {
        try {
            List<Path> saves =
                    darkforge.persistenceAccess().listSaves();

            if (saves.isEmpty()) {
                System.out.println("No save files found.");
                return;
            }

            System.out.println("\nSaved files:");
            for (int i = 0; i < saves.size(); i++) {
                System.out.printf(" %d. %s%n",
                        i + 1,
                        saves.get(i).getFileName());
            }

            System.out.print("Choose file to delete: ");
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (idx < 0 || idx >= saves.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            Path target = saves.get(idx);
            System.out.printf("Delete '%s'? (y/n): ",
                    target.getFileName());

            String confirm = scanner.nextLine().trim();
            if (!confirm.equalsIgnoreCase("y")) {
                System.out.println("Deletion cancelled.");
                return;
            }

            boolean deleted = darkforge.persistenceAccess()
                    .deleteExplorer(target);

            if (deleted) {
                System.out.println("Deleted: " + target.getFileName());
            } else {
                System.out.println("File not found.");
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number.");
        }
    }

    // =========================================
    // 7. Create/Manage Crew
    // =========================================

    private void handleCrewWizard() {
        Crew crew = crewWizard.run();
        if (crew == null) return;

        sessionCrews.add(crew);

        for (Explorer m : crew.getMembers()) {
            if (!sessionExplorers.contains(m)) {
                sessionExplorers.add(m);
            }
        }
    }

    // =========================================
    // 8. Browse Talents
    // =========================================

    private void handleBrowseTalents() {
        System.out.println("\n--- Browse Talents ---");
        System.out.println(" 1. Browse by Category");
        System.out.println(" 2. Search by Name");
        System.out.print("> ");

        String choice = scanner.nextLine().trim();
        if (choice.equals("1")) {
            browseTalentsByCategory();
        } else if (choice.equals("2")) {
            searchTalentsByName();
        } else {
            System.out.println("Invalid option.");
        }
    }

    private void browseTalentsByCategory() {
        TalentCategory[] cats = TalentCategory.values();

        System.out.println("\nSelect category:");
        for (int i = 0; i < cats.length; i++) {
            System.out.printf(" %d. %s%n",
                    i + 1,
                    cats[i].name());
        }
        System.out.print("> ");

        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= cats.length) {
                System.out.println("Invalid selection.");
                return;
            }

            List<Talent> talents =
                    darkforge.crewAccess()
                            .browseTalents(cats[idx]);

            if (talents.isEmpty()) {
                System.out.println("No talents in "
                        + cats[idx].name() + ".");
                return;
            }

            System.out.printf("%n%s talents:%n", cats[idx].name());
            for (Talent t : talents) {
                System.out.printf("  • %s (max lvl %d) — %s%n",
                        t.getName(),
                        t.getMaxLevel(),
                        t.getEffect());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    private void searchTalentsByName() {
        System.out.print("Search: ");
        String query = scanner.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("Enter a search term.");
            return;
        }

        List<Talent> results =
                darkforge.crewAccess()
                        .searchTalents(query);

        if (results.isEmpty()) {
            System.out.println("No matches.");
            return;
        }

        System.out.printf("%d result(s):%n", results.size());
        for (Talent t : results) {
            System.out.printf("  • %s [%s] (max lvl %d) — %s%n",
                    t.getName(),
                    t.getCategory().name(),
                    t.getMaxLevel(),
                    t.getEffect());
        }
    }

    // =========================================
    // 9–12. Catalog, vehicles, benchmark
    // =========================================

    /**
     * Launch the interactive equipment catalog browser.
     */
    private void browseEquipmentCatalog() {
        ConsoleCatalogBrowser browser =
                new ConsoleCatalogBrowser(
                        darkforge.catalogAccess(),
                        scanner);
        browser.run();
    }

    /**
     * Search the catalog and add a selected CharacterItem to a selected explorer
     * from a selected crew in the current session.
     */
    private void addCatalogItemToExplorer() {
        Crew crew = selectCrew("modify");
        if (crew == null) return;

        if (crew.getCrewSize() == 0) {
            System.out.println("Crew has no members.");
            return;
        }

        System.out.print("Search catalog: ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) {
            System.out.println("Enter a keyword.");
            return;
        }

        List<Item> items = darkforge.catalogAccess().search(keyword);
        if (items.isEmpty()) {
            System.out.println("No items found.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %d. %s (%d rukh)%n",
                    i + 1,
                    items.get(i).getName(),
                    items.get(i).getCost());
        }

        System.out.print("Select item #: ");
        int idx;
        try {
            idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        if (idx < 0 || idx >= items.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Item selected = items.get(idx);
        if (!(selected instanceof CharacterItem)) {
            System.out.println("Not a character item.");
            return;
        }
        CharacterItem ci = (CharacterItem) selected;

        List<Explorer> members = crew.getMembers();
        for (int i = 0; i < members.size(); i++) {
            System.out.printf("  %d. %s%n",
                    i + 1,
                    members.get(i).getName());
        }

        System.out.print("Select explorer #: ");
        int eIdx;
        try {
            eIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        if (eIdx < 0 || eIdx >= members.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        boolean added = members.get(eIdx).addItem(ci);
        System.out.println(added ? "Item added." : "Could not add item.");
    }

    /**
     * Install a vehicle module from the catalog onto the selected crew's rover or shuttle.
     */
    private void installVehicleModule() {
        Crew crew = selectCrew("upgrade vehicle");
        if (crew == null) return;

        Vehicle rover = crew.getRover();
        Vehicle shuttle = crew.getShuttle();

        if (rover == null && shuttle == null) {
            System.out.println("This crew has no vehicles.");
            return;
        }

        System.out.println("Select vehicle:");
        if (rover != null) {
            System.out.println("  1. " + rover.display());
        }
        if (shuttle != null) {
            System.out.println("  2. " + shuttle.display());
        }
        System.out.print("> ");

        String vChoice = scanner.nextLine().trim();
        Vehicle vehicle;

        if ("1".equals(vChoice) && rover != null) {
            vehicle = rover;
        } else if ("2".equals(vChoice) && shuttle != null) {
            vehicle = shuttle;
        } else {
            System.out.println("Invalid selection.");
            return;
        }

        // Avoid relying on ItemCatalog-specific helper names here:
        // pull modules from the catalog via the facade, then filter by compatibility.
        List<VehicleModule> modules = darkforge.catalogAccess()
                .filterByType(VehicleModule.class);

        List<VehicleModule> compatible = modules.stream()
                .filter(m -> m.isCompatibleWith(vehicle.getType()))
                .toList();

        if (compatible.isEmpty()) {
            System.out.println("No compatible modules found.");
            return;
        }

        for (int i = 0; i < compatible.size(); i++) {
            VehicleModule m = compatible.get(i);
            System.out.printf("  %d. %s (%d CP)%n",
                    i + 1,
                    m.getName(),
                    m.getCpCost());
        }

        System.out.print("Select module #: ");
        int mIdx;
        try {
            mIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }
        if (mIdx < 0 || mIdx >= compatible.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        boolean ok = vehicle.equip(compatible.get(mIdx));
        System.out.println(ok
                ? "Module installed."
                : "Cannot install (incompatible or no slots).");
    }

    /**
     * Run the serialization benchmark entry point.
     */
    private void runSerializationBenchmark() {
        System.out.println("Running serialization benchmark...");
        try {
            SerializationBenchmark.main(new String[]{});
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // =========================================
    // Helpers: selecting session objects
    // =========================================

    private Explorer selectExplorer(String action) {
        System.out.println("\nSelect Explorer to " + action + ":");
        for (int i = 0; i < sessionExplorers.size(); i++) {
            System.out.printf(" %d. %s%n",
                    i + 1,
                    sessionExplorers.get(i).getName());
        }
        System.out.print("> ");

        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= sessionExplorers.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return sessionExplorers.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }

    private Crew selectCrew(String action) {
        if (sessionCrews.isEmpty()) {
            System.out.println("No crews available. Create a crew first.");
            return null;
        }

        System.out.println("\nSelect crew to " + action + ":");
        for (int i = 0; i < sessionCrews.size(); i++) {
            System.out.printf(" %d. %s%n",
                    i + 1,
                    sessionCrews.get(i).getName());
        }
        System.out.print("> ");

        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= sessionCrews.size()) {
                System.out.println("Invalid selection.");
                return null;
            }
            return sessionCrews.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return null;
        }
    }
}