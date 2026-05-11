package darkforge.model.profession;

import darkforge.model.*;
import java.util.*;
import darkforge.mechanics.D6Table;

public class Artist extends Explorer {

  private static final Attribute KEY_ATTRIBUTE = Attribute.EMPATHY;

  public Artist(String name) {
    super(name, "Artist explorer");
  }

  @Override
  public String getProfessionName() {
    return "Artist";
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
        new Talent("Cultural Savant", "Understanding customs", TalentCategory.KNOWLEDGE, 3,
            "+1 base die per talent level to understand customs and cultural habits in the Lost Horizon"),
        new Talent("Renowned", "Pushing Empathy rolls", TalentCategory.SOCIAL, 1,
            "You can push any roll based on Empathy twice, not just once like other characters"));
  }

  @Override
  public List<Specialty> getSpecialties() {
    return List.of(
        new Specialty("Hull Painter", "Bringing color to the giant husks of old spacefarers",
            new Talent("Zero-G Training", "Zero gravity operations", TalentCategory.STEALTH_MOBILITY, 1,
                "You suffer no negative effects when operating in zero gravity environments")),
        new Specialty("Staircase Poet", "Reciting poetry on the steep stairs of the Chasm",
            new Talent("Charmer", "Making NPCs like you", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level when rolling for Empathy to make an NPC like you")),
        new Specialty("Maidy Row Balladeer", "Singing ballads and anthems for the people of Ship City",
            new Talent("Musician", "Playing musical instruments", TalentCategory.SOCIAL, 3,
                "+1 base die per talent level to Empathy when using a musical instrument")),
        new Specialty("Alley Theater Actor", "Performing lead roles in small alley theaters",
            new Talent("Disguise", "Avoiding recognition", TalentCategory.STEALTH_MOBILITY, 3,
                "+1 base die per talent level to avoid being recognized, and to spot a disguise")),
        new Specialty("Occasional Publisher", "Writing and publishing gossip pamphlets",
            new Talent("Librarian", "Using libraries and info cubes", TalentCategory.KNOWLEDGE, 3,
                "+1 base die per talent level when using a library or info cubes to find information")),
        new Specialty("Machine Artisan", "Crafting automata, lighting contraptions, and mechanical devices",
            new Talent("Jury-Rig", "Crafting mechanical machinery", TalentCategory.EQUIPMENT, 3,
                "+1 base die per talent level when rolling to craft or jury-rig mechanical machinery")));
  }

  @Override
  public List<List<Equipment>> getStartingEquipmentSets() {
    return List.of(
        List.of(new Equipment("Music Instrument", "Fine craftsmanship instrument", EquipmentWeight.REGULAR, 1),
            new Equipment("Notebook and Pen", "Writing supplies", EquipmentWeight.TINY),
            new Equipment("Bottle of Grass Wine", "A bottle of local wine", EquipmentWeight.TINY)),
        List.of(new Equipment("Exquisite Clothing", "Impressive and ornate attire", EquipmentWeight.LIGHT),
            new Equipment("Rare Collection of Poems", "Valued literary collection", EquipmentWeight.TINY),
            new Equipment("Make-Up", "Cosmetics and face paints", EquipmentWeight.TINY)),
        List.of(new Equipment("Fine Tools", "Precision crafting tools", EquipmentWeight.LIGHT, 1),
            new Equipment("Magnifying Glass", "Examination lens", EquipmentWeight.TINY, 1),
            new Equipment("Set of Paints", "Assorted colors for painting", EquipmentWeight.TINY)));
  }

  public int getPerformanceBonus() {
    int bonus = 0;
    for (Talent t : getTalents()) {
      if (t.getCategory() == TalentCategory.SOCIAL) {
        bonus += t.getCurrentLevel();
      }
    }
    return bonus;
  }

  /** Sample first names for Artist (Ch. 2, D6 table). */
  public static D6Table<String> getSampleFirstNames() {
    return new D6Table<>(Map.of(1, "Iosop", 2, "Picar", 3, "Alvereto", 4, "Charita", 5, "Yisma", 6, "Demetria"));
  }

  /** Sample surnames for Artist (Ch. 2, D6 table). */
  public static D6Table<String> getSampleSurnames() {
    return new D6Table<>(Map.of(1, "Berin", 2, "Harkoum", 3, "Misiopio", 4, "Dargar", 5, "Coumar", 6, "Kalventer"));
  }
}
