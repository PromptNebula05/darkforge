package darkforge.data;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stream-based analytics for the equipment
 * catalog. Demonstrates groupingBy,
 * summarizingDouble, partitioningBy,
 * and multi-step stream pipelines.
 */
public class CatalogAnalytics {

    private final ItemCatalog catalog;

    public CatalogAnalytics(
            ItemCatalog catalog) {
        this.catalog = catalog;
    }

    // =========================================
    // Cost statistics by category
    // =========================================

    /**
     * groupingBy + summarizingDouble.
     * Returns cost summary statistics
     * per category.
     */
    public Map<String,
            DoubleSummaryStatistics>
    getCostStatsByCategory() {
        return catalog.getAll().stream()
                .collect(Collectors.groupingBy(
                        Item::getCategory,
                        Collectors
                                .summarizingDouble(
                                        Item::getCost)));
    }

    // =========================================
    // Top-N by cost
    // =========================================

    /**
     * sorted().limit(n).
     */
    public List<Item> getTopNByCost(
            int n) {
        return catalog.getAll().stream()
                .sorted(Comparator
                        .comparingInt(Item::getCost)
                        .reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    // =========================================
    // Partition by affordability
    // =========================================

    /**
     * partitioningBy.
     */
    public Map<Boolean, List<Item>>
    partitionByAffordability(
            int budget) {
        return catalog.getAll().stream()
                .collect(Collectors
                        .partitioningBy(i ->
                                i.getCost() <= budget));
    }

    // =========================================
    // Weapon type distribution
    // =========================================

    public Map<WeaponType, Long>
    getWeaponTypeDistribution() {
        return catalog
                .filterByType(Weapon.class)
                .stream()
                .collect(Collectors.groupingBy(
                        Weapon::getWeaponType,
                        Collectors.counting()));
    }

    // =========================================
    // Tech level distribution
    // =========================================

    public Map<TechLevel, Long>
    getTechLevelDistribution() {
        return catalog.getAll().stream()
                .collect(Collectors.groupingBy(
                        Item::getTechLevel,
                        Collectors.counting()));
    }

    // =========================================
    // Formatted report
    // =========================================

    /**
     * Multi-stream pipeline producing a
     * formatted catalog summary report.
     */
    public String generateCatalogReport() {
        StringBuilder sb =
                new StringBuilder();
        sb.append("=== DARKFORGE Catalog"
                        + " Report ===")
                .append("\n");
        sb.append(String.format(
                "Total items: %d%n",
                catalog.size()));
        sb.append(String.format(
                "Average cost: %.0f rukh%n",
                catalog.getAverageCost()));

        sb.append("\nBy category:\n");
        catalog.getCategoryStats()
                .entrySet().stream()
                .sorted(Map.Entry
                        .<String, Long>
                                comparingByValue()
                        .reversed())
                .forEach(e ->
                        sb.append(String.format(
                                "  %-25s %d items%n",
                                e.getKey(),
                                e.getValue())));

        sb.append("\nTop 5 most expensive:\n");
        getTopNByCost(5).forEach(i ->
                sb.append(String.format(
                        "  %-30s %d cost%n",
                        i.getName(),
                        i.getCost())));

        sb.append("\nTech level breakdown:\n");
        getTechLevelDistribution()
                .forEach((tl, count) ->
                        sb.append(String.format(
                                "  %s: %d items%n",
                                tl.getDisplayName(),
                                count)));

        return sb.toString();
    }
}