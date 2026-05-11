package darkforge.creation;

import darkforge.model.*;
import darkforge.model.profession.*;
import darkforge.mechanics.AttributeDistributor;
import java.util.*;

public class ExplorerFactory {

  private static final List<Origin> DEFAULT_ORIGINS = List.of(
      new Origin("Among the Hulks and Wrecks of Hull Town",
          new Talent("Exo-Specialist", "Exo suit operation", TalentCategory.VEHICLE_EXO, 3, "Bonus to exo suit rolls"),
          "Navigators Guild", "Algebraist Anapur", 11, 12),
      new Origin("In the Shadow of the Monolith",
          new Talent("Tough", "Physical resilience", TalentCategory.RESILIENCE, 3, "You can withstand more punishment"),
          "The Church of the Awakening", "Preacher Dara", 13, 14),
      new Origin("On a Nomad Ship Between the Stars",
          new Talent("Wanderer", "Travel experience", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to navigation"),
          "Free Traders", "Captain Mirrem", 15, 16),
      new Origin("In the Markets of Coriolis Station",
          new Talent("Street Smart", "Urban survival", TalentCategory.STEALTH_MOBILITY, 3, "Bonus to streetwise rolls"),
          "The Syndicate", "Merchant Farouz", 21, 22),
      new Origin("At the Academy of Ahlam",
          new Talent("Smart", "Quick thinking", TalentCategory.KNOWLEDGE, 3,
              "You can re-roll one die when using LOGIC"),
          "The Academy", "Professor Hadiya", 23, 24),
      new Origin("Among the Ice Miners of Kua",
          new Talent("Hardy", "Physical toughness", TalentCategory.RESILIENCE, 3,
              "You can ignore one point of damage per session"),
          "Ice Miners Union", "Foreman Rashid", 25, 26));

  /**
   * Creates an Explorer with a default weapon talent (Sharpshooter) for
   * Enforcers.
   * For Enforcers who want a different weapon talent, use the overload that
   * accepts weaponTalentName.
   */
  public Explorer createExplorer(
      String professionName, String characterName,
      int originIndex, int specialtyIndex,
      EnumMap<Attribute, Integer> attributes, int[] talentPoints,
      String quirk, String keepsake, String appearance) {
    return createExplorer(professionName, characterName, originIndex, specialtyIndex,
        attributes, talentPoints, quirk, keepsake, appearance, null);
  }

  /**
   * Creates an Explorer.
   *
   * @param weaponTalentName for Enforcers only: the name of the chosen weapon
   *                         talent
   *                         (e.g. "Sharpshooter", "Pistoleer"). Pass null for
   *                         non-Enforcers
   *                         or to use the default (Sharpshooter).
   *                         See {@link Enforcer#getAvailableWeaponTalents()} for
   *                         valid names.
   */
  public Explorer createExplorer(
      String professionName, String characterName,
      int originIndex, int specialtyIndex,
      EnumMap<Attribute, Integer> attributes, int[] talentPoints,
      String quirk, String keepsake, String appearance,
      String weaponTalentName) {

    if (originIndex < 1 || originIndex > DEFAULT_ORIGINS.size())
      throw new IllegalArgumentException("Origin index must be 1-" + DEFAULT_ORIGINS.size() + ", got " + originIndex);
    Origin origin = DEFAULT_ORIGINS.get(originIndex - 1);

    Explorer explorer = createProfession(professionName, characterName, weaponTalentName);
    explorer.setOrigin(origin);

    List<Specialty> specialties = explorer.getSpecialties();
    if (specialtyIndex < 1 || specialtyIndex > specialties.size())
      throw new IllegalArgumentException("Specialty index must be 1-" + specialties.size() + ", got " + specialtyIndex);
    Specialty specialty = specialties.get(specialtyIndex - 1);
    explorer.setSpecialty(specialty);

    Talent originTalent = new Talent(origin.getFreeTalent().getName(), origin.getFreeTalent().getDescription(),
        origin.getFreeTalent().getCategory(), origin.getFreeTalent().getMaxLevel(), 1,
        origin.getFreeTalent().getEffect());
    explorer.addTalent(originTalent);

    Talent specTalent = new Talent(specialty.getFreeTalent().getName(), specialty.getFreeTalent().getDescription(),
        specialty.getFreeTalent().getCategory(), specialty.getFreeTalent().getMaxLevel(), 1,
        specialty.getFreeTalent().getEffect());
    explorer.addTalent(specTalent);

    AttributeDistributor.validate(attributes, explorer.getKeyAttribute());
    explorer.setAttributes(attributes);

    List<Talent> keyTalents = explorer.getKeyTalents();
    if (talentPoints == null || talentPoints.length != keyTalents.size())
      throw new IllegalArgumentException("Talent points array must have " + keyTalents.size() + " entries");
    int totalPoints = 0;
    for (int points : talentPoints)
      totalPoints += points;
    if (totalPoints != 3)
      throw new IllegalArgumentException("Total talent points must be 3, got " + totalPoints);

    for (int i = 0; i < keyTalents.size(); i++) {
      if (talentPoints[i] > 0) {
        Talent keyTalent = new Talent(keyTalents.get(i).getName(), keyTalents.get(i).getDescription(),
            keyTalents.get(i).getCategory(), keyTalents.get(i).getMaxLevel(), talentPoints[i],
            keyTalents.get(i).getEffect());
        explorer.addTalent(keyTalent);
      }
    }

    explorer.setQuirk(quirk);
    explorer.setKeepsake(keepsake);
    explorer.setAppearance(appearance);
    return explorer;
  }

  private Explorer createProfession(String professionName, String characterName, String weaponTalentName) {
    return switch (professionName.toLowerCase().replace(" ", "")) {
      case "scholar" -> new Scholar(characterName);
      case "enforcer" -> (weaponTalentName != null)
          ? new Enforcer(characterName, weaponTalentName)
          : new Enforcer(characterName);
      case "artist" -> new Artist(characterName);
      case "esoteric" -> new Esoteric(characterName);
      case "oddjobber" -> new OddJobber(characterName);
      case "roughneck" -> new Roughneck(characterName);
      case "scoundrel" -> new Scoundrel(characterName);
      case "traveler" -> new Traveler(characterName);
      default -> throw new IllegalArgumentException("Unknown profession: " + professionName);
    };
  }
}
