package darkforge.model;

import java.util.*;

/**
 * A weapon from Coriolis: The Great Dark
 * (Ch. 6 weapon tables). Maps directly to
 * the rulebook stat columns:
 *   Weapon | Grip | Dmg | Crit | Bonus |
 *   Range | WT | Tech | Features | Cost
 *
 * gearBonus (inherited from CharacterItem)
 * corresponds to the Bonus column (gear dice
 * added to attack rolls).
 */
public class Weapon extends CharacterItem {

    private static final long
            serialVersionUID = 4L;

    // =========================================
    // Fields
    // =========================================

    private final int damage;
    private final int critThreshold;
    private final Grip grip;
    private final String range;
    private final WeaponType weaponType;
    private final List<String> features;

    // =========================================
    // Constructor
    // =========================================

    public Weapon(String name,
                  String description,
                  double weight, int cost,
                  TechLevel techLevel,
                  boolean restricted,
                  EquipmentWeight weightClass,
                  int gearBonus,
                  int damage, int critThreshold,
                  Grip grip, String range,
                  WeaponType weaponType,
                  List<String> features) {
        super(name, description, weight,
                cost, weaponType.getDisplayName(),
                techLevel, restricted,
                weightClass, gearBonus, false);
        this.damage = damage;
        this.critThreshold = critThreshold;
        this.grip =
                (grip != null)
                        ? grip
                        : Grip.TWO_HANDED;
        this.range =
                (range != null)
                        ? range : "Engaged";
        this.weaponType =
                (weaponType != null)
                        ? weaponType
                        : WeaponType.CLOSE_COMBAT;
        this.features =
                (features != null)
                        ? List.copyOf(features)
                        : List.of();
    }

    // =========================================
    // Getters
    // =========================================

    public int getDamage() {
        return damage;
    }

    public int getCritThreshold() {
        return critThreshold;
    }

    public Grip getGrip() {
        return grip;
    }

    public String getRange() {
        return range;
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public List<String> getFeatures() {
        return features;
    }

    // =========================================
    // Item type
    // =========================================

    @Override
    public String getItemType() {
        return "weapon";
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        String feat = features.isEmpty()
                ? ""
                : " {" + String.join(", ",
                features) + "}";
        return String.format(
                "⚔ %s [%s %s] Dmg %d "
                        + "Crit %d | %s | %s%s"
                        + " — %d rukh",
                name, grip.getCode(),
                getWeightClass()
                        .getDisplayName(),
                damage, critThreshold,
                range, getTechString(),
                feat, getCost());
    }
}