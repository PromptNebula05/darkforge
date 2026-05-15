package darkforge;

import darkforge.model.*;
import darkforge.model.profession.*;
import darkforge.mechanics.*;
import darkforge.creation.ExplorerFactory;
import darkforge.display.CharacterSheetFormatter;

import java.util.*;

/**
 * DARKFORGE v0.1 — Iteration 1 OOP Technique Demonstration.
 *
 * Demonstrates all CS622 Module 1 required techniques:
 * - Abstract classes (GameEntity, Explorer)
 * - Interfaces (Rollable, Displayable)
 * - Inheritance hierarchy (8 profession subclasses)
 * - Polymorphism (display(), getKeyAttribute(), roll())
 * - Upcasting (profession → GameEntity[])
 * - Downcasting (instanceof + cast for profession-specific queries)
 */
public class Main {

  public static void main(String[] args) {
    System.out.println("══════════════════════════════════════════════════════");
    System.out.println("     DARKFORGE v0.1 — OOP Technique Demonstration");
    System.out.println("══════════════════════════════════════════════════════");

    // ── Section 1: Explorer Creation (Factory Method) ──────────────
    System.out.println("\n--- Section 1: Explorer Creation via Factory ---");
    System.out.println("Creating 3 Explorers of different professions...\n");

    ExplorerFactory factory = new ExplorerFactory();

    EnumMap<Attribute, Integer> scholarAttrs = new EnumMap<>(Attribute.class);
    scholarAttrs.put(Attribute.STRENGTH, 2);
    scholarAttrs.put(Attribute.AGILITY, 4);
    scholarAttrs.put(Attribute.LOGIC, 6);
    scholarAttrs.put(Attribute.PERCEPTION, 3);
    scholarAttrs.put(Attribute.INSIGHT, 4);
    scholarAttrs.put(Attribute.EMPATHY, 5);

    EnumMap<Attribute, Integer> enforcerAttrs = new EnumMap<>(Attribute.class);
    enforcerAttrs.put(Attribute.STRENGTH, 4);
    enforcerAttrs.put(Attribute.AGILITY, 6);
    enforcerAttrs.put(Attribute.LOGIC, 3);
    enforcerAttrs.put(Attribute.PERCEPTION, 4);
    enforcerAttrs.put(Attribute.INSIGHT, 3);
    enforcerAttrs.put(Attribute.EMPATHY, 4);

    EnumMap<Attribute, Integer> artistAttrs = new EnumMap<>(Attribute.class);
    artistAttrs.put(Attribute.STRENGTH, 3);
    artistAttrs.put(Attribute.AGILITY, 3);
    artistAttrs.put(Attribute.LOGIC, 4);
    artistAttrs.put(Attribute.PERCEPTION, 4);
    artistAttrs.put(Attribute.INSIGHT, 4);
    artistAttrs.put(Attribute.EMPATHY, 6);

    Explorer scholar = factory.createExplorer(
        "Scholar", "Cantara Loutreides",
        1, 1,
        scholarAttrs,
        new int[] { 1, 1, 1, 0 },
        "Constantly reading", "Worn silver coin from the Old Horizon",
        "Sharp eyes, ink-stained fingers");

    Explorer enforcer = factory.createExplorer(
        "Enforcer", "Kaan Verros",
        2, 2,
        enforcerAttrs,
        new int[] { 1, 1, 0, 1 },
        "Always scanning for threats", "Dented combat knife",
        "Scarred forearms, military posture");

    Explorer artist = factory.createExplorer(
        "Artist", "Lysa Denn",
        3, 3,
        artistAttrs,
        new int[] { 1, 0, 1, 1 },
        "Hums constantly", "Antique flute",
        "Graceful movements, colorful clothing");

    System.out.println("Created: " + scholar.getName() + " (Scholar)");
    System.out.println("Created: " + enforcer.getName() + " (Enforcer)");
    System.out.println("Created: " + artist.getName() + " (Artist)");

    // ── Section 2: Upcasting (CS622 Requirement) ───────────────────
    System.out.println("\n--- Section 2: Upcasting ---");
    System.out.println("Storing 3 profession-specific Explorers as GameEntity[] roster.");
    System.out.println("This is implicit upcasting: Scholar/Enforcer/Artist → GameEntity.");
    System.out.println("Enables uniform iteration over mixed profession types.\n");

    GameEntity[] roster = { scholar, enforcer, artist };

    System.out.println("Roster contains " + roster.length + " entities (stored as GameEntity):");
    for (int i = 0; i < roster.length; i++) {
      System.out.println("  [" + i + "] Runtime type: " + roster[i].getClass().getSimpleName());
    }

    // ── Section 3: Polymorphism via GameEntity.display() ───────────
    System.out.println("\n--- Section 3: Polymorphism via GameEntity.display() ---");
    System.out.println("Calling entity.display() on each roster member.");
    System.out.println("Same method call, different output per profession (runtime dispatch):\n");

    for (GameEntity entity : roster) {
      System.out.println("  " + entity.display());
    }

    // ── Section 4: Downcasting with instanceof (CS622 Requirement) ─
    System.out.println("\n--- Section 4: Downcasting with instanceof ---");
    System.out.println("Accessing profession-specific methods requires narrowing from GameEntity.");
    System.out.println("These methods don't exist on GameEntity or Explorer — only on subclasses.\n");

    for (GameEntity entity : roster) {
      if (entity instanceof Scholar s) {
        System.out.println("  " + s.getName() + " (Scholar) research bonus: " + s.getResearchBonus());
      } else if (entity instanceof Enforcer e) {
        System.out.println("  " + e.getName() + " (Enforcer) weapon talents: " + e.getWeaponTalents());
      } else if (entity instanceof Artist a) {
        System.out.println("  " + a.getName() + " (Artist) performance bonus: " + a.getPerformanceBonus());
      }
    }

    // ── Section 5: Interface Polymorphism via Rollable ─────────────
    System.out.println("\n--- Section 5: Interface Polymorphism via Rollable ---");
    System.out.println("DicePool and D66Table both implement Rollable.");
    System.out.println("Same roll() call, fundamentally different behavior:\n");

    Random rng = new Random(42);
    DicePool dicePool = new DicePool(5, 2, rng);

    Map<Integer, String> demoD66Map = new HashMap<>();
    for (int tens = 1; tens <= 6; tens++) {
      for (int ones = 1; ones <= 6; ones++) {
        demoD66Map.put(tens * 10 + ones, "Entry-" + tens + ones);
      }
    }
    D66Table<String> d66Table = new D66Table<>(demoD66Map, new Random(42));

    Rollable[] mechanics = { dicePool, d66Table };
    for (Rollable r : mechanics) {
      System.out.println("  " + r.getClass().getSimpleName() + ".roll() → " + r.roll());
    }
    System.out.println("  DicePool counts 6s (successes). D66Table generates a table index.");

    // ── Section 6: Full Character Sheets (Displayable) ─────────────
    System.out.println("\n--- Section 6: Full Character Sheets via CharacterSheetRenderer ---");
    System.out.println("CharacterSheetRenderer implements Displayable (not a GameEntity).\n");

    for (GameEntity entity : roster) {
      if (entity instanceof Explorer exp) {
        CharacterSheetFormatter renderer = new CharacterSheetFormatter(exp);
        System.out.println(renderer.toFormattedString());
      }
    }

    // ── Attribute Validation Demo ──────────────────────────────────
    System.out.println("\n--- Bonus: Attribute Validation Demo ---");
    System.out.println("Demonstrating constraint validation with invalid attributes:\n");

    EnumMap<Attribute, Integer> invalidAttrs = new EnumMap<>(Attribute.class);
    invalidAttrs.put(Attribute.STRENGTH, 5);
    invalidAttrs.put(Attribute.AGILITY, 5);
    invalidAttrs.put(Attribute.LOGIC, 5);
    invalidAttrs.put(Attribute.PERCEPTION, 5);
    invalidAttrs.put(Attribute.INSIGHT, 5);
    invalidAttrs.put(Attribute.EMPATHY, 5);

    try {
      AttributeDistributor.validate(invalidAttrs, Attribute.LOGIC);
    } catch (IllegalArgumentException e) {
      System.out.println("  REJECTED: " + e.getMessage());
    }

    System.out.println("\n══════════════════════════════════════════════════════");
    System.out.println("     DARKFORGE v0.1 — Demonstration Complete");
    System.out.println("══════════════════════════════════════════════════════");
  }
}
