package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;

public class Roughneck extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.STRENGTH;

  public Roughneck(String name) {
    super(name, "Roughneck explorer");
  }

  @Override
  public String getProfessionName() {
    return "Roughneck";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Brawler", "Unarmed combat", TalentCategory.COMBAT, 3, "You get +1 to unarmed combat rolls"),
        new Talent("Hardy", "Physical toughness", TalentCategory.RESILIENCE, 3,
            "You can ignore one point of damage per session"),
        new Talent("Lifter", "Raw strength", TalentCategory.RESILIENCE, 3, "You get +1 to feats of strength"),
        new Talent("Mechanic", "Repair skills", TalentCategory.EQUIPMENT, 3, "You get +1 to mechanical repair rolls"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Breaker", "Demolition expert",
            new Talent("Demolisher", "Structural destruction", TalentCategory.COMBAT, 3, "Bonus to demolition")),
        new Specialty("Driver", "Vehicle operator",
            new Talent("Pilot", "Vehicle handling", TalentCategory.VEHICLE_EXO, 3, "Bonus to driving")),
        new Specialty("Miner", "Extraction worker",
            new Talent("Excavator", "Mining operations", TalentCategory.EQUIPMENT, 3, "Bonus to mining rolls")),
        new Specialty("Rigger", "Heavy equipment operator",
            new Talent("Crane Operator", "Heavy machinery", TalentCategory.VEHICLE_EXO, 3, "Bonus to heavy equipment")),
        new Specialty("Smith", "Metal working",
            new Talent("Forger", "Metal crafting", TalentCategory.EQUIPMENT, 3, "Bonus to smithing")),
        new Specialty("Wrestler", "Grappling specialist",
            new Talent("Grappler", "Wrestling holds", TalentCategory.COMBAT, 3, "Bonus to grappling")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Sledgehammer", "Heavy melee", EquipmentWeight.HEAVY, 2),
            new Equipment("Work Gloves", "Hand protection", EquipmentWeight.TINY, 1),
            new Equipment("Hard Hat", "Head protection", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Tool Kit", "Repair tools", EquipmentWeight.REGULAR, 2),
            new Equipment("Fusillard Pistol", "Sidearm", EquipmentWeight.REGULAR, 2),
            new Equipment("Rope", "Utility rope", EquipmentWeight.LIGHT)),
        List.of(new Equipment("Exo-Loader Arms", "Powered assistance", EquipmentWeight.HEAVY, 3),
            new Equipment("Protective Coveralls", "Work clothes", EquipmentWeight.LIGHT, 1),
            new Equipment("Canteen", "Water supply", EquipmentWeight.TINY)));
  }

  public int getEnduranceBonus() {
    int bonus = 0;
    for (Talent t : getTalents()) {
      if (t.getCategory() == TalentCategory.RESILIENCE) {
        bonus += t.getCurrentLevel();
      }
    }
    return bonus;
  }
}
