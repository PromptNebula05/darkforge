package darkforge.cli;

import darkforge.crew.*;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.collection.EquipmentInventory;
import darkforge.collection.Inventory;
import darkforge.facade.FacadeDarkforge;
import darkforge.model.Equipment;
import darkforge.model.EquipmentWeight;
import darkforge.model.Explorer;
import darkforge.model.GameEntity;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Interactive console interface for crew
 * assembly, loading, and management.
 * Mirrors ConsoleCreationWizard's Scanner-based
 * pattern with Façade delegation.
 */
public class ConsoleCrewWizard {
    private final Scanner scanner;
    private final FacadeDarkforge darkforge;
    private final ConsoleCreationWizard
            creationWizard;
    private Crew activeCrew;

    public ConsoleCrewWizard(Scanner scanner) {
        this.scanner = scanner;
        this.darkforge =
                FacadeDarkforge.getTheInstance();
        this.creationWizard =
                new ConsoleCreationWizard(scanner);
    }

    /**
     * Run the crew management menu.
     * @return the active Crew when exiting,
     *     or null if none loaded
     */
    public Crew run() {
        printBanner();

        boolean running = true;
        while (running) {
            try {
                printCrewMenu();
                String choice =
                        scanner.nextLine().trim();
                switch (choice) {
                    case "1" -> createNewCrew();
                    case "2" -> loadCrewFromFile();
                    case "3" -> viewActiveCrew();
                    case "4" -> manageRoster();
                    case "5" -> manageBird();
                    case "6" -> manageVehicles();
                    case "7" -> saveActiveCrew();
                    case "0" -> running = false;
                    default -> System.out.println(
                            "Invalid option.");
                }
            } catch (
                    NoSuchElementException e) {
                running = false;
            }
        }
        return activeCrew;
    }
    /**
     * Test-friendly creation entry point.
     *
     * Runs the banner + new-crew creation flow directly, bypassing the
     * management menu. Returns the resulting Crew (or null if creation
     * was aborted or input was exhausted).
     *
     * The full management menu remains available via {@link #run()}
     * for the interactive console UI.
     */
    public Crew runCreate() {
        printBanner();
        createNewCrew();
        return activeCrew;
    }

    // =========================================
    // Crew management menu
    // =========================================

    private void printCrewMenu() {
        System.out.println(
                "\n=== Crew Management ===");
        String status = (activeCrew != null)
                ? "Active: " + activeCrew.getName()
                  + " (" + activeCrew.getCrewSize()
                  + " members)"
                : "No crew loaded";
        System.out.println(
                "  [" + status + "]");
        System.out.println(
                " 1. Create New Crew");
        System.out.println(
                " 2. Load Saved Crew");
        System.out.println(
                " 3. View Current Crew");
        System.out.println(
                " 4. Manage Roster");
        System.out.println(
                " 5. Manage Bird");
        System.out.println(
                " 6. Manage Vehicles");
        System.out.println(
                " 7. Save Current Crew");
        System.out.println(
                " 0. Back to Main Menu");
        System.out.print("> ");
    }

    // =========================================
    // Option 1: Create new crew
    // =========================================

    private void createNewCrew() {
        try {
            String name = promptCrewName();
            List<Explorer> members =
                    promptMembers();
            if (members.size() < 4) {
                System.out.println(
                        "Need at least 4 members."
                                + " Aborting.");
                return;
            }

            Crew crew = new Crew(name);
            for (Explorer m : members) {
                crew.addMember(m);
            }

            promptRoleAssignment(crew);

            Bird bird = promptBird();
            crew.setBird(bird);

            Vehicle shuttle =
                    promptVehicleChoice(
                            VehicleType.Category
                                    .SHUTTLE);
            if (shuttle != null) {
                shuttle =
                        promptVehicleCustomize(
                                shuttle);
                crew.setShuttle(shuttle);
            }

            Vehicle rover =
                    promptVehicleChoice(
                            VehicleType.Category
                                    .ROVER);
            if (rover != null) {
                rover =
                        promptVehicleCustomize(
                                rover);
                crew.setRover(rover);
            }

            promptSupply(crew);
            reviewCrew(crew);

            activeCrew = crew;
            System.out.println(
                    "\n✓ Crew '" + crew.getName()
                            + "' is now the active crew.");
            promptSave(crew);

        } catch (
                NoSuchElementException e) {
            System.out.println(
                    "Input ended. Returning"
                            + " to menu.");
        }
    }

    // =========================================
    // Option 2: Load crew from file
    // =========================================

    private void loadCrewFromFile() {
        try {
            List<Path> saves =
                    darkforge.crewAccess()
                            .listCrewSaves();
            if (saves.isEmpty()) {
                System.out.println(
                        "\nNo crew save files found"
                                + " in ~/.darkforge/saves/");
                return;
            }

            System.out.println(
                    "\n--- Saved Crews ---");
            for (int i = 0;
                 i < saves.size(); i++) {
                System.out.printf(
                        " %d. %s%n", i + 1,
                        saves.get(i)
                                .getFileName());
            }
            System.out.println(
                    " 0. Cancel");
            System.out.print("Choose file: ");
            String input =
                    scanner.nextLine().trim();

            if (input.equals("0")) return;

            int idx = Integer.parseInt(input)
                    - 1;
            if (idx < 0
                    || idx >= saves.size()) {
                System.out.println(
                        "Invalid selection.");
                return;
            }

            Crew loaded =
                    darkforge.crewAccess()
                            .loadCrew(saves.get(idx));
            activeCrew = loaded;
            System.out.println(
                    "\n✓ Loaded crew: "
                            + loaded.getName());
            System.out.println(
                    loaded.toSummary());

        } catch (IOException ex) {
            System.out.println(
                    "Load failed: "
                            + ex.getMessage());
        } catch (
                CharacterCorruptionException
                        ex) {
            System.out.println(
                    "⚠ Crew file corrupted: "
                            + ex.getUserMessage());
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Invalid number.");
        }
    }

    // =========================================
    // Option 3: View active crew
    // =========================================

    private void viewActiveCrew() {
        if (activeCrew == null) {
            System.out.println(
                    "\nNo crew loaded. Create or"
                            + " load a crew first.");
            return;
        }
        System.out.println();
        System.out.println(
                activeCrew.toFormattedString());
    }

    // =========================================
    // Option 4: Manage roster
    // =========================================

    private void manageRoster() {
        if (activeCrew == null) {
            System.out.println(
                    "\nNo crew loaded. Create or"
                            + " load a crew first.");
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println(
                    "\n--- Roster: "
                            + activeCrew.getName()
                            + " ---");
            List<Explorer> members =
                    activeCrew.getMembers();
            for (int i = 0;
                 i < members.size(); i++) {
                Explorer m = members.get(i);
                CrewRole role =
                        activeCrew
                                .getAssignedRole(m);
                String roleStr =
                        (role != null)
                                ? " [" + role
                                .getDisplayName()
                                  + "]"
                                : " [unassigned]";
                System.out.printf(
                        " %d. %s%s%n",
                        i + 1, m.getName(),
                        roleStr);
            }
            System.out.println();
            System.out.println(
                    " 1. Add Member");
            System.out.println(
                    " 2. Remove Member");
            System.out.println(
                    " 3. Reassign Roles");
            System.out.println(
                    " 4. View Member Details");
            System.out.println(
                    " 5. Manage Equipment");
            System.out.println(
                    " 0. Back");
            System.out.print("> ");
            String choice =
                    scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    if (activeCrew.getCrewSize()
                            >= 5) {
                        System.out.println(
                                "Crew is full"
                                        + " (5/5).");
                    } else {
                        System.out.println(
                                " 1. Create New"
                                        + " Explorer");
                        System.out.println(
                                " 2. Load Explorer"
                                        + " from File");
                        System.out.print(
                                "> ");
                        String sub = scanner
                                .nextLine().trim();
                        Explorer e = null;
                        if (sub.equals("1"))
                            e = creationWizard
                                    .run();
                        else if (
                                sub.equals("2"))
                            e = loadExplorer();
                        if (e != null) {
                            activeCrew
                                    .addMember(e);
                            System.out.println(
                                    "Added: "
                                            + e.getName());
                        }
                    }
                }
                case "2" -> {
                    if (members.isEmpty()) {
                        System.out.println(
                                "No members.");
                    } else {
                        System.out.print(
                                "Remove # "
                                        + "(0=cancel): ");
                        try {
                            int idx =
                                    Integer.parseInt(
                                            scanner
                                                    .nextLine()
                                                    .trim())
                                            - 1;
                            if (idx >= 0
                                    && idx
                                    < members
                                    .size()) {
                                Explorer rem =
                                        members
                                                .get(idx);
                                activeCrew
                                        .removeMember(
                                                rem);
                                System.out
                                        .println(
                                                "Removed: "
                                                        + rem
                                                        .getName());
                            }
                        } catch (
                                NumberFormatException
                                        ex) {
                            System.out.println(
                                    "Invalid.");
                        }
                    }
                }
                case "3" ->
                        promptRoleAssignment(
                                activeCrew);
                case "4" -> {
                    System.out.print(
                            "View # (0=cancel): ");
                    try {
                        int idx =
                                Integer.parseInt(
                                        scanner
                                                .nextLine()
                                                .trim())
                                        - 1;
                        if (idx >= 0
                                && idx
                                < members
                                .size()) {
                            System.out.println();
                            System.out.println(
                                    members.get(idx)
                                            .toFormattedString());
                        }
                    } catch (
                            NumberFormatException
                                    ex) {
                        System.out.println(
                                "Invalid.");
                    }
                }
                case "5" ->
                        manageExplorerInventory();
                case "0" -> managing = false;
                default -> System.out.println(
                        "Invalid option.");
            }
        }
    }

    // =========================================
    // Roster sub-option: Manage Equipment
    // =========================================

    private void manageExplorerInventory() {
        List<Explorer> members =
                activeCrew.getMembers();
        if (members.isEmpty()) {
            System.out.println(
                    "No members in crew.");
            return;
        }
        System.out.print(
                "Select member # (0=cancel): ");
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (idx < 0
                    || idx >= members.size())
                return;
            manageInventoryFor(
                    members.get(idx));
        } catch (
                NumberFormatException ex) {
            System.out.println("Invalid.");
        }
    }

    private void manageInventoryFor(
            Explorer explorer) {
        // Wrap explorer's equipment in an
        // EquipmentInventory for capacity-
        // aware management (STR+4 limit)
        EquipmentInventory inv =
                Inventory.forExplorer(explorer);
        for (Equipment e
                : explorer.getEquipment()) {
            inv.add(e);
        }

        boolean managing = true;
        while (managing) {
            System.out.printf(
                    "\n--- %s's Equipment"
                            + " (%.1f/%.1f wt)"
                            + " ---%n",
                    explorer.getName(),
                    inv.getCurrentWeight(),
                    inv.getMaxWeight());
            if (inv.isOverEncumbered()) {
                System.out.println(
                        "  ⚠ OVER-ENCUMBERED!"
                                + " Movement penalty"
                                + " applies.");
            }
            List<Equipment> items =
                    inv.getAll();
            if (items.isEmpty()) {
                System.out.println(
                        "  (empty)");
            } else {
                for (int i = 0;
                     i < items.size();
                     i++) {
                    System.out.printf(
                            "  %d. %s%n",
                            i + 1,
                            items.get(i)
                                    .display());
                }
            }
            System.out.printf(
                    "  Weight: %.1f/%.1f |" +
                            " Items: %d%n",
                    inv.getCurrentWeight(),
                    inv.getMaxWeight(),
                    inv.size());
            System.out.println();
            System.out.println(
                    " 1. Add Equipment");
            System.out.println(
                    " 2. Remove Equipment");
            System.out.println(
                    " 3. Search by Name");
            System.out.println(
                    " 4. Weapons at Hand");
            System.out.println(
                    " 5. Sort by Name");
            System.out.println(
                    " 6. Sort by Weight");
            System.out.println(
                    " 0. Back");
            System.out.print("> ");
            String choice =
                    scanner.nextLine().trim();

            switch (choice) {
                case "1" ->
                        addEquipmentToInv(
                                explorer, inv);
                case "2" ->
                        removeEquipmentFromInv(
                                explorer, inv);
                case "3" ->
                        searchEquipmentInv(
                                inv);
                case "4" -> {
                    List<Equipment> weapons =
                            inv.getWeaponsAtHand();
                    if (weapons.isEmpty()) {
                        System.out.println(
                                "No weapons.");
                    } else {
                        System.out.println(
                                "\nWeapons at hand"
                                        + " (max 3):");
                        for (Equipment w
                                : weapons) {
                            System.out.println(
                                    "  • "
                                            + w.display());
                        }
                    }
                }
                case "5" -> {
                    inv.sort(Comparator
                            .comparing(
                                    GameEntity
                                            ::getName));
                    System.out.println(
                            "Sorted by name.");
                }
                case "6" -> {
                    inv.sort(Comparator
                            .comparingDouble(
                                    e -> e.getWeight()
                                            .getWeightValue()));
                    System.out.println(
                            "Sorted by weight.");
                }
                case "0" ->
                        managing = false;
                default ->
                        System.out.println(
                                "Invalid option.");
            }
        }
    }

    private void addEquipmentToInv(
            Explorer explorer,
            EquipmentInventory inv) {
        System.out.println(
                "\n--- Add Equipment ---");
        System.out.print("Name: ");
        String name =
                scanner.nextLine().trim();
        if (name.isEmpty()) return;

        System.out.print(
                "Description: ");
        String desc =
                scanner.nextLine().trim();

        System.out.println(
                "Weight class:");
        EquipmentWeight[] weights =
                EquipmentWeight.values();
        for (int i = 0;
             i < weights.length; i++) {
            System.out.printf(
                    " %d. %s (%.1f)%n",
                    i + 1,
                    weights[i]
                            .getDisplayName(),
                    weights[i]
                            .getWeightValue());
        }
        System.out.print("> ");
        int wIdx;
        try {
            wIdx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (wIdx < 0
                    || wIdx
                    >= weights.length) {
                System.out.println(
                        "Invalid.");
                return;
            }
        } catch (
                NumberFormatException ex) {
            System.out.println(
                    "Invalid.");
            return;
        }

        System.out.print(
                "Gear bonus (0-3): ");
        int bonus;
        try {
            bonus = Integer.parseInt(
                    scanner.nextLine().trim());
        } catch (
                NumberFormatException ex) {
            bonus = 0;
        }

        System.out.print(
                "Is weapon? (y/n): ");
        boolean weapon = scanner.nextLine()
                .trim().equalsIgnoreCase("y");

        Equipment equip = new Equipment(
                name, desc, weights[wIdx],
                bonus, weapon);

        if (inv.add(equip)) {
            explorer.addEquipment("Personal",
                    List.of(equip));
            System.out.println(
                    "✓ Added: "
                            + equip.display());
            System.out.printf(
                    "  Weight: %.1f/%.1f%n",
                    inv.getCurrentWeight(),
                    inv.getMaxWeight());
        } else {
            System.out.printf(
                    "✗ Cannot add —"
                            + " exceeds carry"
                            + " limit (%.1f +"
                            + " %.1f > %.1f)%n",
                    inv.getCurrentWeight(),
                    equip.getWeight()
                            .getWeightValue(),
                    inv.getMaxWeight());
        }
    }

    private void removeEquipmentFromInv(
            Explorer explorer,
            EquipmentInventory inv) {
        if (inv.isEmpty()) {
            System.out.println(
                    "Inventory is empty.");
            return;
        }
        System.out.print(
                "Remove # (0=cancel): ");
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (idx >= 0
                    && idx < inv.size()) {
                Equipment removed =
                        inv.getAll().get(idx);
                inv.remove(removed);
                List<Equipment> updated =
                        new ArrayList<>(
                                explorer
                                        .getEquipment());
                updated.remove(removed);
                explorer.setEquipment(
                        updated);
                System.out.println(
                        "Removed: "
                                + removed.display());
            }
        } catch (
                NumberFormatException ex) {
            System.out.println(
                    "Invalid.");
        }
    }

    private void searchEquipmentInv(
            EquipmentInventory inv) {
        System.out.print(
                "Search term: ");
        String query =
                scanner.nextLine().trim();
        if (query.isEmpty()) return;

        List<Equipment> results =
                inv.findByName(query);
        if (results.isEmpty()) {
            System.out.println(
                    "No matches for '"
                            + query + "'.");
        } else {
            System.out.println(
                    "\nFound " + results.size()
                            + " match(es):");
            for (Equipment e : results) {
                System.out.println(
                        "  • "
                                + e.display());
            }
        }
    }

    // =========================================
    // Option 5: Manage Bird
    // =========================================

    private void manageBird() {
        if (activeCrew == null) {
            System.out.println(
                    "\nNo crew loaded. Create or"
                            + " load a crew first.");
            return;
        }
        Bird bird = activeCrew.getBird();
        if (bird == null) {
            System.out.println(
                    "\nNo Bird assigned. Choose"
                            + " one now? (y/n)");
            System.out.print("> ");
            if (scanner.nextLine().trim()
                    .equalsIgnoreCase("y")) {
                bird = promptBird();
                activeCrew.setBird(bird);
            }
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.printf(
                    "\n--- Bird: %s (%s) ---%n",
                    bird.getName(),
                    bird.getType()
                            .getDisplayName());
            System.out.printf(
                    "  HP: %d/%d  |  EP: %d/%d%n",
                    bird.getCurrentHealth(),
                    bird.getMaxHealth(),
                    bird.getCurrentEnergy(),
                    bird.getMaxEnergy());
            System.out.println(
                    "  Powers: "
                            + bird.getPowers().size());
            System.out.println();
            System.out.println(
                    " 1. View Powers");
            System.out.println(
                    " 2. Learn New Power");
            System.out.println(
                    " 3. Rest (Recover Energy)");
            System.out.println(
                    " 0. Back");
            System.out.print("> ");
            String choice =
                    scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.println(
                            "\nBasic:");
                    for (GarudaPower p
                            : bird
                            .getBasicPowers()) {
                        System.out.println(
                                "  • "
                                        + p.display());
                    }
                    System.out.println(
                            "Advanced:");
                    for (GarudaPower p
                            : bird
                            .getAdvancedPowers()) {
                        System.out.println(
                                "  • "
                                        + p.display());
                    }
                }
                case "2" -> {
                    List<GarudaPower> avail =
                            bird
                                    .getAvailablePowersToLearn(
                                            darkforge
                                                    .crewAccess()
                                                    .getAvailablePowers(
                                                            bird.getType()));
                    if (avail.isEmpty()) {
                        System.out.println(
                                "No more powers"
                                        + " to learn.");
                    } else {
                        System.out.println(
                                "\n--- Available"
                                        + " Powers ---");
                        for (int i = 0;
                             i < avail
                                     .size();
                             i++) {
                            GarudaPower p =
                                    avail.get(i);
                            System.out.printf(
                                    " %d. %s"
                                            + " (Cost:"
                                            + " %d CP)%n",
                                    i + 1,
                                    p.display(),
                                    p.getTrainingCost(
                                            bird
                                                    .getType()));
                        }
                        System.out.print(
                                "Choose (0="
                                        + "cancel): ");
                        try {
                            int idx =
                                    Integer
                                            .parseInt(
                                                    scanner
                                                            .nextLine()
                                                            .trim())
                                            - 1;
                            if (idx >= 0
                                    && idx
                                    < avail
                                    .size()) {
                                GarudaPower gp =
                                        avail
                                                .get(idx);
                                bird.learnPower(
                                        gp);
                                System.out
                                        .println(
                                                "✓ "
                                                        + bird
                                                        .getName()
                                                        + " learned "
                                                        + gp.getName()
                                                        + "!");
                            }
                        } catch (
                                NumberFormatException
                                        ex) {
                            System.out.println(
                                    "Invalid.");
                        }
                    }
                }
                case "3" -> {
                    bird.rest();
                    System.out.printf(
                            "Energy restored:"
                                    + " %d/%d%n",
                            bird
                                    .getCurrentEnergy(),
                            bird.getMaxEnergy());
                }
                case "0" -> managing = false;
                default -> System.out.println(
                        "Invalid option.");
            }
        }
    }

    // =========================================
    // Option 6: Manage Vehicles
    // =========================================

    private void manageVehicles() {
        if (activeCrew == null) {
            System.out.println(
                    "\nNo crew loaded. Create or"
                            + " load a crew first.");
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println(
                    "\n--- Vehicles ---");
            System.out.println("Shuttle: "
                    + (activeCrew.getShuttle()
                    != null
                    ? activeCrew.getShuttle()
                    .display()
                    : "None"));
            System.out.println("Rover: "
                    + (activeCrew.getRover()
                    != null
                    ? activeCrew.getRover()
                    .display()
                    : "None"));
            System.out.println();
            System.out.println(
                    " 1. Change Shuttle");
            System.out.println(
                    " 2. Change Rover");
            System.out.println(
                    " 0. Back");
            System.out.print("> ");
            String choice =
                    scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    Vehicle shuttle =
                            promptVehicleChoice(
                                    VehicleType.Category
                                            .SHUTTLE);
                    if (shuttle != null) {
                        shuttle =
                                promptVehicleCustomize(
                                        shuttle);
                        activeCrew.setShuttle(
                                shuttle);
                        System.out.println(
                                "✓ Shuttle: "
                                        + shuttle
                                        .getName());
                    }
                }
                case "2" -> {
                    Vehicle rover =
                            promptVehicleChoice(
                                    VehicleType.Category
                                            .ROVER);
                    if (rover != null) {
                        rover =
                                promptVehicleCustomize(
                                        rover);
                        activeCrew.setRover(
                                rover);
                        System.out.println(
                                "✓ Rover: "
                                        + rover
                                        .getName());
                    }
                }
                case "0" -> managing = false;
                default -> System.out.println(
                        "Invalid option.");
            }
        }
    }

    // =========================================
    // Option 7: Save active crew
    // =========================================

    private void saveActiveCrew() {
        if (activeCrew == null) {
            System.out.println(
                    "\nNo crew loaded. Create or"
                            + " load a crew first.");
            return;
        }
        try {
            Path saved =
                    darkforge.crewAccess()
                            .saveCrew(activeCrew);
            System.out.println(
                    "\n✓ Crew saved to: "
                            + saved.getFileName());
        } catch (IOException ex) {
            System.out.println(
                    "Save failed: "
                            + ex.getMessage());
        }
    }

    // =========================================
    // Crew creation helpers
    // =========================================

    // =========================================
    // Step 1: Crew name
    // =========================================

    private String promptCrewName() {
        while (true) {
            System.out.print(
                    "\nCrew name (type 'random' to roll a D6 name): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Name cannot be empty.");
                continue;
            }

            if (input.equalsIgnoreCase("random")
                    || input.equalsIgnoreCase("roll")) {
                // Prompt for an Explorer name in case the D6 prefix
                // roll = 1 (EXPLORER_NAME).
                System.out.print(
                        "Explorer name for the roll (or"
                                + " Enter to skip): ");
                String eName = scanner.nextLine().trim();
                String generated = darkforge.crewAccess()
                        .generateRandomCrewName(
                                eName.isEmpty() ? null : eName);
                System.out.println("\n  Rolled: " + generated);
                System.out.print("Accept? (y/n): ");
                String confirm = scanner.nextLine().trim();
                if (confirm.equalsIgnoreCase("y")) {
                    return generated;
                }
                // Otherwise loop back to re-prompt
                continue;
            }

            return input;
        }
    }

    // =========================================
    // Step 2: Add members (4-5)
    // =========================================

    private List<Explorer> promptMembers() {
        List<Explorer> members =
                new ArrayList<>();

        while (members.size() < 5) {
            int current = members.size();
            String req = current < 4
                    ? " (need "
                      + (4 - current) + " more)"
                    : " (optional, 0 to finish)";

            System.out.printf(
                    "\n--- Member %d/5%s ---%n",
                    current + 1, req);
            System.out.println(
                    " 1. Create New Explorer");
            System.out.println(
                    " 2. Load Explorer from File");
            if (current >= 4) {
                System.out.println(
                        " 0. Done adding members");
            }
            System.out.print("> ");
            String choice =
                    scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    Explorer e =
                            creationWizard.run();
                    if (e != null) {
                        members.add(e);
                        System.out.println(
                                "Added: "
                                        + e.getName());
                    }
                }
                case "2" -> {
                    Explorer e =
                            loadExplorer();
                    if (e != null) {
                        members.add(e);
                        System.out.println(
                                "Added: "
                                        + e.getName());
                    }
                }
                case "0" -> {
                    if (current >= 4)
                        return members;
                    System.out.println(
                            "Need at least"
                                    + " 4 members.");
                }
                default -> System.out.println(
                        "Invalid option.");
            }
        }
        return members;
    }

    private Explorer loadExplorer() {
        try {
            List<Path> saves =
                    darkforge.persistenceAccess()
                            .listSaves();
            if (saves.isEmpty()) {
                System.out.println(
                        "No save files found.");
                return null;
            }

            System.out.println(
                    "\nSaved files:");
            for (int i = 0;
                 i < saves.size(); i++) {
                System.out.printf(
                        " %d. %s%n", i + 1,
                        saves.get(i)
                                .getFileName());
            }
            System.out.print("Choose file: ");
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (idx < 0
                    || idx >= saves.size()) {
                System.out.println(
                        "Invalid selection.");
                return null;
            }

            return darkforge
                    .persistenceAccess()
                    .loadExplorer(
                            saves.get(idx));

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
        } catch (NumberFormatException ex) {
            System.out.println(
                    "Invalid number.");
        }
        return null;
    }

    // =========================================
    // Step 3: Role assignment
    // =========================================

    private void promptRoleAssignment(
            Crew crew) {
        System.out.println(
                "\n--- Role Assignment ---");
        System.out.println(
                " 1. Auto-assign (optimal fit)");
        System.out.println(
                " 2. Manual assignment");
        System.out.print("> ");
        String choice =
                scanner.nextLine().trim();

        if (choice.equals("1")) {
            autoAssignRoles(crew);
        } else {
            manualAssignRoles(crew);
        }
    }

    private void autoAssignRoles(Crew crew) {
        Map<CrewRole, Explorer> optimal =
                darkforge.crewAccess()
                        .getOptimalAssignment(crew);

        for (Map.Entry<CrewRole, Explorer>
                entry : optimal.entrySet()) {
            crew.assignRole(
                    entry.getKey(),
                    entry.getValue());
            System.out.printf(
                    "  %s \u2192 %s"
                            + " (fitness %d)%n",
                    entry.getKey()
                            .getDisplayName(),
                    entry.getValue().getName(),
                    entry.getKey()
                            .getRoleFitness(
                                    entry.getValue()));
        }
        System.out.println(
                "Roles auto-assigned.");
    }

    private void manualAssignRoles(Crew crew) {
        List<Explorer> members =
                crew.getMembers();

        // Display fitness table
        System.out.println(
                "\nRole Fitness Scores:");
        System.out.printf("  %-15s", "");
        for (Explorer m : members) {
            System.out.printf("%-12s",
                    m.getName().split(" ")[0]);
        }
        System.out.println();

        for (CrewRole role
                : CrewRole.values()) {
            System.out.printf("  %-15s",
                    role.getDisplayName());
            for (Explorer m : members) {
                System.out.printf("%-12d",
                        role.getRoleFitness(m));
            }
            System.out.println();
        }

        // Assign mandatory roles
        Set<Explorer> assigned =
                new HashSet<>();
        for (CrewRole role
                : CrewRole.values()) {
            if (role.isOptional()) continue;
            assignSingleRole(
                    crew, role, members,
                    assigned);
        }

        // Optional: Archaeologist
        if (members.size()
                > assigned.size()) {
            System.out.printf(
                    "\nAssign %s? (y/n): ",
                    CrewRole.ARCHAEOLOGIST
                            .getDisplayName());
            if (scanner.nextLine().trim()
                    .equalsIgnoreCase("y")) {
                assignSingleRole(crew,
                        CrewRole.ARCHAEOLOGIST,
                        members, assigned);
            }
        }
    }

    private void assignSingleRole(Crew crew,
                                  CrewRole role,
                                  List<Explorer> members,
                                  Set<Explorer> assigned) {
        List<Explorer> available =
                members.stream()
                        .filter(m ->
                                !assigned.contains(m))
                        .toList();

        while (true) {
            System.out.printf(
                    "\nAssign %s:%n",
                    role.getDisplayName());
            for (int i = 0;
                 i < available.size();
                 i++) {
                Explorer m = available.get(i);
                System.out.printf(
                        " %d. %s (fitness %d)%n",
                        i + 1, m.getName(),
                        role.getRoleFitness(m));
            }
            System.out.print("> ");
            try {
                int idx = Integer.parseInt(
                        scanner.nextLine().trim())
                        - 1;
                if (idx >= 0
                        && idx
                        < available.size()) {
                    Explorer chosen =
                            available.get(idx);
                    crew.assignRole(
                            role, chosen);
                    assigned.add(chosen);
                    return;
                }
            } catch (
                    NumberFormatException
                            ignored) {
            }
            System.out.println(
                    "Invalid choice.");
        }
    }

    // =========================================
    // Steps 4-5: Bird selection
    // =========================================

    private Bird promptBird() {
        System.out.println(
                "\n--- Bird Selection ---");
        BirdType type = promptBirdType();
        return promptBirdDetails(type);
    }

    private BirdType promptBirdType() {
        BirdType[] types = BirdType.values();
        System.out.println(
                "\nChoose your Bird type:");
        System.out.printf(
                "  %-3s %-10s %-8s %-8s %-20s%n",
                "#", "Type", "Health",
                "Energy", "Special Power");
        System.out.println(
                "  " + "-".repeat(49));

        for (int i = 0;
             i < types.length; i++) {
            BirdType t = types[i];
            System.out.printf(
                    "  %d. %-10s %-8d %-8d"
                            + " %-20s%n",
                    i + 1,
                    t.getDisplayName(),
                    t.getStartingHealth(),
                    t.getStartingEnergy(),
                    t.getSpecialPower());
        }

        while (true) {
            System.out.print("> ");
            try {
                int idx = Integer.parseInt(
                        scanner.nextLine().trim())
                        - 1;
                if (idx >= 0
                        && idx
                        < types.length) {
                    return types[idx];
                }
            } catch (
                    NumberFormatException
                            ignored) {
            }
            System.out.println(
                    "Enter 1-"
                            + types.length + ".");
        }
    }

    private Bird promptBirdDetails(
            BirdType type) {
        System.out.println(
                "\nCustomize your "
                        + type.getDisplayName()
                        + " Bird:");
        System.out.println(
                " 1. Enter details manually");
        System.out.println(
                " 2. Roll random appearance"
                        + " (D66)");
        System.out.print("> ");
        String choice =
                scanner.nextLine().trim();

        String name, color, body,
                personality;

        if (choice.equals("2")) {
            // Roll all four D66 tables
            Map<String, String> rolled =
                    darkforge.crewAccess()
                            .generateRandomBirdAppearance();
            name = rolled.get("name");
            color = rolled.get("color");
            body = rolled.get(
                    "bodyFeature");
            personality = rolled.get(
                    "personality");

            System.out.println(
                    "\n  Rolled appearance:");
            System.out.println(
                    "    Name:        " + name);
            System.out.println(
                    "    Color:       "
                            + color);
            System.out.println(
                    "    Body:        " + body);
            System.out.println(
                    "    Personality: "
                            + personality);
            System.out.print(
                    "Accept? (y to keep,"
                            + " n to customize): ");

            if (!scanner.nextLine().trim()
                    .equalsIgnoreCase("y")) {
                // Let user override
                // individual fields
                System.out.print(
                        "Name (Enter to keep"
                                + " '" + name
                                + "'): ");
                String in =
                        scanner.nextLine()
                                .trim();
                if (!in.isEmpty())
                    name = in;

                System.out.print(
                        "Color (Enter to keep"
                                + " '" + color
                                + "'): ");
                in = scanner.nextLine()
                        .trim();
                if (!in.isEmpty())
                    color = in;

                System.out.print(
                        "Body feature (Enter"
                                + " to keep '" + body
                                + "'): ");
                in = scanner.nextLine()
                        .trim();
                if (!in.isEmpty())
                    body = in;

                System.out.print(
                        "Personality (Enter"
                                + " to keep '"
                                + personality
                                + "'): ");
                in = scanner.nextLine()
                        .trim();
                if (!in.isEmpty())
                    personality = in;
            }
        } else {
            // Manual entry
            System.out.print("Name: ");
            name =
                    scanner.nextLine().trim();
            if (name.isEmpty())
                name = type
                        .getDisplayName();

            System.out.print(
                    "Color (or Enter for"
                            + " default): ");
            color =
                    scanner.nextLine().trim();
            if (color.isEmpty())
                color = "Dark grey";

            System.out.print(
                    "Body feature (or Enter"
                            + " for default): ");
            body =
                    scanner.nextLine().trim();
            if (body.isEmpty())
                body = "Sleek feathers";

            System.out.print(
                    "Personality (or Enter"
                            + " for default): ");
            personality =
                    scanner.nextLine().trim();
            if (personality.isEmpty())
                personality = "Alert";
        }

        Bird bird = new Bird(
                name, type, color,
                body, personality);
        System.out.printf(
                "\nBird created: %s the %s"
                        + " (HP %d / EP %d)%n",
                name, type.getDisplayName(),
                bird.getMaxHealth(),
                bird.getMaxEnergy());
        System.out.printf(
                "  %s | %s | %s%n",
                color, body, personality);
        return bird;
    }

    // =========================================
    // Steps 6-8: Vehicle selection
    // =========================================

    private Vehicle promptVehicleChoice(
            VehicleType.Category category) {
        String label =
                category
                        == VehicleType.Category.SHUTTLE
                        ? "Shuttle" : "Rover";

        System.out.printf(
                "\n--- %s Selection ---%n",
                label);

        VehicleType[] options =
                Arrays.stream(
                                VehicleType.values())
                        .filter(v -> v.getCategory()
                                == category)
                        .toArray(VehicleType[]::new);

        System.out.printf(
                "  %-3s %-14s %-5s %-5s"
                        + " %-6s %-6s %-6s %-12s%n",
                "#", "Type", "Mnv", "Spd",
                "Hull", "Armor", "Slots",
                "Propulsion");
        System.out.println(
                "  " + "-".repeat(60));

        for (int i = 0;
             i < options.length; i++) {
            VehicleType v = options[i];
            System.out.printf(
                    "  %d. %-14s %+d   %-5d"
                            + " %-6d %-6d %-6d"
                            + " %-12s%n",
                    i + 1,
                    v.getDisplayName(),
                    v.getManeuverability(),
                    v.getSpeed(), v.getHull(),
                    v.getArmor(), v.getSlots(),
                    v.getPropulsion());
        }

        System.out.println(
                "  0. Skip (no " + label + ")");

        while (true) {
            System.out.print("> ");
            try {
                int idx = Integer.parseInt(
                        scanner.nextLine().trim());
                if (idx == 0) return null;
                idx--;
                if (idx >= 0
                        && idx
                        < options.length) {
                    return new Vehicle(
                            options[idx]
                                    .getDisplayName(),
                            options[idx],
                            "Unpainted");
                }
            } catch (
                    NumberFormatException
                            ignored) {
            }
            System.out.println(
                    "Enter 0-"
                            + options.length + ".");
        }
    }

    private Vehicle promptVehicleCustomize(
            Vehicle vehicle) {
        System.out.printf(
                "\nCustomize %s (%s):%n",
                vehicle.getType()
                        .getDisplayName(),
                vehicle.getType()
                        .getCategory());

        System.out.print(
                "Name (or Enter for '"
                        + vehicle.getName()
                        + "'): ");
        String name =
                scanner.nextLine().trim();

        System.out.print(
                "Paint color"
                        + " (or Enter for default): ");
        String paint =
                scanner.nextLine().trim();
        if (paint.isEmpty())
            paint = "Standard grey";

        String finalName = name.isEmpty()
                ? vehicle.getName() : name;
        return new Vehicle(
                finalName, vehicle.getType(),
                paint);
    }

    // =========================================
    // Step 9: Supply
    // =========================================

    private void promptSupply(Crew crew) {
        System.out.println(
                "\n--- Supply Distribution ---");
        while (true) {
            System.out.print(
                    "Starting supply points: ");
            try {
                int supply = Integer.parseInt(
                        scanner.nextLine().trim());
                if (supply >= 0) {
                    crew.addSupply(supply);
                    System.out.printf(
                            "Supply set to %d"
                                    + " (%d per"
                                    + " explorer).%n",
                            supply,
                            crew
                                    .getSupplyPerExplorer());
                    return;
                }
                System.out.println(
                        "Supply cannot be"
                                + " negative.");
            } catch (
                    NumberFormatException e) {
                System.out.println(
                        "Enter a number.");
            }
        }
    }

    // =========================================
    // Step 10: Review
    // =========================================

    private void reviewCrew(Crew crew) {
        System.out.println(
                "\n" + "=".repeat(50));
        System.out.println(
                " CREW REVIEW");
        System.out.println(
                "=".repeat(50));
        System.out.println(
                crew.toFormattedString());
    }

    // =========================================
    // Step 11: Save
    // =========================================

    private void promptSave(Crew crew) {
        System.out.print(
                "\nSave this crew? (y/n): ");
        String confirm =
                scanner.nextLine().trim();

        if (confirm
                .equalsIgnoreCase("y")) {
            try {
                Path path = darkforge
                        .crewAccess()
                        .saveCrew(crew);
                System.out.println(
                        "Saved to: " + path);
            } catch (IOException ex) {
                System.out.println(
                        "Save failed: "
                                + ex.getMessage());
            }
        }
    }

    // =========================================
    // Banner
    // =========================================

    private void printBanner() {
        System.out.println(
                "\n" + "=".repeat(50));
        System.out.println(
                " DARKFORGE v4.0 \u2014 Crew"
                        + " Assembly Wizard");
        System.out.println(
                "=".repeat(50));
    }
}
