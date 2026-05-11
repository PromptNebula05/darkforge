package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class Scoundrel extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.AGILITY;

  public Scoundrel(String name) {
    super(name, "Scoundrel explorer");
  }

  @Override
  public String getProfessionName() {
    return "Scoundrel";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Sneaky", "Moving unseen", TalentCategory.STEALTH_MOBILITY, 3,
            "You can re-roll one die when sneaking"),
        new Talent("Cunning", "Clever tricks", TalentCategory.SOCIAL, 3, "You get +1 to deception rolls"),
        new Talent("Nimble Fingers", "Sleight of hand", TalentCategory.STEALTH_MOBILITY, 3,
            "You get +1 to pickpocket and lock-picking rolls"),
        new Talent("Escape Artist", "Breaking free", TalentCategory.STEALTH_MOBILITY, 3, "You get +1 to escape rolls"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Assassin", "Silent killing",
            new Talent("Death Strike", "Lethal precision", TalentCategory.COMBAT, 3, "Bonus to sneak attacks")),
        new Specialty("Burglar", "Breaking and entering",
            new Talent("Cat Burglar", "Infiltration", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to breaking in")),
        new Specialty("Con Artist", "Confidence schemes",
            new Talent("Silver Tongue", "Convincing lies", TalentCategory.SOCIAL, 3, "Bonus to deception")),
        new Specialty("Hacker", "Digital intrusion",
            new Talent("Code Breaker", "System penetration", TalentCategory.KNOWLEDGE, 3, "Bonus to hacking")),
        new Specialty("Smuggler", "Illegal transport",
            new Talent("Hidden Compartments", "Concealment", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to smuggling")),
        new Specialty("Spy", "Intelligence gathering",
            new Talent("Shadow", "Following targets", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to surveillance")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Lockpick Set", "Security tools", EquipmentWeight.TINY, 2),
            new Equipment("Grappling Hook", "Climbing aid", EquipmentWeight.LIGHT, 1),
            new Equipment("Dark Clothing", "Stealth attire", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Silenced Pistol", "Quiet sidearm", EquipmentWeight.REGULAR, 2),
            new Equipment("Smoke Grenades", "Visual cover", EquipmentWeight.TINY, 0),
            new Equipment("Communicator", "Encrypted comms", EquipmentWeight.TINY, 1)),
        List.of(new Equipment("Disguise Kit", "Identity tools", EquipmentWeight.LIGHT, 2),
            new Equipment("Forged Documents", "Fake identities", EquipmentWeight.TINY, 1),
            new Equipment("Fusillard Pistol", "Sidearm", EquipmentWeight.REGULAR, 2)));
  }

  public List<String> getDeceptionTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.STEALTH_MOBILITY)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }
}
