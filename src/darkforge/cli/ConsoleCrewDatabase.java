package darkforge.cli;

import darkforge.crew.Crew;
import darkforge.database.CrewDao;
import darkforge.database.CrewMembershipDao;
import darkforge.exception.DatabaseException;
import darkforge.facade.FacadeDarkforge;
import darkforge.facade.FacadeDatabase;
import darkforge.model.Explorer;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive submenu for the SQLite crew database
 * (main menu option [15]). Delegates all persistence to
 * FacadeDatabase; holds no JDBC objects of its own.
 */
public class ConsoleCrewDatabase {

    private final Scanner scanner;
    private final FacadeDatabase database;

    public ConsoleCrewDatabase(Scanner scanner) {
        this.scanner = scanner;
        this.database = FacadeDarkforge.getTheInstance()
                .databaseAccess();
    }

    /**
     * @param sessionCrews crews created/loaded during the
     *                     current run (from ConsoleMainMenu)
     */
    public void run(List<Crew> sessionCrews) {
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> saveCrew(sessionCrews);
                    case "2" -> listCrews();
                    case "3" -> loadCrew(sessionCrews);
                    case "4" -> deleteCrew();
                    case "5" -> listExplorers();
                    case "6" -> viewRoster();
                    case "7" -> crewStats();
                    case "0", "q", "Q" -> running = false;
                    default ->
                            System.out.println(
                                    "Invalid option.");
                }
            } catch (DatabaseException e) {
                System.out.println(
                        "\u26a0 " + e.getUserMessage());
                System.out.println(
                        "  (" + e.getMessage() + ")");
            }
        }
    }

    private void printMenu() {
        System.out.println(
                "\n--- Crew Database (SQLite) ---");
        System.out.println(
                " 1. Save a session crew to the database");
        System.out.println(
                " 2. List crews in the database");
        System.out.println(
                " 3. Load a crew from the database");
        System.out.println(
                " 4. Delete a crew from the database");
        System.out.println(
                " 5. List all stored explorers");
        System.out.println(
                " 6. View full crew roster (join)");
        System.out.println(
                " 7. Crew stats (aggregation)");
        System.out.println(" 0. Back");
    }

    // ---- 1. Save ----
    private void saveCrew(List<Crew> sessionCrews) {
        if (sessionCrews.isEmpty()) {
            System.out.println(
                    "No crews in this session. Create one"
                            + " first (main menu option 7).");
            return;
        }
        System.out.println("\nSelect crew to save:");
        for (int i = 0; i < sessionCrews.size(); i++) {
            System.out.printf(" %d. %s%n",
                    i + 1, sessionCrews.get(i).getName());
        }
        System.out.print("> ");
        int idx = parseIndex(sessionCrews.size());
        if (idx < 0) {
            return;
        }
        long crewId =
                database.saveCrew(sessionCrews.get(idx));
        System.out.println(
                "Saved crew (crew_id=" + crewId + ").");
    }

    // ---- 2. List crews ----
    private void listCrews() {
        List<CrewDao.CrewRecord> crews =
                database.listCrews();
        if (crews.isEmpty()) {
            System.out.println(
                    "No crews stored yet.");
            return;
        }
        System.out.printf("%n%d crew(s):%n",
                crews.size());
        for (CrewDao.CrewRecord c : crews) {
            System.out.printf(
                    "  #%d  %s%s%n",
                    c.crewId(), c.name(),
                    c.birdName() != null
                            ? "  (Bird: " + c.birdName() + ")"
                            : "");
        }
    }

    // ---- 3. Load ----
    private void loadCrew(List<Crew> sessionCrews) {
        System.out.print("Crew name to load: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Enter a crew name.");
            return;
        }
        Crew crew = database.loadCrew(name);
        if (crew == null) {
            System.out.println(
                    "No crew named '" + name + "'.");
            return;
        }
        sessionCrews.add(crew);
        System.out.println(crew.toFormattedString());
        System.out.println(
                "(Loaded into the current session.)");
    }

    // ---- 4. Delete ----
    private void deleteCrew() {
        System.out.print("Crew name to delete: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Enter a crew name.");
            return;
        }
        boolean deleted = database.deleteCrew(name);
        System.out.println(deleted
                ? "Deleted '" + name
                  + "' (memberships cascaded)."
                : "No crew named '" + name + "'.");
    }

    // ---- 5. List explorers ----
    private void listExplorers() {
        List<Explorer> explorers =
                database.listExplorers();
        if (explorers.isEmpty()) {
            System.out.println(
                    "No explorers stored yet.");
            return;
        }
        System.out.printf("%n%d explorer(s):%n",
                explorers.size());
        for (Explorer e : explorers) {
            System.out.println("  " + e.display());
        }
    }

    // ---- 6. View roster (3-table join) ----
    private void viewRoster() {
        List<CrewMembershipDao.RosterEntry> roster =
                database.roster();
        if (roster.isEmpty()) {
            System.out.println(
                    "No memberships stored yet.");
            return;
        }
        System.out.println("\nCrew roster:");
        for (CrewMembershipDao.RosterEntry r
                : roster) {
            System.out.printf(
                    "  %-14s %-22s %s%n",
                    "[" + r.role() + "]",
                    r.explorer(), r.crew());
        }
    }

    // ---- 7. Crew stats (aggregation) ----
    private void crewStats() {
        List<CrewMembershipDao.CrewStat> stats =
                database.crewStats();
        if (stats.isEmpty()) {
            System.out.println(
                    "No crews with members yet.");
            return;
        }
        System.out.println(
                "\nCrew stats (members, avg STR):");
        for (CrewMembershipDao.CrewStat s : stats) {
            System.out.printf(
                    "  %-22s %2d member(s)"
                            + "  avg STR %.1f%n",
                    s.crew(), s.memberCount(),
                    s.avgStrength());
        }
    }

    // ---- helper ----
    private int parseIndex(int size) {
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= size) {
                System.out.println(
                        "Invalid selection.");
                return -1;
            }
            return idx;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return -1;
        }
    }
}
