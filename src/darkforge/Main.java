package darkforge;

import darkforge.cli.ConsoleMainMenu;
import darkforge.facade.FacadeDarkforge;

import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    System.out.println(
            "\n" + "=".repeat(50));
    System.out.println(
            " DARKFORGE v6.0 — Coriolis"
                    + " Explorer & Crew"
                    + " Management");
    System.out.println(
            " CS622 — Assignment 6:"
                    + " Relational Database (JDBC/SQLite)");
    System.out.println(
            "=".repeat(50));

    FacadeDarkforge facade =
            FacadeDarkforge.getTheInstance();
    facade.initialize();

    try (Scanner scanner =
                 new Scanner(System.in)) {
      ConsoleMainMenu menu =
              new ConsoleMainMenu(scanner);
      menu.run();
    } finally {
      facade.concurrencyAccess()
              .shutdown();
      facade.databaseAccess().shutdown();
    }

    System.out.println(
            "\nDARKFORGE shut down. May the "
                    + "Icons guide your path.");
  }
}
