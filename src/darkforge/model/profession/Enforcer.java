package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class Enforcer extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.AGILITY;

  public Enforcer(String name) {
    super(name, "Enforcer explorer");
  }

  @Override
  public String getProfessionName() {
    return "Enforcer";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Tough", "Physical resilience", TalentCategory.COMBAT, 3, "You can re-roll one die in close combat"),
        new Talent("Nimble", "Quick reflexes", TalentCategory.COMBAT, 3, "You get +1 to initiative rolls"),
        new Talent("Intimidating", "Threatening presence", TalentCategory.SOCIAL, 3,
            "You get +1 to intimidation rolls"),
        new Talent("Sharpshooter", "Ranged accuracy", TalentCategory.COMBAT, 3, "You get +1 to ranged combat rolls"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Bodyguard", "Personal protection",
            new Talent("Shield Bearer", "Defensive stance", TalentCategory.COMBAT, 3, "Bonus to protection rolls")),
        new Specialty("Bounty Hunter", "Tracking targets",
            new Talent("Tracker", "Following trails", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to tracking")),
        new Specialty("Corsair", "Ship-to-ship combat",
            new Talent("Boarding Party", "Assault tactics", TalentCategory.COMBAT, 3, "Bonus to boarding actions")),
        new Specialty("Duelist", "One-on-one combat",
            new Talent("Blade Dancer", "Melee finesse", TalentCategory.COMBAT, 3, "Bonus in duels")),
        new Specialty("Soldier", "Military operations",
            new Talent("Tactician", "Battle strategy", TalentCategory.COMBAT, 3, "Bonus to tactical decisions")),
        new Specialty("Warden", "Security enforcement",
            new Talent("Vigilant", "Watchful guard", TalentCategory.INSIGHT, 3, "Bonus to guard duty")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Fusillard Rifle", "Standard-issue rifle", EquipmentWeight.REGULAR, 2),
            new Equipment("Combat Armor", "Basic protection", EquipmentWeight.HEAVY, 3),
            new Equipment("Combat Knife", "Melee sidearm", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Vulcan Pistol", "Heavy sidearm", EquipmentWeight.REGULAR, 3),
            new Equipment("Shield", "Defensive gear", EquipmentWeight.REGULAR, 2),
            new Equipment("Stun Grenade", "Non-lethal ordinance", EquipmentWeight.TINY, 0)),
        List.of(new Equipment("Thermal Lance", "Energy weapon", EquipmentWeight.REGULAR, 3),
            new Equipment("Light Delving Suit", "Protective gear", EquipmentWeight.REGULAR, 1),
            new Equipment("Binoculars", "Surveillance tool", EquipmentWeight.LIGHT, 1)));
  }

  public List<String> getWeaponTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.COMBAT)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }
}
