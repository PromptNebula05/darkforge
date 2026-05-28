package darkforge.cli;

import darkforge.facade.FacadeCatalog;
import darkforge.model.*;
import java.util.*;

/**
 * Console-based catalog browser.
 * Provides text-menu access to search,
 * filter by category/cost/tech, and
 * the catalog analytics report.
 */
public class ConsoleCatalogBrowser {

    private final FacadeCatalog catalog;
    private final Scanner scanner;

    public ConsoleCatalogBrowser(
            FacadeCatalog catalog,
            Scanner scanner) {
        this.catalog = catalog;
        this.scanner = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice =
                    scanner.nextLine().trim();
            switch (choice) {
                case "1" ->
                        searchByKeyword();
                case "2" ->
                        browseByCategory();
                case "3" ->
                        filterByCostRange();
                case "4" ->
                        filterByTechLevel();
                case "5" ->
                        showAnalyticsReport();
                case "6" ->
                        showTopExpensive();
                case "0" ->
                        running = false;
                default ->
                        System.out.println(
                                "Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println(
                "\n=== Equipment Catalog"
                        + " Browser ===");
        System.out.printf(
                "Catalog: %d items%n",
                catalog.getCatalogSize());
        System.out.println(
                "1. Search by keyword");
        System.out.println(
                "2. Browse by category");
        System.out.println(
                "3. Filter by cost range");
        System.out.println(
                "4. Filter by tech level");
        System.out.println(
                "5. Analytics report");
        System.out.println(
                "6. Top 10 most expensive");
        System.out.println(
                "0. Back to main menu");
        System.out.print("> ");
    }

    private void searchByKeyword() {
        System.out.print(
                "Enter keyword: ");
        String kw =
                scanner.nextLine().trim();
        List<Item> results =
                catalog.search(kw);
        displayResults(results);
    }

    private void browseByCategory() {
        System.out.println(
                "Available categories:");
        List<String> cats =
                new ArrayList<>(
                        catalog.getCategories());
        Collections.sort(cats);
        for (int i = 0;
             i < cats.size(); i++) {
            System.out.printf(
                    "  %d. %s%n",
                    i + 1, cats.get(i));
        }
        System.out.print(
                "Select category #: ");
        try {
            int idx = Integer.parseInt(
                    scanner.nextLine().trim())
                    - 1;
            if (idx >= 0
                    && idx < cats.size()) {
                displayResults(
                        catalog
                                .filterByCategory(
                                        cats.get(idx)));
            }
        } catch (
                NumberFormatException e) {
            System.out.println(
                    "Invalid input.");
        }
    }

    private void filterByCostRange() {
        System.out.print(
                "Min cost: ");
        int min = Integer.parseInt(
                scanner.nextLine().trim());
        System.out.print(
                "Max cost: ");
        int max = Integer.parseInt(
                scanner.nextLine().trim());
        displayResults(
                catalog.filterByCostRange(
                        min, max));
    }

    private void filterByTechLevel() {
        System.out.println(
                "Tech levels: O, G, H");
        System.out.print(
                "Enter tech code: ");
        String code =
                scanner.nextLine().trim()
                        .toUpperCase();
        try {
            TechLevel tl =
                    TechLevel.fromCode(code);
            displayResults(
                    catalog.filterByTechLevel(
                            tl));
        } catch (Exception e) {
            System.out.println(
                    "Invalid tech level.");
        }
    }

    private void showAnalyticsReport() {
        System.out.println(
                catalog.getCatalogReport());
    }

    private void showTopExpensive() {
        displayResults(
                catalog.getTopByCost(10));
    }

    private void displayResults(
            List<? extends Item> items) {
        if (items.isEmpty()) {
            System.out.println(
                    "No results found.");
            return;
        }
        System.out.printf(
                "%n%-30s %-12s %-8s %-6s%n",
                "Name", "Category",
                "Cost", "Tech");
        System.out.println(
                "-".repeat(60));
        for (Item item : items) {
            System.out.printf(
                    "%-30s %-12s %-8d %-6s%n",
                    item.getName(),
                    item.getCategory(),
                    item.getCost(),
                    item.getTechString());
        }
        System.out.printf(
                "%d items shown.%n",
                items.size());
    }
}