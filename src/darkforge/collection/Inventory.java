package darkforge.collection;

import darkforge.model.GameEntity;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Bounded generic container for game entities.
 * T extends GameEntity enables getName() and display()
 * calls without casting.
 */
public class Inventory<T extends GameEntity>
        implements Iterable<T> {

    private final List<T> items;
    private final int capacityLimit; // -1 = unlimited
    private final String ownerName;

    public Inventory(String ownerName,
                     int capacityLimit) {
        this.ownerName = ownerName;
        this.capacityLimit = capacityLimit;
        this.items = new ArrayList<>();
    }

    // =========================================
    // Core operations
    // =========================================

    // Add item if capacity permits.
    public boolean add(T item) {
        if (capacityLimit >= 0
                && items.size() >= capacityLimit)
            return false;
        return items.add(item);
    }

    // Remove by reference equality.
    public boolean remove(T item) {
        return items.remove(item);
    }

    // Case-insensitive lookup via getName().
    public T getByName(String name) {
        return items.stream()
                .filter(i -> i.getName()
                        .equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    // Partial match search.
    public List<T> findByName(String query) {
        String q = query.toLowerCase();
        return items.stream()
                .filter(i -> i.getName().toLowerCase()
                        .contains(q))
                .collect(Collectors.toList());
    }

    public boolean contains(T item) {
        return items.contains(item);
    }

    public int size() { return items.size(); }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int remainingCapacity() {
        return capacityLimit < 0
                ? Integer.MAX_VALUE
                : capacityLimit - items.size();
    }

    // =========================================
    // Generic collection operations
    // =========================================

    // WILDCARD BOUND: accepts List<Equipment>
    // into an Inventory<GameEntity>.
    public void addAll(
            Collection<? extends T> newItems) {
        for (T item : newItems) {
            add(item);
        }
    }

    // Immutable view via Collections.unmodifiableList().
    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    // WILDCARD BOUND on predicate.
    public List<T> filter(
            Predicate<? super T> predicate) {
        return items.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    // WILDCARD BOUND on comparator. In-place sort.
    public void sort(
            Comparator<? super T> comparator) {
        items.sort(comparator);
    }

    // Transform elements.
    public <R> List<R> map(
            Function<? super T,
                    ? extends R> mapper) {
        return items.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    // =========================================
    // Iterator support
    // =========================================

    // Enables enhanced for-loops.
    @Override
    public Iterator<T> iterator() {
        return Collections
                .unmodifiableList(items).iterator();
    }

    // =========================================
    // Display
    // =========================================

    // Formatted listing using each item's display().
    public String display() {
        if (items.isEmpty())
            return ownerName + "'s inventory"
                    + " is empty.";
        StringBuilder sb = new StringBuilder();
        sb.append(ownerName).append("'s inventory")
                .append(" (").append(size());
        if (capacityLimit >= 0)
            sb.append("/").append(capacityLimit);
        sb.append("):\n");
        for (T item : items) {
            sb.append("  \u2022 ")
                    .append(item.display())
                    .append("\n");
        }
        return sb.toString();
    }

    // =========================================
    // Getters
    // =========================================

    public String getOwnerName() {
        return ownerName;
    }

    public int getCapacityLimit() {
        return capacityLimit;
    }
}