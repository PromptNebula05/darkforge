package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;
import darkforge.mechanics.D6Table;

public class Scoundrel extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.PERCEPTION;

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
        new Talent("Acrobat", "Jumping, climbing, and running", TalentCategory.STEALTH_MOBILITY, 3,
            "+1 base die per talent level to jumping, climbing, and running"),
        new Talent("Charmer", "Making NPCs like you", TalentCategory.SOCIAL, 3,
            "+1 base die per talent level when rolling for Empathy to make an NPC like you"),
        new Talent("Lookout", "Spotting threats", TalentCategory.STEALTH_MOBILITY, 3,
            "+1 base die per talent level to rolls for spotting approaching threats"),
        new Talent("Stealthy", "Staying hidden", TalentCategory.STEALTH_MOBILITY, 3,
            "+1 base die per talent level for staying hidden"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Dust Runner", "Running orchid dust for the Toads all over the Rock",
            new Talent("Streetwise", "Urban connections and rumors", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level for acquiring stolen goods, finding a contact, or hearing rumors in Ship City and the colonies")),
        new Specialty("Tech Smuggler", "Smuggling shards and Old Horizon tech past Guild quarantine",
            new Talent("Electro-Specialist", "Electronic devices", TalentCategory.EQUIPMENT, 3,
                "+1 base die per talent level for manipulating and repairing electronic devices, including computers")),
        new Specialty("Guild Spy", "Gathering intelligence and working in the shadows for a Guild",
            new Talent("Stealthy", "Staying hidden", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level for staying hidden")),
        new Specialty("Alley Thug", "Odd jobs as bouncer, enforcer, and debt collector",
            new Talent("Thug", "Threatening with Strength", TalentCategory.SOCIAL, 3,
                "You can roll for Strength instead of Empathy when threatening someone, and you get +1 base die per talent level")),
        new Specialty("Hull Cutter", "Stripping gear from vessels to sell on the black market",
            new Talent("Mechanic", "Repairing vehicles and devices", TalentCategory.EQUIPMENT, 3,
                "+1 base die per talent level for repairing vehicles and other mechanical devices")),
        new Specialty("Con Artist", "Fooling the gullible with a range of characters and performances",
            new Talent("Actor", "Bluffing and lying", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level when rolling to bluff or tell a lie to an NPC")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Fusillard Cricket", "Small concealable fusillard", EquipmentWeight.LIGHT, 1),
            new Equipment("Fake Guild ID", "Forged identification documents", EquipmentWeight.TINY),
            new Equipment("Proximity Sweeper", "Detects nearby signals and devices", EquipmentWeight.TINY, 1)),
        List.of(new Equipment("Heavy Tools", "Industrial-grade tools", EquipmentWeight.REGULAR, 2),
            new Equipment("Lockpicks", "Lock-picking tools", EquipmentWeight.TINY, 1),
            new Equipment("Flamer", "Incendiary weapon", EquipmentWeight.REGULAR, 2)),
        List.of(new Equipment("Chemical Kit", "Chemistry supplies and reagents", EquipmentWeight.LIGHT, 1),
            new Equipment("Orchid Dust (5 doses)", "Psychoactive substance from the Blight", EquipmentWeight.TINY),
            new Equipment("Breathing Mask", "Filtration mask for hazardous environments", EquipmentWeight.TINY)));
  }

  public List<String> getDeceptionTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.STEALTH_MOBILITY)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }

  /** Sample first names for Scoundrel (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Aruma", 2, "Etila", 3, "Jevgi", 4, "Kef", 5, "Salamana", 6, "Wallih"));
  }

  /** Sample surnames for Scoundrel (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(Map.of(1, "mir-Caph", 2, "Xem", 3, "Ramastan", 4, "Koud", 5, "Zerzim", 6, "din-Tallah"));
  }
}
