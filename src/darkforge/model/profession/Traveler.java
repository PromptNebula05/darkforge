package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;
import darkforge.mechanics.D6Table;

public class Traveler extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.PERCEPTION;

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
        new Talent("Driver", "Driving ground vehicles", TalentCategory.VEHICLE_EXO, 3,
            "+1 base die per talent level for driving any kind of ground vehicle"),
        new Talent("Mechanic", "Repairing vehicles and devices", TalentCategory.EQUIPMENT, 3,
            "+1 base die per talent level for repairing vehicles and other mechanical devices"),
        new Talent("Exo-Specialist", "Exo suit operation", TalentCategory.VEHICLE_EXO, 3,
            "+1 base die per talent level when handling an exo of some kind"),
        new Talent("Zero-G Training", "Zero gravity operations", TalentCategory.STEALTH_MOBILITY, 1,
            "You suffer no negative effects when operating in zero gravity environments"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Tugship Pilot", "Navigating the miasma and steering ships into dock",
            new Talent("Shuttle Pilot", "Piloting shuttles", TalentCategory.VEHICLE_EXO, 3,
                "+1 base die per talent level to Agility rolls for piloting a shuttle")),
        new Specialty("Hull Warden", "Detecting leaks and faulty piping in the holds of ships",
            new Talent("Investigator", "Searching for clues", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level when searching an area for clues")),
        new Specialty("Guild Surveyor", "Scouring the system for hidden treasures with scanners",
            new Talent("Scan Operator", "Scanning for threats", TalentCategory.EQUIPMENT, 3,
                "+1 base die per talent level to scan an area for its configuration, Blight, or other threats")),
        new Specialty("Kite Handler", "Operating kites, the near-intelligent contraptions of the Machinists",
            new Talent("Kite Operator", "Controlling kites", TalentCategory.VEHICLE_EXO, 3,
                "+1 base die per talent level to rolls when controlling a kite")),
        new Specialty("Lighthouse Keeper", "Servicing radio buoys and keeping the space lanes safe",
            new Talent("Endurance", "Resisting harsh conditions", TalentCategory.RESILIENCE, 3,
                "+1 base die per talent level for resisting vacuum, suffocation, and cold")),
        new Specialty("Asteroid Hauler", "Herding boulders across the vast expanse of space",
            new Talent("Exo-Specialist", "Exo suit operation", TalentCategory.VEHICLE_EXO, 3,
                "+1 base die per talent level when handling an exo of some kind")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Flight Suit", "Standard flight attire", EquipmentWeight.LIGHT),
            new Equipment("Short Story Collection", "Leisure reading material", EquipmentWeight.TINY),
            new Equipment("MediKit", "Medical supplies", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Fine Tools", "Precision tools for delicate work", EquipmentWeight.LIGHT, 1),
            new Equipment("Waking Pills", "Stimulant pills to stay alert", EquipmentWeight.TINY),
            new Equipment("Recon Kite", "Small reconnaissance kite", EquipmentWeight.LIGHT, 2)),
        List.of(new Equipment("Portable Sensor Display", "Handheld scanning display", EquipmentWeight.LIGHT, 2),
            new Equipment("Multi-Wrench", "Adjustable repair wrench", EquipmentWeight.LIGHT, 1),
            new Equipment("Astro-Compass", "Celestial navigation instrument", EquipmentWeight.TINY, 1)));
  }

  public List<String> getSurvivalTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.VEHICLE_EXO)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }

  /** Sample first names for Traveler (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Dacos", 2, "Juvero", 3, "Hamza", 4, "Farzine", 5, "Nilette", 6, "Sahma"));
  }

  /** Sample surnames for Traveler (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(Map.of(1, "Lesmodere", 2, "Quiro", 3, "Rosmahneh", 4, "Ashrum", 5, "Semili", 6, "Nour"));
  }
}
