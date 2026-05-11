package darkforge.creation;

import darkforge.model.*;
import darkforge.model.profession.*;
import darkforge.mechanics.AttributeDistributor;
import darkforge.mechanics.D6Table;
import darkforge.mechanics.D66Table;
import java.util.*;

public class ExplorerFactory {

  private final Random rng;

  public ExplorerFactory() {
    this(new Random());
  }

  public ExplorerFactory(Random rng) {
    this.rng = rng;
  }

  /**
   * Helper to create a contacts map from three D6-paired contact names (1-2, 3-4,
   * 5-6).
   */
  private static Map<Integer, String> contacts(String c12, String c34, String c56) {
    return Map.of(1, c12, 2, c12, 3, c34, 4, c34, 5, c56, 6, c56);
  }

  /**
   * Variable faction D6 table shared by Hull Town, Aluminum Bay, and The Dome
   * origins.
   */
  private static D6Table<String> variableFactionTable() {
    return new D6Table<>(Map.of(
        1, "Machinists", 2, "Gardeners", 3, "Navigators",
        4, "Navigators", 5, "The Black Toad", 6, "Coriolites"));
  }

  // ── D66 Quirks Table (Ch. 2) ────────────────────────────────────────────
  private static final D66Table<String> QUIRKS_TABLE = new D66Table<>(Map.ofEntries(
      Map.entry(11, "Always fidgets with something"), Map.entry(12, "Always fidgets with something"),
      Map.entry(13, "Triple checks everything"), Map.entry(14, "Triple checks everything"),
      Map.entry(15, "Always perfumed"), Map.entry(16, "Always perfumed"),
      Map.entry(21, "Thrill seeker"), Map.entry(22, "Thrill seeker"),
      Map.entry(23, "Overly polite"), Map.entry(24, "Overly polite"),
      Map.entry(25, "Very superstitious"), Map.entry(26, "Very superstitious"),
      Map.entry(31, "Wears makeup"), Map.entry(32, "Wears makeup"),
      Map.entry(33, "Always eating"), Map.entry(34, "Always eating"),
      Map.entry(35, "Very sleepy"), Map.entry(36, "Very sleepy"),
      Map.entry(41, "Easily distracted"), Map.entry(42, "Easily distracted"),
      Map.entry(43, "Constantly reading"), Map.entry(44, "Constantly reading"),
      Map.entry(45, "Says \"hmm\" a lot"), Map.entry(46, "Says \"hmm\" a lot"),
      Map.entry(51, "Smokes too much"), Map.entry(52, "Smokes too much"),
      Map.entry(53, "Sniffs cirra-cirra to calm nerves"), Map.entry(54, "Sniffs cirra-cirra to calm nerves"),
      Map.entry(55, "Easily amazed"), Map.entry(56, "Easily amazed"),
      Map.entry(61, "Developed sense of fashion"), Map.entry(62, "Developed sense of fashion"),
      Map.entry(63, "Eye-catching tattoo"), Map.entry(64, "Eye-catching tattoo"),
      Map.entry(65, "Fond of jewelry"), Map.entry(66, "Fond of jewelry")));

  /** Returns the D66 Quirks table for random or lookup-based quirk selection. */
  public static D66Table<String> getQuirksTable() {
    return QUIRKS_TABLE;
  }

  /** Looks up a quirk by D66 value. */
  public static String getQuirkByD66(int d66Value) {
    return QUIRKS_TABLE.getResult(d66Value);
  }

  // ── D66 Reason to Become an Explorer Table (Ch. 2) ─────────────────────────
  private static final D66Table<String> EXPLORER_REASON_TABLE = new D66Table<>(Map.ofEntries(
      Map.entry(11, "It was your childhood dream."), Map.entry(12, "It was your childhood dream."),
      Map.entry(13, "You want to find out what's out there."), Map.entry(14, "You want to find out what's out there."),
      Map.entry(15, "You read all of the zinti novels. Several times."),
      Map.entry(16, "You read all of the zinti novels. Several times."),
      Map.entry(21, "You had a thirst for knowledge."), Map.entry(22, "You had a thirst for knowledge."),
      Map.entry(23, "A close friend talked you into it."), Map.entry(24, "A close friend talked you into it."),
      Map.entry(25, "You met a very persuasive recruiter down in the Bay."),
      Map.entry(26, "You met a very persuasive recruiter down in the Bay."),
      Map.entry(31, "You were always restless."), Map.entry(32, "You were always restless."),
      Map.entry(33, "You needed the money."), Map.entry(34, "You needed the money."),
      Map.entry(35, "You wanted to escape the drudgery of everyday life."),
      Map.entry(36, "You wanted to escape the drudgery of everyday life."),
      Map.entry(41, "You promised a parent on their deathbed."),
      Map.entry(42, "You promised a parent on their deathbed."),
      Map.entry(43, "To escape disgrace."), Map.entry(44, "To escape disgrace."),
      Map.entry(45, "You had to pay a debt."), Map.entry(46, "You had to pay a debt."),
      Map.entry(51, "You always chased thrills."), Map.entry(52, "You always chased thrills."),
      Map.entry(53, "You wanted to see the inside of a Greatship."),
      Map.entry(54, "You wanted to see the inside of a Greatship."),
      Map.entry(55, "You were fascinated by the mystery of the Builders."),
      Map.entry(56, "You were fascinated by the mystery of the Builders."),
      Map.entry(61, "You wanted to impress someone you loved."),
      Map.entry(62, "You wanted to impress someone you loved."),
      Map.entry(63, "You were hunted by someone bad, this was a way out."),
      Map.entry(64, "You were hunted by someone bad, this was a way out."),
      Map.entry(65, "Ship City simply felt too small for you."),
      Map.entry(66, "Ship City simply felt too small for you.")));

  /** Returns the D66 table for why the Explorer joined the Explorers Guild. */
  public static D66Table<String> getExplorerReasonTable() {
    return EXPLORER_REASON_TABLE;
  }

  /** Looks up a reason by D66 value. */
  public static String getExplorerReasonByD66(int d66Value) {
    return EXPLORER_REASON_TABLE.getResult(d66Value);
  }

  // ── D66 Appearances Table (Ch. 2) ───────────────────────────────────────
  private static final D66Table<String> APPEARANCES_TABLE = new D66Table<>(Map.ofEntries(
      Map.entry(11, "Intense stare"), Map.entry(12, "Intense stare"),
      Map.entry(13, "Unruly hair"), Map.entry(14, "Unruly hair"),
      Map.entry(15, "Frayed scarf"), Map.entry(16, "Frayed scarf"),
      Map.entry(21, "Turban"), Map.entry(22, "Turban"),
      Map.entry(23, "Patterned shirt"), Map.entry(24, "Patterned shirt"),
      Map.entry(25, "Jacket with many pockets"), Map.entry(26, "Jacket with many pockets"),
      Map.entry(31, "Long facial scar"), Map.entry(32, "Long facial scar"),
      Map.entry(33, "Shaved head"), Map.entry(34, "Shaved head"),
      Map.entry(35, "Kohl-lined eyes"), Map.entry(36, "Kohl-lined eyes"),
      Map.entry(41, "Leather coat"), Map.entry(42, "Leather coat"),
      Map.entry(43, "Intricate face tattoo"), Map.entry(44, "Intricate face tattoo"),
      Map.entry(45, "Henna tattooed hands"), Map.entry(46, "Henna tattooed hands"),
      Map.entry(51, "Patterned scarf covering face"), Map.entry(52, "Patterned scarf covering face"),
      Map.entry(53, "Embroidered cape"), Map.entry(54, "Embroidered cape"),
      Map.entry(55, "Swirling tunic"), Map.entry(56, "Swirling tunic"),
      Map.entry(61, "Ancient coin in necklace"), Map.entry(62, "Ancient coin in necklace"),
      Map.entry(63, "Crimson robe"), Map.entry(64, "Crimson robe"),
      Map.entry(65, "Long black jacket"), Map.entry(66, "Long black jacket")));

  /**
   * Returns the D66 Appearances table for random or lookup-based appearance
   * selection.
   */
  public static D66Table<String> getAppearancesTable() {
    return APPEARANCES_TABLE;
  }

  /** Looks up an appearance by D66 value. */
  public static String getAppearanceByD66(int d66Value) {
    return APPEARANCES_TABLE.getResult(d66Value);
  }

  // ── D66 Keepsakes Table (Ch. 2) ──────────────────────────────────────────
  private static final D66Table<String> KEEPSAKES_TABLE = new D66Table<>(Map.ofEntries(
      Map.entry(11, "Model of the Greatship Hammurabi"),
      Map.entry(12, "Recruiting pamphlet for the cult of the Lady with a Thousand Eyes, smells of perfume"),
      Map.entry(13, "Worn silver coin from the Old Horizon"),
      Map.entry(14, "Ceremonial Coriolite dagger"),
      Map.entry(15, "Piece of a Builder shard"),
      Map.entry(16, "Pouch with cirra-cirra leaves"),
      Map.entry(21, "Well-read zinti novel: Ninhulam – The Golden Star"),
      Map.entry(22, "Small statuette depicting the Uncreated Spirit"),
      Map.entry(23, "Yellowed article from the Jumuah Gazette"),
      Map.entry(24, "Picture plate of a loved one"),
      Map.entry(25, "Medallion depicting the Coriolite Traveler icon"),
      Map.entry(26, "Dog-eared book: Mind-expanding Fungi Recipes Vol. IV by J.W. Markatalam"),
      Map.entry(31, "Engraved brass harmonica from Dabaran"),
      Map.entry(32, "Out-of-date star chart by master Algebraist Zouridian"),
      Map.entry(33, "Commemorative amulet celebrating the Three Captains"),
      Map.entry(34, "Engraved smoking pipe"),
      Map.entry(35, "Well-used journal"),
      Map.entry(36, "Old automaton doll, can crawl but not walk"),
      Map.entry(41, "Dried and very rare purple kifu flower"),
      Map.entry(42, "Tame prana-rat named Zozo"),
      Map.entry(43, "Flask of makh spirits, brewed in the Turbine Halls in '77"),
      Map.entry(44, "Signed biography by veteran delver Jereman: Into the Unknown"),
      Map.entry(45, "Rock in shifting hues found in the Great Vault of Xu"),
      Map.entry(46, "Wooden box with colors and paintbrush"),
      Map.entry(51, "Small music box playing The First and Last Star"),
      Map.entry(52, "Kuan Theater programme, season of '86. Signed by actor: \"To my darling\""),
      Map.entry(53, "Glass bottle with miniature forest"),
      Map.entry(54, "Bronze medal issued by Navigators Guild Youth League – \"Junior Algebraist Meritus\""),
      Map.entry(55, "Broken Dabarani bone mask"),
      Map.entry(56, "Sealed glass sphere with an ink-black Blight lily"),
      Map.entry(61, "Old deed to a repurposed ship-dwelling in Hull Town, missing the address"),
      Map.entry(62, "Children's book belonging to a missed sibling: My First Garden"),
      Map.entry(63, "Letter from uncle that went missing in the Serpentine in '79"),
      Map.entry(64, "Worn travel edition of classic game Shroom!"),
      Map.entry(65, "An old fusillard bullet in a necklace"),
      Map.entry(66, "A supposed Builder artifact, inert and broken")));

  /**
   * Returns the D66 Keepsakes table for random or lookup-based keepsake
   * selection.
   */
  public static D66Table<String> getKeepsakesTable() {
    return KEEPSAKES_TABLE;
  }

  /** Looks up a keepsake by D66 value. */
  public static String getKeepsakeByD66(int d66Value) {
    return KEEPSAKES_TABLE.getResult(d66Value);
  }

  // ── D66 Origin Table (Ch. 2) ─────────────────────────────────────────────
  private static final List<Origin> DEFAULT_ORIGINS = List.of(
      // D66 11: The Decrepit Halls of an Orphanage
      new Origin("The Decrepit Halls of an Orphanage",
          new Talent("Stealthy", "Staying hidden", TalentCategory.STEALTH_MOBILITY, 3,
              "+1 base die per talent level for staying hidden"),
          "The Navigators Guild",
          contacts("Second Cartographer Masmirand", "Thief Youkobos", "Algebraist Anapur"),
          11, 11),
      // D66 12-14: Among the Hulks and Wrecks of Hull Town
      new Origin("Among the Hulks and Wrecks of Hull Town",
          new Talent("Exo-Specialist", "Exo suit operation", TalentCategory.VEHICLE_EXO, 3,
              "+1 base die per talent level when handling an exo of some kind"),
          variableFactionTable(),
          contacts("Hull Guard Hima Qu", "Gardener Mellima Joucol", "Toad aspirant Bizo"),
          12, 14),
      // D66 15-22: Deep Inside the Factory City of the Turbine Halls
      new Origin("Deep Inside the Factory City of the Turbine Halls",
          new Talent("Mechanic", "Repairing vehicles and devices", TalentCategory.EQUIPMENT, 3,
              "+1 base die per talent level for repairing vehicles and other mechanical devices"),
          "The Machinists Guild",
          contacts("Driller Hasara Doukem", "Loader Bor Berkem", "Third mechanic Yina Caph"),
          15, 22),
      // D66 23-31: In the Purple Meadows of the Cave Gardens
      new Origin("In the Purple Meadows of the Cave Gardens",
          new Talent("Botanist", "Cultivating and understanding flora", TalentCategory.KNOWLEDGE, 3,
              "+1 base die per talent level for cultivating plants or fungi and understanding flora and ivy"),
          "The Gardeners Guild",
          contacts("Gardener Preci Loukou", "Second botanist Zahre Uhat", "Root cutter Yisam Recha"),
          23, 31),
      // D66 32-36: In the Eternal Dusk of the Inner Sanctum
      new Origin("In the Eternal Dusk of the Inner Sanctum",
          new Talent("Cultural Savant", "Understanding customs", TalentCategory.KNOWLEDGE, 3,
              "+1 base die per talent level to understand customs and cultural habits in the Lost Horizon"),
          "The Coriolites",
          contacts("Seer Harima din-Hrama", "First servant Kir", "Coriolite guard Lucret Ventri"),
          32, 36),
      // D66 41-45: In the Lighthouse on the Edge of the Dark
      new Origin("In the Lighthouse on the Edge of the Dark",
          new Talent("Lookout", "Spotting threats", TalentCategory.STEALTH_MOBILITY, 3,
              "+1 base die per talent level to rolls for spotting approaching threats"),
          "The Navigators Guild",
          contacts("Lighthouse keeper Hamareous", "Ice trader Kamara Yves", "Rim zealot Eriman Desram"),
          41, 45),
      // D66 46-53: Among the Hulks of a Scavenger Herd
      new Origin("Among the Hulks of a Scavenger Herd",
          new Talent("Zero-G Training", "Zero gravity operations", TalentCategory.STEALTH_MOBILITY, 1,
              "You suffer no negative effects when operating in zero gravity environments"),
          "Wreckers",
          contacts("Star prophet Yesima", "Astro wallah Qumar D'nima", "Hull cutter Limiri Treidis"),
          46, 53),
      // D66 54-55: In the Depths of a Greatship
      new Origin("In the Depths of a Greatship",
          new Talent("Shuttle Pilot", "Piloting shuttles", TalentCategory.VEHICLE_EXO, 3,
              "+1 base die per talent level to Agility rolls for piloting a shuttle"),
          "The Navigators Guild",
          contacts("First mate Kander Zinn", "Hull warden Mekomos Hagg", "Cargo specialist Kosto Wahma"),
          54, 55),
      // D66 56-61: In the Depths of a Mining Colony
      new Origin("In the Depths of a Mining Colony",
          new Talent("Miner", "Drilling and digging", TalentCategory.EQUIPMENT, 3,
              "+1 base die per talent level to drill, dig, or secure an underground cave or tunnel"),
          "Mining Combine",
          contacts("Dust miner Xavi Bordu", "Veteran driller Mavandra Xo", "Union organizer Lab Sindra"),
          56, 61),
      // D66 62-63: Under the Sunless Sky of the Far Colonies
      new Origin("Under the Sunless Sky of the Far Colonies",
          new Talent("Endurance", "Resisting harsh conditions", TalentCategory.RESILIENCE, 3,
              "+1 base die per talent level for resisting vacuum, suffocation, and cold"),
          "Mining Combine",
          contacts("Tug pilot Aritra Bey", "Zapti officer Lei Kavani", "Cantina owner Rada Johuna"),
          62, 63),
      // D66 64: Among the Alleys and Shanties of Aluminum Bay
      new Origin("Among the Alleys and Shanties of Aluminum Bay",
          new Talent("Actor", "Bluffing and lying", TalentCategory.SOCIAL, 3,
              "+1 base die per talent level when rolling to bluff or tell a lie to an NPC"),
          variableFactionTable(),
          contacts("Hull cutter Rey Mouri", "Rigger Canti Gopa", "Crane rat Mi Aram"),
          64, 64),
      // D66 65: Under the Iron Sky of the Dome
      new Origin("Under the Iron Sky of the Dome",
          new Talent("Acrobat", "Jumping, climbing, and running", TalentCategory.STEALTH_MOBILITY, 3,
              "+1 base die per talent level to jumping, climbing, and running"),
          variableFactionTable(),
          contacts("Cook Lissa Losoi", "Alley actor Rizan Mandra", "Publisher Orun Okan"),
          65, 65),
      // D66 66: Somewhere in the Eternal Fog of the Haze
      new Origin("Somewhere in the Eternal Fog of the Haze",
          new Talent("Sleight of Hand", "Tricks and pickpocketing", TalentCategory.STEALTH_MOBILITY, 3,
              "+1 base die per talent level to rolls for playing tricks with your hands and pickpocketing"),
          "The Black Toad",
          contacts("Dust Runner Giina Coult", "Bone breaker Vasil Fash", "Assassin Grachi Sourtan"),
          66, 66));

  /** Returns an unmodifiable list of all 13 origins from the D66 Origin Table. */
  public static List<Origin> getDefaultOrigins() {
    return DEFAULT_ORIGINS;
  }

  /** Finds the Origin matching a given D66 roll value. */
  public static Origin getOriginByD66(int d66Value) {
    return DEFAULT_ORIGINS.stream()
        .filter(o -> o.matchesD66(d66Value))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No origin matches D66 value: " + d66Value));
  }

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

    // Resolve contact and faction via D6 rolls
    int contactRoll = rng.nextInt(6) + 1;
    explorer.setResolvedContact(origin.getContact(contactRoll));
    if (origin.hasVariableFaction()) {
      int factionRoll = rng.nextInt(6) + 1;
      explorer.setResolvedFaction(origin.getFaction(factionRoll));
    } else {
      explorer.setResolvedFaction(origin.getAssociatedFaction());
    }

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

    // If quirk is null, roll on the D66 Quirks table; otherwise use the provided
    // string
    if (quirk == null) {
      QUIRKS_TABLE.roll();
      explorer.setQuirk(QUIRKS_TABLE.getResult(QUIRKS_TABLE.getLastRollValue()));
    } else {
      explorer.setQuirk(quirk);
    }

    // If keepsake is null, roll on the D66 Keepsakes table; otherwise use the
    // provided string
    if (keepsake == null) {
      KEEPSAKES_TABLE.roll();
      explorer.setKeepsake(KEEPSAKES_TABLE.getResult(KEEPSAKES_TABLE.getLastRollValue()));
    } else {
      explorer.setKeepsake(keepsake);
    }

    // If appearance is null, roll on the D66 Appearances table; otherwise use the
    // provided string
    if (appearance == null) {
      APPEARANCES_TABLE.roll();
      explorer.setAppearance(APPEARANCES_TABLE.getResult(APPEARANCES_TABLE.getLastRollValue()));
    } else {
      explorer.setAppearance(appearance);
    }

    // Roll reason for becoming an Explorer (Ch. 2 step 10)
    EXPLORER_REASON_TABLE.roll();
    explorer.setExplorerReason(EXPLORER_REASON_TABLE.getResult(EXPLORER_REASON_TABLE.getLastRollValue()));

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
