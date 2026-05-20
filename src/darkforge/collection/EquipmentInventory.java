package darkforge.collection;

import darkforge.model.Equipment;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Weight-based specialization of Inventory<Equipment>.
 * Overrides add() to enforce Coriolis Ch. 6 encumbrance:
 * an Explorer can carry items totaling STR + 4 weight points.
 */
public class EquipmentInventory
        extends Inventory<Equipment> {

    private final double maxWeight;

    public EquipmentInventory(String ownerName,
                              int strengthPlusCarry) {
        super(ownerName, -1);
        this.maxWeight = strengthPlusCarry;
    }

    // =========================================
    // Overridden add — weight-based capacity
    // =========================================

    @Override
    public boolean add(Equipment item) {
        if (getCurrentWeight()
                + item.getWeight().getWeightValue()
                > maxWeight) {
            return false;
        }
        return super.add(item);
    }

    // =========================================
    // Weight tracking
    // =========================================

    // Sum of all item weights.
    public double getCurrentWeight() {
        return getAll().stream()
                .mapToDouble(e -> e.getWeight()
                        .getWeightValue())
                .sum();
    }

    // STR + 4 carry limit (Ch. 6).
    public double getMaxWeight() {
        return maxWeight;
    }

    // Current weight exceeds max → movement penalty.
    public boolean isOverEncumbered() {
        return getCurrentWeight() > maxWeight;
    }

    // =========================================
    // Weapons at hand
    // =========================================

    // Max 3 weapons readily available (Ch. 6 rule).
    public List<Equipment> getWeaponsAtHand() {
        return getAll().stream()
                .filter(Equipment::isWeapon)
                .limit(3)
                .collect(Collectors.toList());
    }
}