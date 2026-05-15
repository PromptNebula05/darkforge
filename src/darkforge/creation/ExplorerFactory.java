package darkforge.creation;

import darkforge.data.GameDataProvider;
import darkforge.exception.*;
import darkforge.model.*;
import darkforge.model.profession.*;
import darkforge.mechanics.AttributeDistributor;
import darkforge.mechanics.D66Table;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Factory for creating fully-assembled Explorer
 * objects through the Coriolis character creation
 * pipeline (Ch. 2). All static game data (Origins,
 * Quirks, Keepsakes, Appearances, Explorer Reasons)
 * is loaded from JSON via GameDataProvider.
 */
public class ExplorerFactory {
  private final Random rng;
  private final GameDataProvider data;

  public ExplorerFactory() {
    this(new Random(),
            GameDataProvider.getInstance());
  }

  public ExplorerFactory(Random rng,
                         GameDataProvider data) {
    this.rng = rng;
    this.data = data;
  }

  /**
   * Creates an Explorer with full validation.
   *
   * @param professionName profession to create
   * @param origin         the Explorer's origin
   * @param specialtyIndex 0-based specialty index
   * @param attributes     attribute distribution
   * @param talentPoints   points per key talent
   * @param quirk          quirk (null to roll)
   * @param keepsake       keepsake (null to roll)
   * @param appearance     appearance (null to roll)
   * @param name           the Explorer's name
   * @throws InvalidProfessionException
   *         if professionName is not recognized
   * @throws InvalidAttributeDistributionException
   *         if attributes violate Coriolis
   *         constraints
   * @throws IncompatibleTalentException
   *         if talent point allocation is invalid
   */
  public Explorer createExplorer(
          String professionName, Origin origin,
          int specialtyIndex,
          EnumMap<Attribute, Integer> attributes,
          int[] talentPoints,
          String quirk, String keepsake,
          String appearance, String name)
          throws InvalidProfessionException,
          InvalidAttributeDistributionException,
          IncompatibleTalentException {

    Explorer explorer =
            createProfession(professionName, name);
    explorer.setOrigin(origin);
    resolveOriginDetails(explorer, origin);

    Specialty specialty = resolveSpecialty(
            explorer, specialtyIndex);
    explorer.setSpecialty(specialty);

    addOriginTalent(explorer, origin);
    addSpecialtyTalent(explorer, specialty);

    AttributeDistributor.validate(
            attributes, explorer.getKeyAttribute());
    explorer.setAttributes(attributes);

    assignKeyTalents(explorer, talentPoints,
            professionName);
    assignPersonalDetails(explorer,
            quirk, keepsake, appearance);
    assignRandomEquipment(explorer);

    return explorer;
  }

  // --- Origin resolution ---

  private void resolveOriginDetails(
          Explorer explorer, Origin origin) {
    if (origin == null) return;
    int contactRoll = rng.nextInt(6) + 1;
    explorer.setResolvedContact(
            origin.getContact(contactRoll));
    if (origin.hasVariableFaction()) {
      int factionRoll = rng.nextInt(6) + 1;
      explorer.setResolvedFaction(
              origin.getFaction(factionRoll));
    } else {
      explorer.setResolvedFaction(
              origin.getAssociatedFaction());
    }
  }

  // --- Specialty resolution ---

  private Specialty resolveSpecialty(
          Explorer explorer, int specialtyIndex) {
    List<Specialty> specialties =
            explorer.getSpecialties();
    if (specialtyIndex < 0
            || specialtyIndex
            >= specialties.size()) {
      specialtyIndex = 0;
    }
    return specialties.get(specialtyIndex);
  }

  // --- Talent helpers ---

  /**
   * Create a level-1 copy of a source talent
   * tagged with the given source label.
   */
  private Talent copyTalentAtLevel1(
          Talent source, String sourceLabel) {
    Talent copy = new Talent(
            source.getName(),
            source.getDescription(),
            source.getCategory(),
            source.getMaxLevel(),
            1,
            source.getEffect());
    copy.setSource(sourceLabel);
    return copy;
  }

  private void addOriginTalent(
          Explorer explorer, Origin origin) {
    if (origin == null
            || origin.getFreeTalent() == null) {
      return;
    }
    explorer.addTalent(copyTalentAtLevel1(
            origin.getFreeTalent(), "Origin"));
  }

  private void addSpecialtyTalent(
          Explorer explorer,
          Specialty specialty) {
    explorer.addTalent(copyTalentAtLevel1(
            specialty.getFreeTalent(),
            "Specialty"));
  }

  private void assignKeyTalents(
          Explorer explorer, int[] talentPoints,
          String professionName)
          throws IncompatibleTalentException {
    List<Talent> keyTalents =
            explorer.getKeyTalents();
    validateTalentPoints(
            talentPoints, keyTalents,
            professionName);

    for (int i = 0;
         i < keyTalents.size(); i++) {
      if (talentPoints[i] > 0) {
        Talent source = keyTalents.get(i);
        Talent chosen = new Talent(
                source.getName(),
                source.getDescription(),
                source.getCategory(),
                source.getMaxLevel(),
                talentPoints[i],
                source.getEffect());
        chosen.setSource("Chosen");
        explorer.addTalent(chosen);
      }
    }
  }

  // --- Personal details & equipment ---

  private void assignPersonalDetails(
          Explorer explorer,
          String quirk, String keepsake,
          String appearance) {
    explorer.setQuirk(
            quirk != null ? quirk
                    : rollOnTable(new D66Table<>(
                    data.getQuirks())));
    explorer.setKeepsake(
            keepsake != null ? keepsake
                    : rollOnTable(new D66Table<>(
                    data.getKeepsakes())));
    explorer.setAppearance(
            appearance != null ? appearance
                    : rollOnTable(new D66Table<>(
                    data.getAppearances())));
    explorer.setExplorerReason(
            rollOnTable(new D66Table<>(
                    data.getExplorerReasons())));
  }

  private void assignRandomEquipment(
          Explorer explorer) {
    List<List<Equipment>> equipSets =
            explorer.getStartingEquipmentSets();
    int equipRoll =
            rng.nextInt(equipSets.size());
    explorer.setEquipment(
            equipSets.get(equipRoll));
  }

  /**
   * Validates talent point distribution against
   * the profession's key talents.
   *
   * @throws IncompatibleTalentException if points
   *         are negative, exceed a talent's max
   *         level, array length mismatches, or
   *         total != 3
   */
  private void validateTalentPoints(
          int[] talentPoints,
          List<Talent> keyTalents,
          String professionName)
          throws IncompatibleTalentException {

    List<String> available =
            keyTalents.stream()
                    .map(Talent::getName)
                    .collect(Collectors.toList());

    if (talentPoints == null
            || talentPoints.length
            != keyTalents.size()) {
      throw new IncompatibleTalentException(
              "(all)", professionName,
              "Talent points array must have "
                      + keyTalents.size()
                      + " entries",
              available);
    }

    int totalPoints = 0;
    for (int i = 0;
         i < talentPoints.length; i++) {
      totalPoints += talentPoints[i];
      if (talentPoints[i] < 0) {
        throw new
                IncompatibleTalentException(
                keyTalents.get(i).getName(),
                professionName,
                "Talent points cannot be "
                        + "negative",
                available);
      }
      if (talentPoints[i]
              > keyTalents.get(i)
              .getMaxLevel()) {
        throw new
                IncompatibleTalentException(
                keyTalents.get(i).getName(),
                professionName,
                String.format(
                        "Allocated %d points "
                                + "but max level is %d",
                        talentPoints[i],
                        keyTalents.get(i)
                                .getMaxLevel()),
                available);
      }
    }

    if (totalPoints != 3) {
      throw new IncompatibleTalentException(
              "(all)", professionName,
              "Total talent points must be 3, "
                      + "got " + totalPoints,
              available);
    }
  }

  /**
   * Create an Explorer subclass by profession
   * name (no-arg constructor). Shared entry point
   * used by ExplorerDeserializer to avoid
   * duplicating the profession→subclass mapping.
   *
   * @throws InvalidProfessionException if the
   *         name is not recognized
   */
  public Explorer createProfessionInstance(
          String professionName)
          throws InvalidProfessionException {
    return createProfession(
            professionName, "Unnamed");
  }

  /**
   * @throws InvalidProfessionException if the
   *         name is not recognized
   */
  private Explorer createProfession(
          String professionName,
          String characterName)
          throws InvalidProfessionException {
    return switch (professionName.toLowerCase()
            .replace(" ", "")) {
      case "scholar" ->
              new Scholar(characterName);
      case "enforcer" ->
              new Enforcer(characterName);
      case "artist" ->
              new Artist(characterName);
      case "esoteric" ->
              new Esoteric(characterName);
      case "oddjobber" ->
              new OddJobber(characterName);
      case "roughneck" ->
              new Roughneck(characterName);
      case "scoundrel" ->
              new Scoundrel(characterName);
      case "traveler" ->
              new Traveler(characterName);
      default -> throw new
              InvalidProfessionException(
              professionName,
              data.getValidProfessionNames());
    };
  }

  /** Roll on a D66 table and return the result. */
  private <T> T rollOnTable(D66Table<T> table) {
    table.roll();
    return table.getResult(
            table.getLastRollValue());
  }
}