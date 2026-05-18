package darkforge.model.profession;

import darkforge.data.ProfessionData;
import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class Enforcer extends Explorer {

  private static final Attribute KEY_ATTRIBUTE =
          Attribute.AGILITY;
  private final Talent chosenWeaponTalent;

  public Enforcer(String name,
                  String weaponTalentName) {
    super(name, "Enforcer explorer");
    this.chosenWeaponTalent =
            getAvailableWeaponTalents().stream()
                    .filter(t -> t.getName()
                            .equalsIgnoreCase(
                                    weaponTalentName))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Invalid weapon talent '"
                                            + weaponTalentName
                                            + "'. Must be one of: "
                                            + getAvailableWeaponTalents()
                                            .stream()
                                            .map(Talent::getName)
                                            .toList()));
  }

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

  public static List<Talent>
  getAvailableWeaponTalents() {
    return List.of(
            new Talent("Blade Fighter",
                    "Bladed weapon combat",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "to close combat rolls when "
                            + "fighting with blade weapons"),
            new Talent("Bowman",
                    "Bow and crossbow accuracy",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "when firing a bow or crossbow"),
            new Talent("Demolitions Expert",
                    "Explosive weapons",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "when using explosive weapons"),
            new Talent("Heavy Weapons",
                    "Heavy mounted weapons",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "when firing heavy weapons"),
            new Talent("Pistoleer",
                    "Pistol accuracy",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "when firing a pistol"),
            new Talent("Polearms",
                    "Blunt weapon combat",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "to close combat rolls when "
                            + "using blunt weapons"),
            new Talent("Pugilist",
                    "Unarmed combat",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "to close combat rolls when "
                            + "fighting unarmed"),
            new Talent("Sharpshooter",
                    "Rifle and carbine accuracy",
                    TalentCategory.COMBAT, 3,
                    "+1 base die per talent level "
                            + "when firing a long barreled "
                            + "gun"));
  }

  public Talent getChosenWeaponTalent() {
    return chosenWeaponTalent;
  }

  @Override
  public List<Talent> getKeyTalents() {
    ProfessionData pd = loadProfessionData();
    List<Talent> talents = new ArrayList<>();
    talents.add(chosenWeaponTalent);
    for (ProfessionData.TalentData td :
            pd.getTalents()) {
      talents.add(new Talent(
              td.name(), td.description(),
              TalentCategory.valueOf(
                      td.category()),
              td.maxLevel(), td.effect()));
    }
    return talents;
  }

  @Override
  public List<Specialty> getSpecialties() {
    ProfessionData pd = loadProfessionData();
    List<Specialty> specs = new ArrayList<>();
    for (ProfessionData.SpecialtyData sd :
            pd.getSpecialties()) {
      Talent freeTalent =
              resolveSpecialtyTalent(
                      sd.freeTalentName());
      specs.add(new Specialty(
              sd.name(), sd.description(),
              freeTalent));
    }
    return specs;
  }

  private Talent resolveSpecialtyTalent(
          String talentName) {
    for (Talent t : getKeyTalents()) {
      if (t.getName().equalsIgnoreCase(
              talentName)) {
        return t;
      }
    }
    return new Talent(
            talentName, "",
            TalentCategory.GENERAL, 3,
            "Specialty talent");
  }

  @Override
  public List<List<Equipment>>
  getStartingEquipmentSets() {
    ProfessionData pd = loadProfessionData();
    List<List<Equipment>> sets =
            new ArrayList<>();
    for (List<ProfessionData.EquipmentData>
            setData :
            pd.getStartingEquipmentSets()) {
      List<Equipment> items =
              new ArrayList<>();
      for (ProfessionData.EquipmentData ed :
              setData) {
        items.add(new Equipment(
                ed.name(), ed.description(),
                EquipmentWeight.valueOf(
                        ed.weight()),
                ed.gearBonus()));
      }
      sets.add(items);
    }
    return sets;
  }

  public List<String> getWeaponTalents() {
    return getTalents().stream()
            .filter(t -> t.getCategory()
                    == TalentCategory.COMBAT)
            .map(Talent::getName)
            .collect(Collectors.toList());
  }
}