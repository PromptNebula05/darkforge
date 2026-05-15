package darkforge.data;

import darkforge.exception.GameDataLoadException;
import darkforge.model.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Singleton that loads, validates, and caches all
 * static game data from JSON resource files at
 * startup. Replaces hardcoded data in
 * ExplorerFactory with runtime-loaded,
 * externalized JSON.
 *
 * Call initialize() once at startup before any
 * game operations. All public getters return
 * unmodifiable views.
 */
public class GameDataProvider {
    private static final GameDataProvider INSTANCE =
            new GameDataProvider();

    // --- Fields ---
    private List<Origin> origins;
    private Map<Integer, String> quirks;
    private Map<Integer, String> keepsakes;
    private Map<Integer, String> appearances;
    private Map<Integer, String> explorerReasons;
    private Map<String, ProfessionData> professions;
    private Map<String, Map<Integer, String>>
            firstNameTables;
    private Map<String, Map<Integer, String>>
            lastNameTables;
    private boolean loaded = false;

    private GameDataProvider() {}

    public static GameDataProvider getInstance() {
        return INSTANCE;
    }

    /**
     * Load all game data from JSON resources and
     * validate. Call once at startup. Throws
     * GameDataLoadException if any file is missing,
     * malformed, or fails validation.
     */
    public synchronized void initialize() {
        if (loaded) return;

        // Core D66 tables
        origins = loadOrigins();
        quirks = loadD66Map("quirks.json");
        keepsakes = loadD66Map("keepsakes.json");
        appearances =
                loadD66Map("appearances.json");
        explorerReasons =
                loadD66Map("explorer-reasons.json");

        // Profession data from professions.json
        professions = loadProfessions();

        // Name tables (one first/last pair per
        // profession)
        firstNameTables = new HashMap<>();
        lastNameTables = new HashMap<>();
        for (String prof : professions.keySet()) {
            String key = prof.toLowerCase();
            firstNameTables.put(key,
                    loadD66Map("names/"
                            + key + "-first.json"));
            lastNameTables.put(key,
                    loadD66Map("names/"
                            + key + "-last.json"));
        }

        // Run startup validation
        GameDataValidator.validate(this);

        loaded = true;
    }

    // =========================================
    // Public accessors (immutable views)
    // =========================================

    public List<Origin> getOrigins() {
        return Collections
                .unmodifiableList(origins);
    }

    public Origin getOriginByD66(int d66Value) {
        return origins.stream()
                .filter(o -> o.matchesD66(d66Value))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No origin matches D66 value: "
                                        + d66Value));
    }

    public Map<Integer, String> getQuirks() {
        return Collections
                .unmodifiableMap(quirks);
    }

    public Map<Integer, String> getKeepsakes() {
        return Collections
                .unmodifiableMap(keepsakes);
    }

    public Map<Integer, String> getAppearances() {
        return Collections
                .unmodifiableMap(appearances);
    }

    public Map<Integer, String>
    getExplorerReasons() {
        return Collections
                .unmodifiableMap(explorerReasons);
    }

    /** Data-driven from professions.json keys. */
    public List<String> getValidProfessionNames() {
        return List.copyOf(professions.keySet());
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

    // =========================================
    // Private loaders
    // =========================================

    private String loadResource(String filename) {
        try (InputStream is = getClass()
                .getResourceAsStream(
                        "/" + filename)) {
            if (is == null) {
                throw new GameDataLoadException(
                        filename,
                        "Missing game data file");
            }
            return new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GameDataLoadException(
                    filename,
                    "Failed to read game data", e);
        }
    }

    private Map<Integer, String> loadD66Map(
            String filename) {
        JSONObject obj = new JSONObject(
                loadResource(filename));
        Map<Integer, String> map =
                new LinkedHashMap<>();
        for (String key : obj.keySet()) {
            map.put(Integer.parseInt(key),
                    obj.getString(key));
        }
        return map;
    }

    private List<Origin> loadOrigins() {
        JSONArray arr = new JSONArray(
                loadResource("origins.json"));
        List<Origin> result = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            result.add(
                    parseOrigin(arr.getJSONObject(i)));
        }
        return result;
    }

    private Origin parseOrigin(JSONObject o) {
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
        for (String key : contactsObj.keySet()) {
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
            for (String key : vfObj.keySet()) {
                factionMap.put(
                        Integer.parseInt(key),
                        vfObj.getString(key));
            }
            return new Origin(
                    location, freeTalent,
                    new darkforge.mechanics
                            .D6Table<>(factionMap),
                    contactsMap,
                    d66Low, d66High);
        } else {
            return new Origin(
                    location, freeTalent,
                    faction, contactsMap,
                    d66Low, d66High);
        }
    }

    private Map<String, ProfessionData>
    loadProfessions() {
        JSONArray arr = new JSONArray(
                loadResource("professions.json"));
        Map<String, ProfessionData> map =
                new LinkedHashMap<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject p = arr.getJSONObject(i);
            String name = p.getString("name");
            map.put(name,
                    parseProfessionData(p));
        }
        return map;
    }

    private ProfessionData parseProfessionData(
            JSONObject p) {
        String name = p.getString("name");
        Attribute keyAttr = Attribute.valueOf(
                p.getString("keyAttribute"));

        JSONArray specArr =
                p.getJSONArray("specialties");
        List<ProfessionData.SpecialtyData> specs =
                new ArrayList<>();
        for (int i = 0;
             i < specArr.length(); i++) {
            JSONObject s =
                    specArr.getJSONObject(i);
            specs.add(
                    new ProfessionData.SpecialtyData(
                            s.getString("name"),
                            s.getString("description"),
                            s.getString(
                                    "freeTalentName")));
        }

        JSONArray talArr =
                p.getJSONArray("talents");
        List<ProfessionData.TalentData> talents =
                new ArrayList<>();
        for (int i = 0;
             i < talArr.length(); i++) {
            JSONObject t =
                    talArr.getJSONObject(i);
            talents.add(
                    new ProfessionData.TalentData(
                            t.getString("name"),
                            t.getString("category"),
                            t.getInt("maxLevel"),
                            t.getString("description"),
                            t.getString("effect")));
        }

        JSONArray eqSets =
                p.getJSONArray(
                        "startingEquipmentSets");
        List<List<ProfessionData.EquipmentData>>
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
                                e.getString("weight"),
                                e.getInt("gearBonus"),
                                e.getString("source")));
            }
            equipSets.add(items);
        }

        return new ProfessionData(name, keyAttr,
                specs, talents, equipSets);
    }
}