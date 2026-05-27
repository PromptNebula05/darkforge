package darkforge.collection;

import darkforge.model.Item;
import java.util.List;

/**
 * Generic interface for entities that can
 * equip items to slots/hardpoints.
 * Characters equip weapons to body slots;
 * vehicles install modules to hardpoints.
 *
 * Equip semantics are type-safe:
 *   Explorer implements Equippable<Weapon>
 *   Vehicle implements Equippable<VehicleModule>
 *
 * @param <T> the equippable item type
 */
public interface Equippable<
        T extends Item> {

    /**
     * Equip an item to a slot/hardpoint.
     * @return true if equipped successfully
     *         (false if no slots available)
     */
    boolean equip(T item);

    /**
     * Unequip an item from its slot.
     * @return true if unequipped successfully
     */
    boolean unequip(T item);

    /**
     * Get all currently equipped items.
     */
    List<T> getEquipped();

    /**
     * Check if a specific item is equipped.
     */
    boolean isEquipped(T item);
}