package darkforge.crew;

import darkforge.collection.InventoryHolder;
import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Cross-entity inventory search.
 * Demonstrates polymorphic InventoryHolder<?>
 * iteration across Explorers and Vehicles.
 */
public class CrewInventorySearch {

    // =========================================
    // Holder aggregation
    // =========================================

    /**
     * Collect all InventoryHolders in a crew
     * (explorers + rover + shuttle).
     */
    private static List<InventoryHolder<?>>
    getAllHolders(Crew crew) {
        List<InventoryHolder<?>> holders =
                new ArrayList<>(
                        crew.getMembers());
        if (crew.getRover() != null) {
            holders.add(crew.getRover());
        }
        if (crew.getShuttle() != null) {
            holders.add(
                    crew.getShuttle());
        }
        return holders;
    }

    // =========================================
    // Cross-entity search
    // =========================================

    /**
     * Search all inventories in a crew
     * (explorers + vehicles) for items
     * matching a keyword.
     */
    public static List<Item>
    searchAllInventories(
            Crew crew,
            String keyword) {
        String q = keyword.toLowerCase();
        return getAllHolders(crew).stream()
                .flatMap(h ->
                        h.getAllItems().stream())
                .filter(item ->
                        item.getName()
                                .toLowerCase()
                                .contains(q)
                                || item.getDescription()
                                .toLowerCase()
                                .contains(q))
                .collect(Collectors.toList());
    }

    /**
     * Get total value across all
     * inventories in the crew.
     */
    public static int
    getTotalCrewItemValue(
            Crew crew) {
        return getAllHolders(crew).stream()
                .flatMap(h ->
                        h.getAllItems().stream())
                .mapToInt(Item::getCost)
                .sum();
    }
}