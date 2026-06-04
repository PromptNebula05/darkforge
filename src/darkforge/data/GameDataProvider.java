package darkforge.data;

import darkforge.crew.BirdType;
import darkforge.crew.GarudaPower;
import darkforge.exception.GameDataLoadException;
import darkforge.mechanics.D6Table;
import darkforge.model.*;

import org.json.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Singleton that loads and caches all
 * static game data from JSON resource
 * files. initialize() populates every
 * table once at startup; reload() re-reads
 * just the 10 item-catalog JSON files and
 * rebuilds the cached ItemCatalog so the
 * GUI can pick up edits without
 * restarting the application.
 */
public class GameDataProvider {

    private static final GameDataProvider
            INSTANCE = new GameDataProvider();

    // =========================================
    // Fields
    // =========================================

    private List<Origin> origins;
    private Map<Integer, String> quirks;
    private Map<Integer, String> keepsakes;
    private Map<Integer, String> appearances;
    private Map<Integer, String>
            explorerReasons;
    private Map<String, ProfessionData>
            professions;
    private Map<String, Map<Integer, String>>
            firstNameTables;
    private Map<String, Map<Integer, String>>
            lastNameTables;
    private GarudaPowerRegistry
            garudaPowerRegistry;
    private TalentRegistry talentRegistry;
    private Map<Integer, String>
            crewNamePrefixes;
    private Map<Integer, String>
            crewNameSuffixes;
    private Map<Integer, String> birdColors;
    private Map<Integer, String>
            birdBodyFeatures;
    private Map<Integer, String>
            birdPersonalities;
    private Map<Integer, String> birdNames;
    private ItemCatalog itemCatalog;
    private boolean loaded = false;

    private GameDataProvider() {}

    public static GameDataProvider
    getTheInstance() {
        return INSTANCE;
    }

    // =========================================
    // Initialization
    // =========================================

    public synchronized void initialize() {
        if (loaded) return;
        origins = loadOrigins();
        quirks = loadD66Map("quirks.json");
        keepsakes =
                loadD66Map("keepsakes.json");
        appearances =
                loadD66Map("appearances.json");
        explorerReasons =
                loadD66Map(
                        "explorer-reasons.json");
        professions = loadProfessions();
        firstNameTables = new HashMap<>();
        lastNameTables = new HashMap<>();
        for (String prof
                : professions.keySet()) {
            String key = prof.toLowerCase();
            firstNameTables.put(key,
                    loadD66Map("names/"
                            + key + "-first.json"));
            lastNameTables.put(key,
                    loadD66Map("names/"
                            + key + "-last.json"));
        }
        garudaPowerRegistry =
                loadGarudaPowers();
        talentRegistry =
                loadTalentRegistry();
        loadCrewNames();
        loadBirdAppearances();
        itemCatalog = loadItemCatalog();
        GameDataValidator.validate(this);
        loaded = true;
    }

    /**
     * Re-reads the 10 item-catalog JSON
     * resource files (weapons, armor,
     * gear, vehicle modules, cargo) and
     * rebuilds the cached ItemCatalog.
     * Other static game data (origins,
     * quirks, professions, talents, name
     * tables, bird appearances) is not
     * reloaded.
     *
     * Safe to call from a worker thread —
     * no Swing components are touched.
     * Callers that hold the previous
     * ItemCatalog reference must replace
     * it with the value returned here.
     *
     * @return the freshly-rebuilt
     *         ItemCatalog
     * @throws IllegalStateException if
     *         initialize() has not yet
     *         been called
     */
    public synchronized ItemCatalog reload() {
        if (!loaded) {
            throw new IllegalStateException(
                    "GameDataProvider must be"
                            + " initialized before"
                            + " reload() can be"
                            + " called");
        }
        this.itemCatalog = loadItemCatalog();
        return this.itemCatalog;
    }

    // =========================================
    // Public accessors
    // =========================================

    public List<Origin> getOrigins() {
        return Collections
                .unmodifiableList(origins);
    }

    public Origin getOriginByD66(
            int d66Value) {
        return origins.stream()
                .filter(o ->
                        o.matchesD66(d66Value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No origin matches D66: "
                                        + d66Value));
    }

    public Map<Integer, String> getQuirks() {
        return Collections
                .unmodifiableMap(quirks);
    }

    public Map<Integer, String>
    getKeepsakes() {
        return Collections
                .unmodifiableMap(keepsakes);
    }

    public Map<Integer, String>
    getAppearances() {
        return Collections
                .unmodifiableMap(appearances);
    }

    public Map<Integer, String>
    getExplorerReasons() {
        return Collections
                .unmodifiableMap(
                        explorerReasons);
    }

    public List<String>
    getValidProfessionNames() {
        return List.copyOf(
                professions.keySet());
    }

    public ProfessionData getProfession(
            String name) {
        return professions.get(name);
    }

    public Map<Integer, String> getFirstNames(
            String profession) {
        return Collections.unmodifiableMap(
                firstNameTables.getOrDefault(
                        profession.toLowerCase(),
                        Map.of()));
    }

    public Map<Integer, String> getLastNames(
            String profession) {
        return Collections.unmodifiableMap(
                lastNameTables.getOrDefault(
                        profession.toLowerCase(),
                        Map.of()));
    }

    public GarudaPowerRegistry
    getGarudaPowerRegistry() {
        return garudaPowerRegistry;
    }

    public TalentRegistry
    getTalentRegistry() {
        return talentRegistry;
    }

    public Map<Integer, String>
    getCrewNamePrefixes() {
        return Collections
                .unmodifiableMap(
                        crewNamePrefixes);
    }

    public Map<Integer, String>
    getCrewNameSuffixes() {
        return Collections
                .unmodifiableMap(
                        crewNameSuffixes);
    }

    public Map<Integer, String>
    getBirdColors() {
        return Collections
                .unmodifiableMap(birdColors);
    }

    public Map<Integer, String>
    getBirdBodyFeatures() {
        return Collections
                .unmodifiableMap(
                        birdBodyFeatures);
    }

    public Map<Integer, String>
    getBirdPersonalities() {
        return Collections
                .unmodifiableMap(
                        birdPersonalities);
    }

    public Map<Integer, String>
    getBirdNames() {
        return Collections
                .unmodifiableMap(birdNames);
    }

    public ItemCatalog getItemCatalog() {
        return itemCatalog;
    }

    // =========================================
    // Resource loading
    // =========================================

    private String loadResource(
            String filename) {
        try (InputStream is = getClass()
                .getResourceAsStream(
                        "/" + filename)) {
            if (is == null) {
                throw new
                        GameDataLoadException(
                        filename,
                        "Missing game data file");
            }
            return new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new
                    GameDataLoadException(
                    filename,
                    "Failed to read game data",
                    e);
        }
    }

    // =========================================
    // JSON parsing helpers
    // =========================================

    private Map<Integer, String> loadD66Map(
            String filename) {
        return parseIntKeyMap(
                new JSONObject(
                        loadResource(
                                filename)));
    }

    private Map<Integer, String>
    parseIntKeyMap(JSONObject obj) {
        Map<Integer, String> map =
                new LinkedHashMap<>();
        for (String key : obj.keySet()) {
            map.put(
                    Integer.parseInt(key),
                    obj.getString(key));
        }
        return map;
    }

    // =========================================
    // Origins
    // =========================================

    private List<Origin> loadOrigins() {
        JSONArray arr = new JSONArray(
                loadResource("origins.json"));
        List<Origin> result =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            result.add(parseOrigin(
                    arr.getJSONObject(i)));
        }
        return result;
    }

    private Origin parseOrigin(
            JSONObject o) {
        String location =
                o.getString("location");
        String faction =
                o.getString("faction");
        boolean variableFaction =
                o.optBoolean(
                        "variableFaction", false);
        JSONObject contactsObj =
                o.getJSONObject("contacts");
        Map<Integer, String> contactsMap =
                new HashMap<>();
        for (String key
                : contactsObj.keySet()) {
            contactsMap.put(
                    Integer.parseInt(key),
                    contactsObj.getString(key));
        }
        JSONObject tObj =
                o.getJSONObject("freeTalent");
        Talent freeTalent = new Talent(
                tObj.getString("name"),
                tObj.getString("description"),
                TalentCategory.valueOf(
                        tObj.getString("category")),
                tObj.getInt("maxLevel"),
                tObj.getString("effect"));
        JSONArray range =
                o.getJSONArray("d66Range");
        int d66Low = range.getInt(0);
        int d66High = range.getInt(1);
        if (variableFaction) {
            JSONObject vfObj =
                    o.getJSONObject(
                            "variableFactionTable");
            Map<Integer, String> factionMap =
                    new HashMap<>();
            for (String key
                    : vfObj.keySet()) {
                factionMap.put(
                        Integer.parseInt(key),
                        vfObj.getString(key));
            }
            return new Origin(
                    location, freeTalent,
                    new D6Table<>(factionMap,
                            new Random()),
                    contactsMap,
                    d66Low, d66High);
        } else {
            return new Origin(
                    location, freeTalent,
                    faction, contactsMap,
                    d66Low, d66High);
        }
    }

    // =========================================
    // Professions
    // =========================================

    private Map<String, ProfessionData>
    loadProfessions() {
        JSONArray arr = new JSONArray(
                loadResource(
                        "professions.json"));
        Map<String, ProfessionData> map =
                new LinkedHashMap<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject p =
                    arr.getJSONObject(i);
            String name =
                    p.getString("name");
            map.put(name,
                    parseProfessionData(p));
        }
        return map;
    }

    private ProfessionData
    parseProfessionData(
            JSONObject p) {
        String name = p.getString("name");
        Attribute keyAttr = Attribute.valueOf(
                p.getString("keyAttribute"));
        JSONArray specArr =
                p.getJSONArray("specialties");
        List<ProfessionData.SpecialtyData>
                specs = new ArrayList<>();
        for (int i = 0;
             i < specArr.length(); i++) {
            JSONObject s =
                    specArr.getJSONObject(i);
            specs.add(
                    new ProfessionData
                            .SpecialtyData(
                            s.getString("name"),
                            s.getString(
                                    "description"),
                            s.getString(
                                    "freeTalentName")));
        }
        JSONArray talArr =
                p.getJSONArray("talents");
        List<ProfessionData.TalentData>
                talents = new ArrayList<>();
        for (int i = 0;
             i < talArr.length(); i++) {
            JSONObject t =
                    talArr.getJSONObject(i);
            talents.add(
                    new ProfessionData
                            .TalentData(
                            t.getString("name"),
                            t.getString("category"),
                            t.getInt("maxLevel"),
                            t.getString(
                                    "description"),
                            t.getString(
                                    "effect")));
        }
        JSONArray eqSets =
                p.getJSONArray(
                        "startingEquipmentSets");
        List<List<ProfessionData
                .EquipmentData>>
                equipSets = new ArrayList<>();
        for (int i = 0;
             i < eqSets.length(); i++) {
            JSONArray set =
                    eqSets.getJSONArray(i);
            List<ProfessionData.EquipmentData>
                    items = new ArrayList<>();
            for (int j = 0;
                 j < set.length(); j++) {
                JSONObject e =
                        set.getJSONObject(j);
                items.add(
                        new ProfessionData
                                .EquipmentData(
                                e.getString("name"),
                                e.getString(
                                        "description"),
                                e.getString(
                                        "weight"),
                                e.getInt(
                                        "gearBonus")));
            }
            equipSets.add(items);
        }
        return new ProfessionData(name,
                keyAttr, specs, talents,
                equipSets);
    }

    // =========================================
    // Garuda powers
    // =========================================

    private GarudaPowerRegistry
    loadGarudaPowers() {
        JSONObject root = new JSONObject(
                loadResource(
                        "garuda-powers.json"));
        JSONArray arr =
                root.getJSONArray("powers");
        List<GarudaPower> powers =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject p =
                    arr.getJSONObject(i);
            Set<BirdType> nativeTypes =
                    new HashSet<>();
            JSONArray typesArr =
                    p.optJSONArray(
                            "nativeTypes");
            if (typesArr != null) {
                for (int j = 0;
                     j < typesArr.length();
                     j++) {
                    nativeTypes.add(
                            BirdType.valueOf(
                                    typesArr.getString(
                                            j)));
                }
            }
            powers.add(new GarudaPower(
                    p.getString("name"),
                    p.getString("description"),
                    p.getString("effect"),
                    p.getBoolean("isBasic"),
                    nativeTypes,
                    p.getInt("energyCost")));
        }
        return new GarudaPowerRegistry(
                powers);
    }

    // =========================================
    // Talents
    // =========================================

    private TalentRegistry
    loadTalentRegistry() {
        JSONArray arr = new JSONArray(
                loadResource("talents.json"));
        List<Talent> talents =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject t =
                    arr.getJSONObject(i);
            talents.add(new Talent(
                    t.getString("name"),
                    t.getString("description"),
                    TalentCategory.valueOf(
                            t.getString("category")),
                    t.getInt("maxLevel"),
                    t.getString("effect")));
        }
        Map<String, List<String>>
                profTalentNames =
                new LinkedHashMap<>();
        for (Map.Entry<String,
                ProfessionData>
                entry :
                professions.entrySet()) {
            List<String> names =
                    new ArrayList<>();
            for (ProfessionData.TalentData td
                    : entry.getValue()
                    .getTalents()) {
                names.add(td.name());
            }
            profTalentNames.put(
                    entry.getKey(), names);
        }
        return new TalentRegistry(
                talents, profTalentNames);
    }

    // =========================================
    // Crew names
    // =========================================

    private void loadCrewNames() {
        JSONObject root = new JSONObject(
                loadResource(
                        "crew-names.json"));
        crewNamePrefixes = parseIntKeyMap(
                root.getJSONObject(
                        "crewNamePrefixes"));
        crewNameSuffixes = parseIntKeyMap(
                root.getJSONObject(
                        "crewNameSuffixes"));
    }

    // =========================================
    // Bird appearances
    // =========================================

    private void loadBirdAppearances() {
        JSONObject root = new JSONObject(
                loadResource(
                        "bird-appearances.json"));
        birdColors = parseIntKeyMap(
                root.getJSONObject(
                        "birdColors"));
        birdBodyFeatures = parseIntKeyMap(
                root.getJSONObject(
                        "birdBodyFeatures"));
        birdPersonalities = parseIntKeyMap(
                root.getJSONObject(
                        "birdPersonalities"));
        birdNames = parseIntKeyMap(
                root.getJSONObject(
                        "birdNames"));
    }

    // =========================================
    // Item catalog
    // =========================================

    private ItemCatalog loadItemCatalog() {
        List<Item> items = new ArrayList<>();
        items.addAll(loadWeapons(
                "weapons-ranged.json"));
        items.addAll(loadWeapons(
                "weapons-melee.json"));
        items.addAll(loadWeapons(
                "weapons-heirloom.json"));
        items.addAll(loadArmor(
                "armor.json"));
        items.addAll(loadGeneralEquipment(
                "equipment-general.json"));
        items.addAll(loadVehicleModules(
                "rover-upgrades.json"));
        items.addAll(loadVehicleWeapons(
                "rover-weapons.json", false));
        items.addAll(loadVehicleModules(
                "shuttle-upgrades.json"));
        items.addAll(loadVehicleWeapons(
                "shuttle-weapons.json", true));
        items.addAll(loadCargoItems(
                "cargo-items.json"));
        return new ItemCatalog(items);
    }

    private List<Weapon> loadWeapons(
            String filename) {
        JSONArray arr = new JSONArray(
                loadResource(filename));
        List<Weapon> weapons =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject w =
                    arr.getJSONObject(i);
            JSONArray featArr =
                    w.optJSONArray("features");
            List<String> features =
                    new ArrayList<>();
            if (featArr != null) {
                for (int j = 0;
                     j < featArr.length();
                     j++) {
                    features.add(
                            featArr.getString(j));
                }
            }
            weapons.add(new Weapon(
                    w.getString("name"),
                    w.getString("description"),
                    w.getDouble("weight"),
                    w.getInt("cost"),
                    TechLevel.fromCode(
                            w.getString("techLevel")),
                    w.getBoolean("restricted"),
                    EquipmentWeight.fromWeight(
                            w.getDouble("weight")),
                    w.getInt("gearBonus"),
                    w.getInt("damage"),
                    w.getInt("crit"),
                    Grip.fromCode(
                            w.getString("grip")),
                    w.getString("range"),
                    WeaponType.valueOf(
                            w.getString(
                                    "weaponType")),
                    features));
        }
        return weapons;
    }

    private List<Armor> loadArmor(
            String filename) {
        JSONArray arr = new JSONArray(
                loadResource(filename));
        List<Armor> armorList =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject a =
                    arr.getJSONObject(i);
            JSONArray featArr =
                    a.optJSONArray("features");
            List<String> features =
                    new ArrayList<>();
            if (featArr != null) {
                for (int j = 0;
                     j < featArr.length();
                     j++) {
                    features.add(
                            featArr.getString(j));
                }
            }
            armorList.add(new Armor(
                    a.getString("name"),
                    a.getString("description"),
                    a.getDouble("weight"),
                    a.getInt("cost"),
                    a.getString("category"),
                    TechLevel.fromCode(
                            a.getString("techLevel")),
                    a.getBoolean("restricted"),
                    EquipmentWeight.fromWeight(
                            a.getDouble("weight")),
                    a.optInt("gearBonus", 0),
                    a.getInt("armorRating"),
                    a.getInt("blightProtection"),
                    a.getInt("extras"),
                    features));
        }
        return armorList;
    }

    private List<CharacterItem>
    loadGeneralEquipment(
            String filename) {
        JSONArray arr = new JSONArray(
                loadResource(filename));
        List<CharacterItem> items =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject e =
                    arr.getJSONObject(i);
            items.add(new CharacterItem(
                    e.getString("name"),
                    e.getString("description"),
                    e.getDouble("weight"),
                    e.getInt("cost"),
                    e.getString("category"),
                    TechLevel.fromCode(
                            e.getString("techLevel")),
                    e.getBoolean("restricted"),
                    e.optInt("gearBonus", 0)));
        }
        return items;
    }

    private List<VehicleModule>
    loadVehicleModules(
            String filename) {
        JSONArray arr = new JSONArray(
                loadResource(filename));
        List<VehicleModule> modules =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject m =
                    arr.getJSONObject(i);
            boolean isShuttle =
                    m.getBoolean(
                            "shuttleUpgrade");
            modules.add(new VehicleModule(
                    m.getString("name"),
                    m.getString("description"),
                    m.getInt("slotCost"),
                    m.getInt("cpCost"),
                    m.getString("moduleType"),
                    m.getString("effect"),
                    TechLevel.fromCode(
                            m.getString("techLevel")),
                    m.getBoolean("restricted"),
                    null,
                    isShuttle));
        }
        return modules;
    }

    private List<VehicleModule>
    loadVehicleWeapons(
            String filename,
            boolean shuttle) {
        JSONArray arr = new JSONArray(
                loadResource(filename));
        List<VehicleModule> weapons =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject w =
                    arr.getJSONObject(i);
            weapons.add(new VehicleModule(
                    w.getString("name"),
                    w.getString("description"),
                    1,
                    w.getInt("cpCost"),
                    "weapon",
                    String.format(
                            "Dmg %d Crit %d %s",
                            w.getInt("damage"),
                            w.getInt("crit"),
                            w.getString("range")),
                    TechLevel.fromCode(
                            w.getString("techLevel")),
                    w.getBoolean("restricted"),
                    null,
                    shuttle));
        }
        return weapons;
    }

    private List<CargoItem> loadCargoItems(
            String filename) {
        JSONArray arr = new JSONArray(
                loadResource(filename));
        List<CargoItem> items =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            JSONObject c =
                    arr.getJSONObject(i);
            items.add(new CargoItem(
                    c.getString("name"),
                    c.getString("description"),
                    c.getInt("supplyPoints"),
                    c.getString("cargoType"),
                    c.getInt("cost"),
                    TechLevel.fromCode(
                            c.getString(
                                    "techLevel"))));
        }
        return items;
    }
}