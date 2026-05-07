package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;

public class Artist extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.EMPATHY;

  public Artist(String name) {
    super(name, "Artist explorer");
  }

  @Override
  public String getProfessionName() {
    return "Artist";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Charming", "Natural charisma", TalentCategory.SOCIAL, 3,
            "You can re-roll one die in social encounters"),
        new Talent("Performer", "Stage presence", TalentCategory.SOCIAL, 3, "You get +1 to performance rolls"),
        new Talent("Inspiring", "Morale booster", TalentCategory.SOCIAL, 3, "You can restore Hope to allies"),
        new Talent("Empathic", "Reading emotions", TalentCategory.SOCIAL, 3, "You get +1 to reading people"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Composer", "Musical creation",
            new Talent("Musician", "Instrument mastery", TalentCategory.SOCIAL, 3, "Bonus to musical performance")),
        new Specialty("Courtesan", "Social manipulation",
            new Talent("Seducer", "Personal charm", TalentCategory.SOCIAL, 3, "Bonus to persuasion")),
        new Specialty("Dancer", "Physical expression",
            new Talent("Acrobat", "Physical grace", TalentCategory.STEALTH_MOBILITY, 3,
                "Bonus to agility-based performance")),
        new Specialty("Orator", "Public speaking",
            new Talent("Rhetoric", "Persuasive speech", TalentCategory.SOCIAL, 3, "Bonus to public address")),
        new Specialty("Painter", "Visual arts",
            new Talent("Observer", "Keen eye for detail", TalentCategory.INSIGHT, 3, "Bonus to noticing details")),
        new Specialty("Poet", "Written expression",
            new Talent("Wordsmith", "Language artistry", TalentCategory.SOCIAL, 3, "Bonus to written communication")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Musical Instrument", "Fine craftsmanship", EquipmentWeight.REGULAR, 1),
            new Equipment("Fine Clothing", "Impressive attire", EquipmentWeight.LIGHT),
            new Equipment("Recording Device", "Audio capture", EquipmentWeight.TINY, 1)),
        List.of(new Equipment("Paint Set", "Artist supplies", EquipmentWeight.LIGHT, 1),
            new Equipment("Sketchbook", "Drawing journal", EquipmentWeight.TINY, 1),
            new Equipment("Fusillard Pistol", "Sidearm", EquipmentWeight.REGULAR, 2)),
        List.of(new Equipment("Disguise Kit", "Identity tools", EquipmentWeight.LIGHT, 2),
            new Equipment("Communicator", "Long-range comms", EquipmentWeight.TINY, 1),
            new Equipment("Medkit", "Basic medical supplies", EquipmentWeight.LIGHT, 1)));
  }

  public int getPerformanceBonus() {
    int bonus = 0;
    for (Talent t : getTalents()) {
      if (t.getCategory() == TalentCategory.SOCIAL) {
        bonus += t.getCurrentLevel();
      }
    }
    return bonus;
  }
}
