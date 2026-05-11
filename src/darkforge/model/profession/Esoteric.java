package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class Esoteric extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.INSIGHT;

  public Esoteric(String name) {
    super(name, "Esoteric explorer");
  }

  @Override
  public String getProfessionName() {
    return "Esoteric";
  }

  @Override
  public Attribute getKeyAttribute() {
    return KEY_ATTRIBUTE;
  }

  @Override
  public List<Talent> getKeyTalents() {
    return List.of(
        new Talent("Intuitive", "Gut feelings", TalentCategory.INSIGHT, 3,
            "You can re-roll one die when using INSIGHT"),
        new Talent("Mystic", "Supernatural awareness", TalentCategory.INSIGHT, 3, "You sense mystical phenomena"),
        new Talent("Seer", "Glimpses of the future", TalentCategory.INSIGHT, 3, "You can make prophetic statements"),
        new Talent("Healer", "Restorative touch", TalentCategory.RECOVERY, 3, "You get +1 to healing rolls"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Diviner", "Reading fate",
            new Talent("Fate Reader", "Fortune telling", TalentCategory.INSIGHT, 3, "Bonus to divination")),
        new Specialty("Dream Walker", "Navigating dreams",
            new Talent("Lucid Dreamer", "Dream control", TalentCategory.INSIGHT, 3, "Bonus in dream encounters")),
        new Specialty("Exorcist", "Banishing entities",
            new Talent("Spirit Ward", "Protection from spirits", TalentCategory.INSIGHT, 3, "Bonus to banishment")),
        new Specialty("Herbalist", "Natural remedies",
            new Talent("Apothecary", "Potion crafting", TalentCategory.RECOVERY, 3, "Bonus to creating remedies")),
        new Specialty("Medium", "Speaking with spirits",
            new Talent("Channeler", "Spirit communication", TalentCategory.INSIGHT, 3, "Bonus to spirit contact")),
        new Specialty("Ritualist", "Performing rites",
            new Talent("Ceremony Master", "Ritual performance", TalentCategory.INSIGHT, 3, "Bonus to ritual rolls")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Ritual Kit", "Mystical components", EquipmentWeight.LIGHT, 2),
            new Equipment("Incense Burner", "Aromatic focus", EquipmentWeight.TINY, 1),
            new Equipment("Robes", "Traditional garments", EquipmentWeight.LIGHT)),
        List.of(new Equipment("Crystal Focus", "Divination aid", EquipmentWeight.TINY, 2),
            new Equipment("Ancient Text", "Mystical knowledge", EquipmentWeight.LIGHT, 1),
            new Equipment("Fusillard Pistol", "Sidearm", EquipmentWeight.REGULAR, 2)),
        List.of(new Equipment("Herb Pouch", "Medicinal herbs", EquipmentWeight.TINY, 1),
            new Equipment("Spirit Lantern", "Spectral light", EquipmentWeight.LIGHT, 1),
            new Equipment("Walking Staff", "Travel aid", EquipmentWeight.REGULAR, 1)));
  }

  public List<String> getMysticalTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.INSIGHT)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }
}
