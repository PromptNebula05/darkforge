package darkforge.collection;

import darkforge.model.Item;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Composition helper that manages equipped
 * items with slot enforcement. Used
 * internally by Explorer and Vehicle via
 * delegation — not exposed directly.
 *
 * @param <T> the equippable item type
 */
public class EquipmentLoadout<
        T extends Item> {

    // =========================================
    // Fields
    // =========================================

    private final List<T> equipped;
    private final int maxSlots;
    private final String ownerName;

    // =========================================
    // Constructor
    // =========================================

    public EquipmentLoadout(
            String ownerName,
            int maxSlots) {
        if (maxSlots < 0) {
            throw new IllegalArgumentException(
                    "Max slots cannot be"
                            + " negative");
        }
        this.ownerName = ownerName;
        this.maxSlots = maxSlots;
        this.equipped = new ArrayList<>();
    }

    // =========================================
    // Core operations
    // =========================================

    public boolean equip(T item) {
        if (equipped.size() >= maxSlots) {
            return false;
        }
        if (equipped.contains(item)) {
            return false;
        }
        return equipped.add(item);
    }

    public boolean unequip(T item) {
        return equipped.remove(item);
    }

    public List<T> getEquipped() {
        return Collections
                .unmodifiableList(equipped);
    }

    public boolean isEquipped(T item) {
        return equipped.contains(item);
    }

    // =========================================
    // Capacity
    // =========================================

    public int getMaxSlots() {
        return maxSlots;
    }

    public int getUsedSlots() {
        return equipped.size();
    }

    public int getRemainingSlots() {
        return maxSlots - equipped.size();
    }

    public boolean isFull() {
        return equipped.size() >= maxSlots;
    }

    // =========================================
    // Display
    // =========================================

    public String display() {
        if (equipped.isEmpty()) {
            return ownerName
                    + ": nothing equipped.";
        }
        StringBuilder sb =
                new StringBuilder();
        sb.append(ownerName)
                .append(" loadout (")
                .append(equipped.size())
                .append("/")
                .append(maxSlots)
                .append("):\n");
        for (T item : equipped) {
            sb.append("  \u2666 ")
                    .append(item.display())
                    .append("\n");
        }
        return sb.toString();
    }
}