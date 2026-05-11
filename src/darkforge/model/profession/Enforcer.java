package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;
import darkforge.mechanics.D6Table;

public class Enforcer extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.AGILITY;
  private final Talent chosenWeaponTalent;

  /**
   * Creates an Enforcer with a chosen weapon talent.
   * The rulebook allows the Enforcer to pick any one weapon talent as a key
   * talent.
   * Use {@link #getAvailableWeaponTalents()} to see valid choices.
   *
   * @param name             the Explorer's name
   * @param weaponTalentName the name of the chosen weapon talent (must match one
   *                         from getAvailableWeaponTalents())
   * @throws IllegalArgumentException if weaponTalentName is not a valid weapon
   *                                  talent
   */
  public Enforcer(String name, String weaponTalentName) {
    super(name, "Enforcer explorer");
    this.chosenWeaponTalent = getAvailableWeaponTalents().stream()
        .filter(t -> t.getName().equalsIgnoreCase(weaponTalentName))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "Invalid weapon talent '" + weaponTalentName + "'. Must be one of: " +
                getAvailableWeaponTalents().stream().map(Talent::getName).toList()));
  }

  /** Convenience constructor defaulting to Sharpshooter as the weapon talent. */
  public Enforcer(String name) {
    this(name, "Sharpshooter");
  }

  @Override
  public String getProfessionName() {
    return "Enforcer";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  /**
   * Returns all weapon talents available for the Enforcer's "Any weapon talent"
   * key talent slot.
   * Per Ch. 3, these are the combat talents tied to specific weapon types.
   */
  public static List<Talent> getAvailableWeaponTalents() {
    return List.of(
        new Talent("Blade Fighter", "Bladed weapon combat", TalentCategory.COMBAT, 3,
            "+1 base die per talent level to close combat rolls when fighting with blade weapons, i.e. swords, knives, axes and spears"),
        new Talent("Bowman", "Bow and crossbow accuracy", TalentCategory.COMBAT, 3,
            "+1 base die per talent level when firing a bow or crossbow"),
        new Talent("Demolitions Expert", "Explosive weapons", TalentCategory.COMBAT, 3,
            "+1 base die per talent level when using explosive weapons, including hand grenades and improvised explosives"),
        new Talent("Heavy Weapons", "Heavy mounted weapons", TalentCategory.COMBAT, 3,
            "+1 base die per talent level when firing heavy weapons such as rocket launchers, flamers, and vehicle mounted weapons"),
        new Talent("Pistoleer", "Pistol accuracy", TalentCategory.COMBAT, 3,
            "+1 base die per talent level when firing a pistol"),
        new Talent("Polearms", "Blunt weapon combat", TalentCategory.COMBAT, 3,
            "+1 base die per talent level to close combat rolls when using blunt weapons"),
        new Talent("Pugilist", "Unarmed combat", TalentCategory.COMBAT, 3,
            "+1 base die per talent level to close combat rolls when fighting unarmed"),
        new Talent("Sharpshooter", "Rifle and carbine accuracy", TalentCategory.COMBAT, 3,
            "+1 base die per talent level when firing a long barreled gun, such as a rifle or a carbine"));
  }

  /** Returns the weapon talent chosen for this Enforcer. */
  public Talent getChosenWeaponTalent() {
    return chosenWeaponTalent;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        chosenWeaponTalent,
        new Talent("Commander", "Helping broken allies", TalentCategory.RECOVERY, 3,
            "+1 base die per talent level when trying to help a person broken by despair"),
        new Talent("Evasive", "Dodging ranged attacks", TalentCategory.COMBAT, 3,
            "+1 base die per talent level to Agility rolls for dodging ranged attacks"),
        new Talent("Medic", "Treating the wounded", TalentCategory.RECOVERY, 3,
            "+1 base die per talent level when trying to help someone broken by damage. Medical equipment give you gear dice."));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Zapti Constable", "Patrolling boroughs and enforcing Guild law",
            new Talent("Interrogator", "Extracting information", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level to rolls for extracting information from someone")),
        new Specialty("Guild Militia", "Protecting, surveilling, and retrieving for the Guilds",
            new Talent("Lookout", "Spotting threats", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level to rolls for spotting approaching threats")),
        new Specialty("Fusillard Protector", "Shielding clients from attacks with fusillard and armor",
            new Talent("Bodyguard", "Diving in to protect others", TalentCategory.COMBAT, 1,
                "If someone within Short range of you is hit by an attack, you can dive in to take the hit. Roll for Agility and if you succeed you take the hit instead. You can push the roll. This doesn't count as an action in combat.")),
        new Specialty("Guild Investigator", "Solving complex crimes for the Guilds and private patrons",
            new Talent("Investigator", "Searching for clues", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level when searching an area for clues")),
        new Specialty("Coriolite Guard", "Guarding Coriolite families with martial skill and presentation",
            new Talent("Polearms", "Blunt weapon combat", TalentCategory.COMBAT, 3,
                "+1 base die per talent level to close combat rolls when using blunt weapons")),
        new Specialty("Bounty Hunter", "Tracking and capturing bounties across Jumuah",
            new Talent("Streetwise", "Urban connections and rumors", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level for acquiring stolen goods, finding a contact, or hearing rumors in Ship City and the colonies")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Fusillard Pistol", "Standard sidearm", EquipmentWeight.REGULAR, 2),
            new Equipment("MediKit (Basic)", "Basic medical supplies", EquipmentWeight.LIGHT, 1),
            new Equipment("Small Shield", "Defensive shield", EquipmentWeight.REGULAR, 1)),
        List.of(new Equipment("Coiler Rifle", "Electromagnetic rifle", EquipmentWeight.REGULAR, 3),
            new Equipment("Vision Scope", "Magnified optic sight", EquipmentWeight.TINY, 1),
            new Equipment("Camouflage Net", "Concealment cover", EquipmentWeight.LIGHT)),
        List.of(new Equipment("Fusillard Carbine", "Short-barreled fusillard", EquipmentWeight.REGULAR, 2),
            new Equipment("Reinforced Exo-Helmet", "Hardened head protection", EquipmentWeight.REGULAR, 1),
            new Equipment("Stun Grenade", "Non-lethal ordinance", EquipmentWeight.TINY)));
  }

  public List<String> getWeaponTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.COMBAT)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }

  /** Sample first names for Enforcer (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Aquilah", 2, "Verlof", 3, "Hazran", 4, "Sighra", 5, "Metilla", 6, "Garida"));
  }

  /** Sample surnames for Enforcer (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(
        Map.of(1, "Andromonos", 2, "Özkal", 3, "Hassmara", 4, "Kourkouti", 5, "Perkantrem", 6, "Goulima"));
  }
}
