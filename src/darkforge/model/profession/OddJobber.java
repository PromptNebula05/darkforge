package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import darkforge.mechanics.D6Table;

public class OddJobber extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.EMPATHY;

  public OddJobber(String name) {
    super(name, "Odd Jobber explorer");
  }

  @Override
  public String getProfessionName() {
    return "Odd Jobber";
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
        new Talent("Charmer", "Making NPCs like you", TalentCategory.SOCIAL, 3,
            "+1 base die per talent level when rolling for Empathy to make an NPC like you"),
        new Talent("Cultural Savant", "Understanding customs", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level to understand customs and cultural habits in the Lost Horizon"),
        new Talent("Streetwise", "Urban connections and rumors", TalentCategory.STEALTH_MOBILITY, 3,
            "+1 base die per talent level for acquiring stolen goods, finding a contact, or hearing rumors in Ship City and the colonies"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Guild Clerk", "Sorting mail, taking notes, and handling tasks for a Guild master",
            new Talent("Librarian", "Using libraries and info cubes", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level when using a library or info cubes to find information")),
        new Specialty("Stair Peddler", "Selling goods on the steep stairs of the Chasm",
            new Talent("Mentalist", "Reading people", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level to Empathy rolls for reading a subject and understanding if they are lying or hiding something")),
        new Specialty("Ice Trader", "Trading ice bonds to Guilds and merchants",
            new Talent("Barter", "Trading and buying", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level when rolling for Empathy to trade or buy something")),
        new Specialty("Alley Cook", "Making stews and grilling skewers in a small street stall",
            new Talent("Cook", "Cooking with a field kitchen", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level when cooking using a field kitchen")),
        new Specialty("Coriolite Servant", "Serving a Coriolite noble and learning the ways of the Old Horizon",
            new Talent("Cultural Savant", "Understanding customs", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level to understand customs and cultural habits in the Lost Horizon")),
        new Specialty("Artifact Dealer", "Buying and selling artifacts from across the Charted Sphere",
            new Talent("Artifact Specialist", "Comprehending artifacts", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level to comprehend artifacts")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Fancy Clothing", "Impressive attire", EquipmentWeight.LIGHT),
            new Equipment("Waking Pills", "Stimulant pills to stay alert", EquipmentWeight.TINY),
            new Equipment("Bottle of Shroom Brandy", "Fermented mushroom spirits", EquipmentWeight.TINY)),
        List.of(new Equipment("Cooking Utensils", "Pots, pans, and tools for cooking", EquipmentWeight.REGULAR, 1),
            new Equipment("Orchid Dust (5 doses)", "Psychoactive substance from the Blight", EquipmentWeight.TINY),
            new Equipment("Large Backpack", "Oversized carry gear", EquipmentWeight.LIGHT)),
        List.of(new Equipment("Accounting Ledger", "Financial record book", EquipmentWeight.TINY),
            new Equipment("Flight Suit", "Standard flight attire", EquipmentWeight.LIGHT),
            new Equipment("MediKit (Basic)", "Basic medical supplies", EquipmentWeight.LIGHT, 1)));
  }

  public int getAdaptabilityBonus() {
    return (int) getTalents().stream().map(Talent::getCategory).distinct().count();
  }

  /** Sample first names for Odd Jobber (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Chandra", 2, "Dharr", 3, "Margou", 4, "Steem", 5, "Vasil", 6, "Xemene"));
  }

  /** Sample surnames for Odd Jobber (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(Map.of(1, "Nemkassur", 2, "Kavour", 3, "N'rama", 4, "Soltev", 5, "Koulidis", 6, "Zhou"));
  }
}
