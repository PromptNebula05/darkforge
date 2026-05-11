package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class Traveler extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.EMPATHY;

  public Traveler(String name) {
    super(name, "Traveler explorer");
  }

  @Override
  public String getProfessionName() {
    return "Traveler";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Wanderer", "Travel experience", TalentCategory.STEALTH_MOBILITY, 3,
            "You can re-roll one die when navigating"),
        new Talent("Survivor", "Staying alive", TalentCategory.RECOVERY, 3, "You get +1 to survival rolls"),
        new Talent("Compassionate", "Caring nature", TalentCategory.SOCIAL, 3,
            "You get +1 to comforting or aiding others"),
        new Talent("Resourceful", "Making do", TalentCategory.RESILIENCE, 3,
            "You can improvise solutions with limited materials"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Cartographer", "Map-making",
            new Talent("Map Reader", "Terrain navigation", TalentCategory.KNOWLEDGE, 3, "Bonus to navigation")),
        new Specialty("Diplomat", "Cultural bridge",
            new Talent("Cultural Awareness", "Understanding customs", TalentCategory.SOCIAL, 3,
                "Bonus to cross-cultural interaction")),
        new Specialty("Guide", "Pathfinding",
            new Talent("Pathfinder", "Route finding", TalentCategory.STEALTH_MOBILITY, 3,
                "Bonus to finding safe routes")),
        new Specialty("Healer", "Medical care",
            new Talent("Field Medic", "Emergency medicine", TalentCategory.RECOVERY, 3, "Bonus to field treatment")),
        new Specialty("Nomad", "Constant movement",
            new Talent("Endurance", "Long-distance stamina", TalentCategory.RESILIENCE, 3,
                "Bonus to endurance checks")),
        new Specialty("Scout", "Reconnaissance",
            new Talent("Recon Expert", "Advance scouting", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to scouting")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Survival Kit", "Wilderness essentials", EquipmentWeight.REGULAR, 2),
            new Equipment("Compass", "Directional tool", EquipmentWeight.TINY, 1),
            new Equipment("Bedroll", "Sleeping gear", EquipmentWeight.LIGHT)),
        List.of(new Equipment("Medkit", "Medical supplies", EquipmentWeight.LIGHT, 2),
            new Equipment("Fusillard Pistol", "Sidearm", EquipmentWeight.REGULAR, 2),
            new Equipment("Water Purifier", "Clean water source", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Binoculars", "Long-range vision", EquipmentWeight.LIGHT, 1),
            new Equipment("Climbing Gear", "Ascent tools", EquipmentWeight.REGULAR, 1),
            new Equipment("Signal Flares", "Emergency signaling", EquipmentWeight.TINY)));
  }

  public List<String> getSurvivalTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.RECOVERY)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }
}
