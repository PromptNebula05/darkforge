package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;

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
        new Talent("Smart", "Quick thinking", TalentCategory.KNOWLEDGE, 3, "You can re-roll one die when using LOGIC"),
        new Talent("Investigator", "Finding clues", TalentCategory.KNOWLEDGE, 3, "You get +1 to investigation rolls"),
        new Talent("Librarian", "Accessing archives", TalentCategory.KNOWLEDGE, 3,
            "You can find information in any archive or database"),
        new Talent("Linguist", "Language expert", TalentCategory.SOCIAL, 3,
            "You can understand and speak additional languages"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Algebraist Apprentice", "Mathematics and computation",
            new Talent("Astrometry", "Star navigation", TalentCategory.KNOWLEDGE, 3,
                "Bonus to navigation calculations")),
        new Specialty("Analyst", "Data interpretation",
            new Talent("Data Mining", "Information extraction", TalentCategory.KNOWLEDGE, 3, "Bonus to data analysis")),
        new Specialty("Archive Master", "Record keeping",
            new Talent("Cataloguer", "Organization", TalentCategory.KNOWLEDGE, 3,
                "Bonus to finding stored information")),
        new Specialty("Excavation Leader", "Archaeological digs",
            new Talent("Archaeology", "Ancient sites", TalentCategory.KNOWLEDGE, 3, "Bonus to excavation rolls")),
        new Specialty("Negotiator", "Diplomatic resolution",
            new Talent("Diplomat", "Conflict resolution", TalentCategory.SOCIAL, 3, "Bonus to negotiation")),
        new Specialty("Quartermaster", "Resource management",
            new Talent("Logistics", "Supply management", TalentCategory.KNOWLEDGE, 3, "Bonus to supply management")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(
            new Equipment("Backpack", "Standard carry gear", EquipmentWeight.LIGHT),
            new Equipment("Fusillard Pistol", "Standard sidearm", EquipmentWeight.REGULAR, 2),
            new Equipment("Light Delving Suit", "Basic protective gear", EquipmentWeight.REGULAR, 1)),
        List.of(
            new Equipment("Astrolabe", "Navigation instrument", EquipmentWeight.LIGHT, 2),
            new Equipment("Cartographer's Kit", "Mapping tools", EquipmentWeight.LIGHT, 1),
            new Equipment("Compass", "Directional tool", EquipmentWeight.TINY, 1)),
        List.of(
            new Equipment("Portable Terminal", "Computing device", EquipmentWeight.LIGHT, 2),
            new Equipment("Research Notes", "Collected findings", EquipmentWeight.TINY, 1),
            new Equipment("Magnifying Lens", "Examination tool", EquipmentWeight.TINY, 1)));
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
}
