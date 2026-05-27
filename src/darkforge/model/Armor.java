package darkforge.model;

import java.util.*;

/**
 * Protective gear from Coriolis: The Great Dark
 * (Ch. 6 armor/delving suit/exo table).
 * Maps to rulebook stat columns:
 *   Name | Armor | Blight Prot. | Features |
 *   Extras | Tech | Cost
 *
 * Note: Coriolis does NOT use body-slot coverage.
 * Armor is categorized by type (armor, delving
 * suit, exo). Movement penalty is represented
 * by the "Bulky" feature (−2 Agility), not
 * a separate field.
 */
public class Armor extends CharacterItem {

    private static final long
            serialVersionUID = 5L;

    // =========================================
    // Fields
    // =========================================

    private final int armorRating;
    private final int blightProtection;
    private final int extras;
    private final List<String> features;

    // =========================================
    // Constructor
    // =========================================

    public Armor(String name,
                 String description,
                 double weight, int cost,
                 String category,
                 TechLevel techLevel,
                 boolean restricted,
                 EquipmentWeight weightClass,
                 int gearBonus,
                 int armorRating,
                 int blightProtection,
                 int extras,
                 List<String> features) {
        super(name, description, weight,
                cost, category, techLevel,
                restricted, weightClass,
                gearBonus, false);
        this.armorRating = armorRating;
        this.blightProtection =
                blightProtection;
        this.extras = extras;
        this.features =
                (features != null)
                        ? List.copyOf(features)
                        : List.of();
    }

    // =========================================
    // Getters
    // =========================================

    public int getArmorRating() {
        return armorRating;
    }

    public int getBlightProtection() {
        return blightProtection;
    }

    public int getExtras() {
        return extras;
    }

    public List<String> getFeatures() {
        return features;
    }

    // =========================================
    // Utility
    // =========================================

    public boolean isBulky() {
        return features.contains("Bulky");
    }

    public boolean isVacuumRated() {
        return features.contains(
                "Vacuum Resistant");
    }

    // =========================================
    // Item type
    // =========================================

    @Override
    public String getItemType() {
        return "armor";
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
        String ext = (extras > 0)
                ? " Extras:" + extras
                : "";
        return String.format(
                "🛡 %s [Armor %d | Blight %d]"
                        + "%s%s | %s — %d rukh",
                name, armorRating,
                blightProtection,
                ext, feat,
                getTechString(), getCost());
    }
}