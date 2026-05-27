package darkforge.data;

import darkforge.crew.VehicleType;
import darkforge.model.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Master equipment catalog for DARKFORGE.
 * Loaded from JSON resources at startup via
 * GameDataProvider. Provides stream-based
 * search, filter, and analytics methods.
 *
 * All query methods return new lists — the
 * catalog itself is immutable after loading.
 */
public class ItemCatalog {

    // =========================================
    // Fields
    // =========================================

    private final List<Item> allItems;
    private final Map<String, List<Item>>
            itemsByCategory;

    // =========================================
    // Constructor
    // =========================================

    public ItemCatalog(List<Item> items) {
        this.allItems =
                List.copyOf(items);
        this.itemsByCategory = allItems
                .stream()
                .collect(Collectors.groupingBy(
                        Item::getCategory,
                        Collectors
                                .toUnmodifiableList()));
    }

    // =========================================
    // Basic accessors
    // =========================================

    public List<Item> getAll() {
        return allItems;
    }

    public int size() {
        return allItems.size();
    }

    public Set<String> getCategories() {
        return Collections.unmodifiableSet(
                itemsByCategory.keySet());
    }

    // =========================================
    // Search (keyword match)
    // =========================================

    /**
     * Partial name/description match.
     * Case-insensitive.
     */
    public List<Item> search(
            String keyword) {
        String q = keyword.toLowerCase();
        return allItems.stream()
                .filter(i ->
                        i.getName().toLowerCase()
                                .contains(q)
                                || i.getDescription()
                                .toLowerCase()
                                .contains(q))
                .collect(Collectors.toList());
    }

    // =========================================
    // Filter (lambda predicate)
    // =========================================

    /**
     * Arbitrary filter using a Predicate<Item>.
     * Primary lambda entry point.
     */
    public List<Item> filter(
            Predicate<Item> predicate) {
        return allItems.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // =========================================
    // Type-safe subclass filter
    // =========================================

    /**
     * Filter by concrete Item subclass.
     * Returns type-safe list.
     */
    @SuppressWarnings("unchecked")
    public <T extends Item> List<T>
    filterByType(Class<T> type) {
        return allItems.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toList());
    }

    // =========================================
    // Category filter
    // =========================================

    public List<Item> filterByCategory(
            String category) {
        return itemsByCategory
                .getOrDefault(category,
                        List.of());
    }

    // =========================================
    // Cost range filter
    // =========================================

    public List<Item> filterByCostRange(
            int min, int max) {
        return allItems.stream()
                .filter(i ->
                        i.getCost() >= min
                                && i.getCost() <= max)
                .collect(Collectors.toList());
    }

    // =========================================
    // Weight class filter (character items)
    // =========================================

    public List<CharacterItem>
    filterByWeightClass(
            EquipmentWeight weight) {
        return allItems.stream()
                .filter(CharacterItem.class
                        ::isInstance)
                .map(CharacterItem.class::cast)
                .filter(ci -> ci.getWeightClass()
                        == weight)
                .collect(Collectors.toList());
    }

    // =========================================
    // Vehicle module compatibility
    // =========================================

    public List<VehicleModule>
    getCompatibleModules(
            VehicleType vehicleType) {
        return allItems.stream()
                .filter(VehicleModule.class
                        ::isInstance)
                .map(VehicleModule.class::cast)
                .filter(vm ->
                        vm.isCompatibleWith(
                                vehicleType))
                .collect(Collectors.toList());
    }

    // =========================================
    // Aggregation (streams)
    // =========================================

    /**
     * Category → count map using
     * Collectors.groupingBy + counting.
     */
    public Map<String, Long>
    getCategoryStats() {
        return allItems.stream()
                .collect(Collectors.groupingBy(
                        Item::getCategory,
                        Collectors.counting()));
    }

    public Optional<Item>
    getMostExpensive() {
        return allItems.stream()
                .max(Comparator.comparingInt(
                        Item::getCost));
    }

    public double getAverageCost() {
        return allItems.stream()
                .mapToInt(Item::getCost)
                .average()
                .orElse(0.0);
    }

    // =========================================
    // Tech level queries
    // =========================================

    public List<Item> filterByTechLevel(
            TechLevel level) {
        return allItems.stream()
                .filter(i -> i.getTechLevel()
                        == level)
                .collect(Collectors.toList());
    }

    public List<Item> getRestrictedItems() {
        return allItems.stream()
                .filter(Item::isRestricted)
                .collect(Collectors.toList());
    }
}