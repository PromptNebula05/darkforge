package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;

public class OddJobber extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.PERCEPTION;

  public OddJobber(String name) {
    super(name, "Odd Jobber explorer");
  }

  @Override
  public String getProfessionName() {
    return "Odd Jobber";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Street Smart", "Urban survival", TalentCategory.STEALTH_MOBILITY, 3,
            "You get +1 to streetwise rolls"),
        new Talent("Scrounger", "Finding resources", TalentCategory.EQUIPMENT, 3,
            "You can find useful items in unlikely places"),
        new Talent("Quick Thinker", "Fast reactions", TalentCategory.INSIGHT, 3, "You get +1 to snap decisions"),
        new Talent("Lucky", "Fortune favors", TalentCategory.RESILIENCE, 3, "You can re-roll one die per session"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Fixer", "Making connections",
            new Talent("Networker", "Social connections", TalentCategory.SOCIAL, 3, "Bonus to finding contacts")),
        new Specialty("Gambler", "Games of chance",
            new Talent("Card Sharp", "Cheating at games", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to gambling")),
        new Specialty("Mechanic", "Fixing things",
            new Talent("Jury Rigger", "Improvised repairs", TalentCategory.EQUIPMENT, 3, "Bonus to emergency repairs")),
        new Specialty("Peddler", "Trading goods",
            new Talent("Haggler", "Price negotiation", TalentCategory.SOCIAL, 3, "Bonus to trading")),
        new Specialty("Scavenger", "Salvaging materials",
            new Talent("Salvager", "Finding valuables", TalentCategory.EQUIPMENT, 3, "Bonus to scavenging")),
        new Specialty("Street Performer", "Public entertainment", new Talent("Crowd Pleaser", "Attracting attention",
            TalentCategory.SOCIAL, 3, "Bonus to public performance")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Tool Kit", "General purpose tools", EquipmentWeight.REGULAR, 2),
            new Equipment("Lockpick Set", "Security bypass tools", EquipmentWeight.TINY, 2),
            new Equipment("Backpack", "Carry gear", EquipmentWeight.LIGHT)),
        List.of(new Equipment("Fusillard Pistol", "Sidearm", EquipmentWeight.REGULAR, 2),
            new Equipment("Grappling Hook", "Climbing aid", EquipmentWeight.LIGHT, 1),
            new Equipment("Flashlight", "Illumination", EquipmentWeight.TINY, 1)),
        List.of(new Equipment("Communicator", "Long-range comms", EquipmentWeight.TINY, 1),
            new Equipment("Disguise Kit", "Identity tools", EquipmentWeight.LIGHT, 2),
            new Equipment("Rations", "Emergency food", EquipmentWeight.LIGHT)));
  }

  public int getAdaptabilityBonus() {
    return (int) getTalents().stream().map(Talent::getCategory).distinct().count();
  }
}
