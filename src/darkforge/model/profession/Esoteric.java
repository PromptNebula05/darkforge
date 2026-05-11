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
        new Talent("Actor", "Bluffing and lying", TalentCategory.SOCIAL, 3,
            "+1 base die per talent level when rolling to bluff or tell a lie to an NPC"),
        new Talent("Botanist", "Cultivating and understanding flora", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level for cultivating plants or fungi as well as understanding flora and ivy"),
        new Talent("Cultural Savant", "Understanding customs", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level to understand customs and cultural habits in the Lost Horizon"),
        new Talent("Librarian", "Using libraries and info cubes", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level when using a library or info cubes to find information"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Spice Engineer", "Experimenting with orchid dust and ingredients in a makeshift laboratory",
            new Talent("Laboratorist", "Concoctions and chemical reactions", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level for making concoctions and understanding chemical reactions")),
        new Specialty("Bird Warden", "Caring for Birds, ornithiums, and other critters",
            new Talent("Bird Handler", "Handling a Bird", TalentCategory.INSIGHT, 3,
                "+1 base die per talent level for handling a Bird")),
        new Specialty("Coriolite Seer", "Reading fate with tea leaves, old bones, and dice",
            new Talent("Intuition", "Vague guidance from the GM", TalentCategory.INSIGHT, 1,
                "Once per session, you may ask the GM to give you vague but useful guidance")),
        new Specialty("Revolutionary Prophet", "Using fiery rhetoric to lead the people toward liberation",
            new Talent("Agitator", "Swaying groups of NPCs", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level when rolling for Empathy to speak to a group of NPCs to sway them")),
        new Specialty("Toad Dreamer", "Chasing epiphanies and truth through orchid dust dreams",
            new Talent("Hardened", "Resisting Blight", TalentCategory.RESILIENCE, 3,
                "Roll base dice equal to the talent level when suffering Blight; for each six rolled, ignore one point of Blight")),
        new Specialty("Rim Zealot", "Preaching expansion of the colonies beyond Jumuah",
            new Talent("Mentalist", "Reading people", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level to Empathy rolls for reading a subject and understanding if they are lying or hiding something")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(
            new Equipment("Smelling Spice (5 doses)", "Aromatic spice with mystical properties", EquipmentWeight.TINY),
            new Equipment("Pipe", "Smoking pipe", EquipmentWeight.TINY),
            new Equipment("Antidote", "Counteragent for poisons", EquipmentWeight.TINY)),
        List.of(new Equipment("Bird Candy", "Treats for attracting and calming Birds", EquipmentWeight.TINY),
            new Equipment("Star Chart", "Map of celestial bodies", EquipmentWeight.TINY, 1),
            new Equipment("Deck of Cards", "Divination or gaming cards", EquipmentWeight.TINY)),
        List.of(new Equipment("Revolutionary Pamphlets", "Fiery propaganda writings", EquipmentWeight.TINY),
            new Equipment("Fusillard Cricket", "Small concealable fusillard", EquipmentWeight.LIGHT, 1),
            new Equipment("Flask with Makh Spirits", "Strong liquor from the Turbine Halls", EquipmentWeight.TINY)));
  }

  public List<String> getMysticalTalents() {
    return getTalents().stream()
        .filter(t -> t.getCategory() == TalentCategory.INSIGHT)
        .map(Talent::getName)
        .collect(Collectors.toList());
  }
}
