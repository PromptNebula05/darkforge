package darkforge.cli;

import darkforge.crew.*;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.facade.FacadeDarkforge;
import darkforge.model.Explorer;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Step-by-step interactive console wizard for
 * crew assembly. Mirrors ConsoleCreationWizard's
 * Scanner-based pattern with Façade delegation.
 */
public class ConsoleCrewWizard {
    private final Scanner scanner;
    private final FacadeDarkforge darkforge;
    private final ConsoleCreationWizard
            creationWizard;

    public ConsoleCrewWizard(Scanner scanner) {
        this.scanner = scanner;
        this.darkforge =
                FacadeDarkforge.getTheInstance();
        this.creationWizard =
                new ConsoleCreationWizard(scanner);
    }

    /**
     * Run the full crew assembly wizard.
     * @return the assembled Crew, or null
     *     if creation is aborted
     */
    public Crew run() {
        printBanner();

        String name = promptCrewName();
        List<Explorer> members =
                promptMembers();
        if (members.size() < 4) {
            System.out.println(
                    "Need at least 4 members."
                            + " Aborting.");
            return null;
        }

        Crew crew = new Crew(name);
        for (Explorer m : members) {
            crew.addMember(m);
        }

        promptRoleAssignment(crew);

        Bird bird = promptBird();
        crew.setBird(bird);

        Vehicle shuttle = promptVehicleChoice(
                VehicleType.Category.SHUTTLE);
        if (shuttle != null) {
            shuttle =
                    promptVehicleCustomize(shuttle);
            crew.setShuttle(shuttle);
        }

        Vehicle rover = promptVehicleChoice(
                VehicleType.Category.ROVER);
        if (rover != null) {
            rover =
                    promptVehicleCustomize(rover);
            crew.setRover(rover);
        }

        promptSupply(crew);
        reviewCrew(crew);
        promptSave(crew);

        return crew;
    }

    // =========================================
    // Step 1: Crew name
    // =========================================

    private String promptCrewName() {
        while (true) {
            System.out.println(
                    "\nEnter crew name:");
            System.out.print("> ");
            String input =
                    scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println(
                    "Name cannot be empty.");
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

        System.out.print("Name: ");
        String name =
                scanner.nextLine().trim();
        if (name.isEmpty())
            name = type.getDisplayName();

        System.out.print(
                "Color (or Enter for default): ");
        String color =
                scanner.nextLine().trim();
        if (color.isEmpty())
            color = "Dark grey";

        System.out.print(
                "Body feature"
                        + " (or Enter for default): ");
        String body =
                scanner.nextLine().trim();
        if (body.isEmpty())
            body = "Sleek feathers";

        System.out.print(
                "Personality"
                        + " (or Enter for default): ");
        String personality =
                scanner.nextLine().trim();
        if (personality.isEmpty())
            personality = "Alert";

        Bird bird = new Bird(
                name, type, color,
                body, personality);
        System.out.printf(
                "\nBird created: %s the %s"
                        + " (HP %d / EP %d)%n",
                name, type.getDisplayName(),
                bird.getMaxHealth(),
                bird.getMaxEnergy());
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
                " DARKFORGE v3.0 \u2014 Crew"
                        + " Assembly Wizard");
        System.out.println(
                "=".repeat(50));
    }
}