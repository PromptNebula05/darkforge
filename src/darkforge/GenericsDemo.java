package darkforge;

import darkforge.collection.*;
import darkforge.crew.*;
import darkforge.data.GameDataProvider;
import darkforge.mechanics.*;
import darkforge.model.*;

import java.util.*;

/**
 * Standalone demonstration of every CS622 Module 3
 * generic pattern used in DARKFORGE. Run directly
 * for assignment deliverable screenshots.
 */
public class GenericsDemo {

    public static void main(String[] args) {
        GameDataProvider.getTheInstance()
                .initialize();

        System.out.println("=".repeat(50));
        System.out.println(
                "  DARKFORGE — Generics & Collections"
                        + " Demo");
        System.out.println("=".repeat(50));

        demoBoundedGenericClass();
        demoGenericInterface();
        demoWildcardBounds();
        demoTwoParameterGeneric();
        demoCollectionClasses();
        demoCollectionsUtilities();
        demoComparatorChains();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("  Demo complete.");
        System.out.println("=".repeat(50));
    }

    // =========================================
    // 1. Bounded generic class
    // =========================================

    private static void demoBoundedGenericClass() {
        System.out.println(
                "\n--- 1. Inventory<T extends"
                        + " GameEntity> ---");

        // Same class, two different type parameters
        Inventory<Equipment> gear =
                new Inventory<>("Demo Explorer", 8);
        Inventory<Talent> talents =
                new Inventory<>("Demo Explorer", 10);

        Equipment sword = new Equipment(
                "Vulcan Pistol",
                "Standard sidearm",
                EquipmentWeight.REGULAR, 1, true);
        Equipment compass = new Equipment(
                "Compass", "Navigation tool",
                EquipmentWeight.LIGHT);

        gear.add(sword);
        gear.add(compass);
        System.out.println(gear.display());

        // T extends GameEntity enables getName()
        Equipment found =
                gear.getByName("vulcan pistol");
        System.out.println(
                "getByName: " + found.display());

        Talent smart = new Talent(
                "Smart", "Quick learner",
                TalentCategory.KNOWLEDGE, 3,
                "+1 to knowledge rolls");
        talents.add(smart);
        System.out.println(
                "Talent search: "
                        + talents.findByName("smart"));

        // Compile-time safety:
        // gear.add(smart); // Won't compile
        System.out.println(
                "  (gear.add(talent) would not compile"
                        + " — bounded type parameter)");
    }

    // =========================================
    // 2. Generic interface — Selectable<T>
    // =========================================

    private static void demoGenericInterface() {
        System.out.println(
                "\n--- 2. Selectable<T> Interface ---");

        Map<Integer, String> d6Map = Map.of(
                1, "Alpha", 2, "Beta",
                3, "Gamma", 4, "Delta",
                5, "Epsilon", 6, "Zeta");
        Map<Integer, String> d66Map =
                new LinkedHashMap<>();
        for (int tens = 1; tens <= 6; tens++) {
            for (int ones = 1; ones <= 6; ones++) {
                d66Map.put(tens * 10 + ones,
                        "Entry-" + tens + ones);
            }
        }

        // Three implementations, one interface
        Selectable<String> table1 =
                new D6Table<>(d6Map);
        Selectable<String> table2 =
                new D66Table<>(d66Map);

        Map<String, Double> weights = Map.of(
                "Common", 7.0,
                "Uncommon", 2.0,
                "Rare", 1.0);
        Selectable<String> table3 =
                new WeightedSelector<>(weights);

        // Polymorphic dispatch
        System.out.println(
                "D6 select(3): "
                        + table1.select(3));
        System.out.println(
                "D66 select(24): "
                        + table2.select(24));
        System.out.println(
                "Weighted random: "
                        + table3.selectRandom(
                        new Random(42)));
        System.out.printf(
                "Sizes: D6=%d, D66=%d,"
                        + " Weighted=%d%n",
                table1.size(), table2.size(),
                table3.size());
    }

    // =========================================
    // 3. Wildcard bounds (PECS)
    // =========================================

    private static void demoWildcardBounds() {
        System.out.println(
                "\n--- 3. Wildcard Bounds ---");

        Inventory<GameEntity> mixed =
                new Inventory<>("Mixed", -1);

        // ? extends T — producer
        List<Equipment> guildGear = List.of(
                new Equipment("Rope", "50m",
                        EquipmentWeight.REGULAR),
                new Equipment("Lantern", "Bright",
                        EquipmentWeight.LIGHT));
        mixed.addAll(guildGear);
        System.out.println(
                "addAll(List<Equipment>) into"
                        + " Inventory<GameEntity>: "
                        + mixed.size() + " items");

        // ? super T — consumer
        List<GameEntity> filtered =
                mixed.filter(e -> e.getName()
                        .length() > 4);
        System.out.println(
                "filter(Predicate<? super T>): "
                        + filtered.size() + " matches");

        // sort with Comparator<? super T>
        mixed.sort(Comparator.comparing(
                GameEntity::getName));
        System.out.println(
                "sort(Comparator<? super T>): "
                        + mixed.map(
                        GameEntity::getName));
    }

    // =========================================
    // 4. Two-parameter generic
    // =========================================

    private static void demoTwoParameterGeneric() {
        System.out.println(
                "\n--- 4. Registry<K, V extends"
                        + " GameEntity> ---");

        Registry<TalentCategory, Talent> reg =
                new Registry<>();

        Talent sharpshooter = new Talent(
                "Sharpshooter", "Precise aim",
                TalentCategory.COMBAT, 3,
                "+1 to ranged attacks");
        Talent brawler = new Talent(
                "Brawler", "Street fighter",
                TalentCategory.COMBAT, 2,
                "+1 to melee");
        Talent scholar = new Talent(
                "Educated", "Well-read",
                TalentCategory.KNOWLEDGE, 3,
                "+1 to knowledge");

        reg.register(
                TalentCategory.COMBAT,
                sharpshooter);
        reg.register(
                TalentCategory.COMBAT, brawler);
        reg.register(
                TalentCategory.KNOWLEDGE, scholar);

        System.out.println(
                "COMBAT talents: "
                        + reg.countByKey(
                        TalentCategory.COMBAT));
        System.out.println(
                "KNOWLEDGE talents: "
                        + reg.countByKey(
                        TalentCategory.KNOWLEDGE));
        System.out.println(
                "Total: " + reg.totalCount());

        // Filtered getAll with wildcard
        List<Talent> maxLevel3 =
                reg.getAll(
                        t -> t.getMaxLevel() == 3);
        System.out.println(
                "Max level 3: "
                        + maxLevel3.size());
    }

    // =========================================
    // 5. Collection classes
    // =========================================

    private static void demoCollectionClasses() {
        System.out.println(
                "\n--- 5. Collection Classes ---");

        // EnumMap<CrewRole, Explorer>
        System.out.println(
                "EnumMap<CrewRole, Explorer>:");
        EnumMap<CrewRole, String> roleDemo =
                new EnumMap<>(CrewRole.class);
        for (CrewRole role : CrewRole.values()) {
            roleDemo.put(role,
                    role.getDisplayName()
                            + " (fitness-based)");
        }
        roleDemo.forEach((k, v) ->
                System.out.println(
                        "  " + k + " → " + v));

        // Set<GarudaPower> — no duplicates
        System.out.println(
                "\nSet<GarudaPower> (Bird powers):");
        Bird demoBird = new Bird(
                "Jade", BirdType.GUIDE,
                "Green", "Sleek", "Curious");
        System.out.println(
                "  Powers: "
                        + demoBird.getPowers().size());
        System.out.println(
                "  Immutable: "
                        + demoBird.getPowers().getClass()
                        .getSimpleName());

        // LinkedHashMap — insertion order
        System.out.println(
                "\nLinkedHashMap (equipment sources):"
                        + " preserves insertion order");
    }

    // =========================================
    // 6. Collections utilities
    // =========================================

    private static void demoCollectionsUtilities() {
        System.out.println(
                "\n--- 6. Collections Utilities ---");

        // Unmodifiable views
        Inventory<Equipment> inv =
                new Inventory<>("Test", 5);
        inv.add(new Equipment(
                "Item", "Test",
                EquipmentWeight.REGULAR));
        List<Equipment> view = inv.getAll();

        try {
            view.add(new Equipment(
                    "Hack", "x",
                    EquipmentWeight.TINY));
        } catch (
                UnsupportedOperationException e) {
            System.out.println(
                    "Collections.unmodifiableList()"
                            + " blocks modification ✓");
        }

        // Collections.sort via Inventory.sort()
        inv.add(new Equipment(
                "Alpha", "First",
                EquipmentWeight.LIGHT));
        inv.add(new Equipment(
                "Zulu", "Last",
                EquipmentWeight.HEAVY));
        inv.sort(Comparator.comparing(
                GameEntity::getName));
        System.out.println(
                "Sorted: " + inv.map(
                        GameEntity::getName));
    }

    // =========================================
    // 7. Comparator chains
    // =========================================

    private static void demoComparatorChains() {
        System.out.println(
                "\n--- 7. Comparator Chains ---");

        // Show composable comparators
        System.out.println(
                "ExplorerComparators available:");
        System.out.println(
                "  byAttribute(attr).reversed()");
        System.out.println(
                "  byProfession()"
                        + ".thenComparing(byName())");
        System.out.println(
                "  byRoleFitness(role).reversed()");
        System.out.println(
                "  byHealth(), byHope(), byHeart()");

        // Demonstrate Comparator.thenComparing()
        List<String> names = List.of(
                "Zara", "Abel", "Zara", "Abel");
        List<Integer> scores = List.of(
                5, 3, 8, 7);
        record Entry(String name, int score) {}
        List<Entry> entries = new ArrayList<>();
        for (int i = 0;
             i < names.size(); i++) {
            entries.add(
                    new Entry(names.get(i),
                            scores.get(i)));
        }
        entries.sort(
                Comparator.comparing(
                                Entry::name)
                        .thenComparingInt(
                                Entry::score));
        System.out.println(
                "  Composed sort result:");
        for (Entry e : entries) {
            System.out.printf(
                    "    %s (score %d)%n",
                    e.name(), e.score());
        }
    }
}