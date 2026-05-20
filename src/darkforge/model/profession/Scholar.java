package darkforge.model.profession;

import darkforge.data.ProfessionData;
import darkforge.model.*;
import java.util.*;

public class Scholar extends Explorer {

  private static final Attribute KEY_ATTRIBUTE =
          Attribute.LOGIC;

  public Scholar(String name) {
    super(name, "Scholar explorer");
  }

  @Override
  public String getProfessionName() {
    return "Scholar";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    ProfessionData pd = loadProfessionData();
    List<Talent> talents = new ArrayList<>();
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

  public int getResearchBonus() {
    int bonus = 0;
    for (Talent t : getTalents()) {
      if (t.getCategory()
              == TalentCategory.KNOWLEDGE) {
        bonus += t.getCurrentLevel();
      }
    }
    return bonus;
  }
}