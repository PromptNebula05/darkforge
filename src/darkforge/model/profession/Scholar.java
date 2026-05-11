package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import darkforge.mechanics.D6Table;

public class Scholar extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.LOGIC;

  public Scholar(String name) {
    super(name, "Scholar explorer");
  }

  @Override
  public String getProfessionName() {
    return "Scholar";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Investigator", "Searching for clues", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level when searching an area for clues"),
        new Talent("Librarian", "Using libraries and info cubes", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level when using a library or info cubes to find information"),
        new Talent("Smart", "Pushing Logic rolls", TalentCategory.RESILIENCE, 1,
            "You can push any roll based on Logic twice"),
        new Talent("Teratology", "Understanding beasts", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level to know and understand the beasts of the Great Dark"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Guild Archivist", "Archiving, cataloging, and organizing Guild knowledge",
            new Talent("Librarian", "Using libraries and info cubes", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level when using a library or info cubes to find information")),
        new Specialty("Algebraist Apprentice", "Studying the old art of al-jabr and the threads of the universe",
            new Talent("Astrometry", "Space phenomena and orbital mechanics", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level to understand space phenomena, orbital mechanics, and planetology")),
        new Specialty("Slipstream Cartographer", "Mapping uncharted territories and star lanes",
            new Talent("Cartographer", "Mapping and navigating", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level when mapping and navigating during treks")),
        new Specialty("Diaspora Historian", "Studying the history of the Diaspora fleet and modern society",
            new Talent("Historian", "Historical knowledge", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level to know historical facts about the Diaspora, the Lost Horizon, and the Old Horizon")),
        new Specialty("Cave Botanist", "Studying plants, fungi, and the Blight in the Cave Gardens",
            new Talent("Botanist", "Cultivating and understanding flora", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level for cultivating plants or fungi as well as understanding flora and ivy")),
        new Specialty("Builder Archaeologist", "Examining Builder ruins, shards, and monuments",
            new Talent("Archaeology", "Understanding ruins and monuments", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level for understanding ruins and monuments")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(
            new Equipment("Portable Lab", "Portable laboratory equipment", EquipmentWeight.REGULAR, 2),
            new Equipment("MediKit (Basic)", "Basic medical supplies", EquipmentWeight.LIGHT, 1),
            new Equipment("Old Paper Journal", "Worn journal for recording findings", EquipmentWeight.TINY)),
        List.of(
            new Equipment("Astrolabe", "Navigation instrument", EquipmentWeight.LIGHT, 2),
            new Equipment("Cartographer's Kit", "Mapping tools", EquipmentWeight.LIGHT, 1),
            new Equipment("Compass", "Directional tool", EquipmentWeight.TINY, 1)),
        List.of(
            new Equipment("Info Cube", "Data storage and retrieval device", EquipmentWeight.TINY, 1),
            new Equipment("Fancy Clothing", "Impressive attire", EquipmentWeight.LIGHT),
            new Equipment("Waking Pills", "Stimulant pills to stay alert", EquipmentWeight.TINY)));
  }

  public int getResearchBonus() {
    int bonus = 0;
    for (Talent t : getTalents()) {
      if (t.getCategory() == TalentCategory.KNOWLEDGE) {
        bonus += t.getCurrentLevel();
      }
    }
    return bonus;
  }

  /** Sample first names for Scholar (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Aramande", 2, "Cantara", 3, "Janousch", 4, "Leito", 5, "Keityl", 6, "Samsand"));
  }

  /** Sample surnames for Scholar (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(Map.of(1, "Aramiz", 2, "Loutreides", 3, "Thyrum", 4, "Jorbana", 5, "Miesma", 6, "Pertesses"));
  }
}
