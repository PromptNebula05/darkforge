package darkforge.collection;

import darkforge.model.Item;
import java.util.List;
import java.util.function.Predicate;

/**
 * Generic interface for any entity that holds
 * an inventory of items. Enables polymorphic
 * iteration across characters and vehicles:
 *   List<InventoryHolder<?>> allHolders = ...;
 *   allHolders.stream()
 *       .flatMap(h -> h.getAllItems().stream())
 *       .filter(i -> i.getCost() > 500)
 *       .collect(toList());
 *
 * @param <T> the item type this entity holds
 */
public interface InventoryHolder<
        T extends Item> {

    /**
     * Returns the backing inventory.
     * Callers should not modify directly;
     * use addItem/removeItem instead.
     */
    Inventory<T> getInventory();

    /**
     * Add an item (e.g. from the catalog)
     * to this entity's inventory.
     * @return true if added successfully
     */
    boolean addItem(T item);

    /**
     * Remove an item from inventory.
     * @return true if removed successfully
     */
    boolean removeItem(T item);

    /**
     * Search items using a lambda predicate.
     * Primary entry point for stream-based
     * inventory queries.
     */
    List<T> searchItems(
            Predicate<T> filter);

    /**
     * Get all items as an immutable view.
     */
    List<T> getAllItems();
}