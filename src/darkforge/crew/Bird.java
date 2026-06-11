package darkforge.crew;

import darkforge.model.GameEntity;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Garuda Bird companion from Coriolis: The Great Dark (Ch. 5).
 * Uses Set<GarudaPower> for power management — enforces
 * uniqueness with no capacity semantics.
 */
public class Bird extends GameEntity {

    private final BirdType type;
    private int maxHealth;
    private int currentHealth;
    private int maxEnergy;
    private int currentEnergy;
    private final Set<GarudaPower> powers;
    private final String color;
    private final String bodyFeature;
    private final String personality;

    // =========================================
    // Constructor
    // =========================================

    public Bird(String name, BirdType type,
                String color, String bodyFeature,
                String personality) {
        super(name, "A " + type.getDisplayName()
                + " bird");
        this.type = type;
        this.maxHealth = type.getStartingHealth();
        this.currentHealth = maxHealth;
        this.maxEnergy = type.getStartingEnergy();
        this.currentEnergy = maxEnergy;
        this.powers = new LinkedHashSet<>();
        this.color = color;
        this.bodyFeature = bodyFeature;
        this.personality = personality;
        initializeDefaultPowers();
    }

    // =========================================
    // Default power initialization
    // =========================================

    private void initializeDefaultPowers() {
        // 6 basic powers (all Birds)
        powers.add(new GarudaPower("Attack",
                "The Bird attacks a target",
                "Deals damage equal to energy spent",
                true, Set.of(), 1));
        powers.add(new GarudaPower("Defend",
                "The Bird shields an ally",
                "Absorbs incoming damage",
                true, Set.of(), 1));
        powers.add(new GarudaPower("Clear Blight",
                "Purge Blight from an area",
                "Removes Blight contamination",
                true, Set.of(), 1));
        powers.add(new GarudaPower("Blight Scan",
                "Scan for Blight contamination",
                "Detects Blight in the area",
                true, Set.of(), 0));
        powers.add(new GarudaPower("Soak Blight",
                "Absorb Blight into the Bird",
                "Bird takes Blight damage instead",
                true, Set.of(), 1));
        powers.add(new GarudaPower("Glow",
                "Emit radiant light",
                "Illuminates dark areas",
                true, Set.of(), 1));

        // Type-specific signature power
        switch (type) {
            case WARD:
                powers.add(new GarudaPower(
                        "Raptor's Call",
                        "Ward signature power",
                        "Rally allies with a piercing cry",
                        false,
                        EnumSet.of(BirdType.WARD), 2));
                break;
            case GUIDE:
                powers.add(new GarudaPower(
                        "Farsight",
                        "Guide signature power",
                        "See through walls and obstacles",
                        false,
                        EnumSet.of(BirdType.GUIDE), 2));
                break;
            case SPECTER:
                powers.add(new GarudaPower(
                        "Enshroud",
                        "Specter signature power",
                        "Cloak allies in shadow",
                        false,
                        EnumSet.of(BirdType.SPECTER), 2));
                break;
        }
    }

    // =========================================
    // Power management (Set<GarudaPower>)
    // =========================================

    public Set<GarudaPower> getPowers() {
        return Collections
                .unmodifiableSet(powers);
    }

    public boolean hasPower(String powerName) {
        return powers.stream()
                .anyMatch(p -> p.getName()
                        .equalsIgnoreCase(powerName));
    }

    public void learnPower(GarudaPower power) {
        powers.add(power);
    }

    public List<GarudaPower> getBasicPowers() {
        return powers.stream()
                .filter(GarudaPower::isBasic)
                .collect(Collectors.toList());
    }

    public List<GarudaPower> getAdvancedPowers() {
        return powers.stream()
                .filter(p -> !p.isBasic())
                .collect(Collectors.toList());
    }

    public List<GarudaPower>
    getAvailablePowersToLearn(
            List<GarudaPower> allPowers) {
        return allPowers.stream()
                .filter(p -> !powers.contains(p))
                .sorted(Comparator.comparingInt(
                        p -> p.getTrainingCost(type)))
                .collect(Collectors.toList());
    }

    // =========================================
    // Energy management
    // =========================================

    public boolean spendEnergy(int amount) {
        if (currentEnergy < amount) return false;
        currentEnergy -= amount;
        return true;
    }

    public void rest() {
        currentEnergy = maxEnergy;
    }

    // =========================================
    // Health management
    // =========================================

    public void takeDamage(int amount) {
        currentHealth = Math.max(0,
                currentHealth - amount);
    }

    public boolean isBroken() {
        return currentHealth <= 0;
    }

    public void heal(int amount) {
        currentHealth = Math.min(maxHealth,
                currentHealth + amount);
    }

    public void recoverPerShift() { heal(1); }

    // =========================================
    // CP upgrades
    // =========================================

    public void upgradeHealth() {
        maxHealth++;
    }

    public void upgradeEnergy() {
        maxEnergy++;
    }

    // =========================================
    // Getters
    // =========================================

    public BirdType getType() { return type; }
    public int getMaxHealth() { return maxHealth; }
    public int getCurrentHealth() {
        return currentHealth;
    }
    public int getMaxEnergy() { return maxEnergy; }
    public int getCurrentEnergy() {
        return currentEnergy;
    }
    public String getColor() { return color; }
    public String getBodyFeature() {
        return bodyFeature;
    }
    public String getPersonality() {
        return personality;
    }

    // =========================================
    // Display
    // =========================================

    @Override
    public String display() {
        return String.format(
                "%s (%s) \u2014 HP %d/%d | EP %d/%d"
                        + " | Powers: %d"
                        + " | %s, %s, %s",
                getName(), type.getDisplayName(),
                currentHealth, maxHealth,
                currentEnergy, maxEnergy,
                powers.size(),
                color, bodyFeature, personality);
    }
}