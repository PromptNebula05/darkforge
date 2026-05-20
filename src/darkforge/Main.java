package darkforge;

import darkforge.cli.ConsoleMainMenu;
import darkforge.facade.FacadeDarkforge;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    System.out.println(
            "\n" + "=".repeat(50));
    System.out.println(
            "  DARKFORGE v2.0 — Coriolis Explorer "
                    + "Management");
    System.out.println(
            "  CS622 — Assignment 2: Handling Text "
                    + "and Exceptions");
    System.out.println(
            "=".repeat(50));

    FacadeDarkforge.getTheInstance()
            .initialize();

    try (Scanner scanner =
                 new Scanner(System.in)) {
      ConsoleMainMenu menu =
              new ConsoleMainMenu(scanner);
      menu.run();
    }

    System.out.println(
            "\nDARKFORGE shut down. May the "
                    + "Icons guide your path.");
  }
}