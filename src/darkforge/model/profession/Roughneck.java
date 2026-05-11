package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import darkforge.mechanics.D6Table;

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
        new Talent("Endurance", "Resisting harsh conditions", TalentCategory.RESILIENCE, 3,
            "+1 base die per talent level for resisting vacuum, suffocation, and cold"),
        new Talent("Force", "Feats of strength", TalentCategory.RESILIENCE, 3,
            "+1 base die per talent level to lift, push, or break something heavy or solid"),
        new Talent("Jury-Rig", "Crafting mechanical machinery", TalentCategory.EQUIPMENT, 3,
            "+1 base die per talent level when rolling to craft or jury-rig mechanical machinery"),
        new Talent("Scan Operator", "Scanning for threats", TalentCategory.EQUIPMENT, 3,
            "+1 base die per talent level to scan an area for its configuration, Blight, or other threats"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Hull Guard", "Keeping watch on ship hulls, alert to turbulence and radiation",
            new Talent("Lookout", "Spotting threats", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level to rolls for spotting approaching threats")),
        new Specialty("Wreck Diver", "Spotting entry points and securing treasures from old wrecks",
            new Talent("Exo-Specialist", "Exo suit operation", TalentCategory.VEHICLE_EXO, 3,
                "+1 base die per talent level when handling an exo of some kind")),
        new Specialty("Vacuum Welder", "Freelance welding in dangerous zero-gravity environments",
            new Talent("Zero-G Training", "Zero gravity operations", TalentCategory.STEALTH_MOBILITY, 1,
                "You suffer no negative effects when operating in zero gravity environments")),
        new Specialty("Deep Miner", "Digging, burrowing, and blasting in deep asteroid mines",
            new Talent("Miner", "Drilling and digging", TalentCategory.EQUIPMENT, 3,
                "+1 base die per talent level to drill, dig, or secure an underground cave or tunnel")),
        new Specialty("Crane Rat", "Climbing, jumping, and securing cargo in harbors and the Bay",
            new Talent("Acrobat", "Jumping, climbing, and running", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level to jumping, climbing, and running")),
        new Specialty("Machine Tender", "Disassembling and reassembling machines, circuits, and gizmos",
            new Talent("Mechanic", "Repairing vehicles and devices", TalentCategory.EQUIPMENT, 3,
                "+1 base die per talent level for repairing vehicles and other mechanical devices")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Exo Suit", "Powered exoskeleton suit", EquipmentWeight.HEAVY, 2),
            new Equipment("Welding Torch", "High-temperature cutting and welding tool", EquipmentWeight.REGULAR, 1),
            new Equipment("Fine Tools", "Precision tools for delicate work", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Slim Suit", "Lightweight vacuum suit", EquipmentWeight.LIGHT, 1),
            new Equipment("Adhesive Gloves", "Gloves that grip any surface", EquipmentWeight.TINY, 1),
            new Equipment("Vacuum Sealer", "Emergency hull patch tool", EquipmentWeight.LIGHT, 1)),
        List.of(new Equipment("Heavy Tools", "Industrial-grade tools", EquipmentWeight.REGULAR, 2),
            new Equipment("Pickaxe", "Mining and breaking tool", EquipmentWeight.REGULAR, 1),
            new Equipment("Breach Charge", "Explosive charge for breaching walls", EquipmentWeight.LIGHT)));
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

  /** Sample first names for Roughneck (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Jurji", 2, "Amman", 3, "Geov", 4, "Gorgija", 5, "Asmirad", 6, "Fassour"));
  }

  /** Sample surnames for Roughneck (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(Map.of(1, "Mammahr", 2, "Ahsouli", 3, "Christofor", 4, "Mell", 5, "Iortis", 6, "Belkovoro"));
  }
}
