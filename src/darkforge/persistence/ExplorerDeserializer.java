package darkforge.persistence;

import darkforge.creation.ExplorerFactory;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.exception
        .CharacterCorruptionException.CorruptionType;
import darkforge.exception
        .InvalidProfessionException;
import darkforge.model.*;
import org.json.*;
import java.util.*;

/**
 * Reconstructs a fully validated Explorer from a
 * JSON string produced by ExplorerSerializer.
 *
 * Every field access goes through require*()
 * helpers that wrap JSONException into
 * CharacterCorruptionException with the correct
 * CorruptionType, field path, and detail message.
 *
 * Checked exception wrapping pattern:
 * Infrastructure exceptions (JSONException,
 * unchecked) are caught and re-thrown as domain
 * exceptions (CharacterCorruptionException,
 * checked) with the original as cause. This
 * preserves the stack trace for debugging while
 * giving the CLI a structured error to display.
 */
public class ExplorerDeserializer {
    private final ExplorerFactory factory;

    public ExplorerDeserializer() {
        this.factory = new ExplorerFactory();
    }



    /**
     * Deserialize a JSON string into a fully
     * validated Explorer.
     *
     * @param json     the JSON string to parse
     * @param filePath the source file path (used
     *                 in error messages)
     * @return a reconstructed Explorer
     * @throws CharacterCorruptionException if the
     *         JSON is malformed, missing required
     *         fields, contains invalid values, or
     *         has a version mismatch
     */
    public Explorer deserialize(String json,
                                String filePath)
            throws CharacterCorruptionException {
        JSONObject root = parseRoot(json, filePath);

        // --- Version check ---
        String version = requireString(root,
                "version", filePath);
        if (!version.equals(
                ExplorerSerializer
                        .FORMAT_VERSION)) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.VERSION_MISMATCH,
                    "version",
                    "Expected "
                            + ExplorerSerializer
                            .FORMAT_VERSION
                            + ", got " + version);
        }

        // --- Profession ---
        String professionName = requireString(
                root, "profession", filePath);

        // --- Name ---
        String name = requireString(
                root, "name", filePath);
        Explorer explorer =
                createProfessionInstance(
                        professionName, name, filePath);

        // --- Specialty ---
        if (root.has("specialty")
                && !root.isNull("specialty")) {
            JSONObject specObj = requireObject(
                    root, "specialty", filePath);
            explorer.setSpecialty(
                    deserializeSpecialty(
                            specObj, explorer,
                            filePath));
        }

        // --- Origin ---
        if (root.has("origin")
                && !root.isNull("origin")) {
            JSONObject originObj = requireObject(
                    root, "origin", filePath);
            deserializeOrigin(
                    originObj, explorer, filePath);
        }

        // --- Attributes ---
        JSONObject attrsObj = requireObject(
                root, "attributes", filePath);
        EnumMap<Attribute, Integer> attributes =
                deserializeAttributes(
                        attrsObj, filePath);
        explorer.setAttributes(attributes);

        // --- Talents ---
        JSONArray talentsArr = requireArray(
                root, "talents", filePath);
        deserializeTalents(
                talentsArr, explorer, filePath);

        // --- Equipment ---
        JSONArray equipArr = requireArray(
                root, "equipment", filePath);
        deserializeEquipment(
                equipArr, explorer, filePath);

        // --- Personal details ---
        if (root.has("personalDetails")
                && !root.isNull(
                "personalDetails")) {
            JSONObject pdObj = requireObject(
                    root, "personalDetails",
                    filePath);
            deserializePersonalDetails(
                    pdObj, explorer, filePath);
        }

        return explorer;
    }

    // =========================================
    // Root parsing
    // =========================================

    private JSONObject parseRoot(String json,
                                 String filePath)
            throws CharacterCorruptionException {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.MALFORMED_FORMAT,
                    "root", "Not valid JSON", e);
        }
    }

    // =========================================
    // Profession instantiation
    // =========================================

    private Explorer createProfessionInstance(
            String professionName, String name,
            String filePath)
            throws CharacterCorruptionException {
        try {
            return factory
                    .createProfessionInstance(
                            professionName, name);
        } catch (InvalidProfessionException e) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    "profession",
                    "Unknown profession: "
                            + professionName, e);
        }
    }

    // =========================================
    // Attributes
    // =========================================

    private EnumMap<Attribute, Integer>
    deserializeAttributes(
            JSONObject attrsObj,
            String filePath)
            throws CharacterCorruptionException {
        EnumMap<Attribute, Integer> result =
                new EnumMap<>(Attribute.class);
        for (Attribute attr : Attribute.values()) {
            String key = attr.name();
            String fieldPath =
                    "attributes." + key;
            if (!attrsObj.has(key)) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .MISSING_FIELD,
                        fieldPath,
                        "Missing attribute");
            }
            int val;
            try {
                val = attrsObj.getInt(key);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        fieldPath,
                        "Expected integer value",
                        e);
            }
            if (val < 2 || val > 6) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        fieldPath,
                        String.format(
                                "value %d, must be "
                                        + "2-6", val));
            }
            result.put(attr, val);
        }
        return result;
    }

    // =========================================
    // Specialty
    // =========================================

    private Specialty deserializeSpecialty(
            JSONObject specObj,
            Explorer explorer,
            String filePath)
            throws CharacterCorruptionException {
        String specName = requireString(
                specObj, "name",
                "specialty.name", filePath);
        String specDesc = requireString(
                specObj, "description",
                "specialty.description", filePath);

        // Resolve free talent from the explorer's
        // known specialties, or build a minimal one
        String freeTalentName = null;
        if (specObj.has("freeTalent")
                && !specObj.isNull("freeTalent")) {
            freeTalentName =
                    specObj.getString("freeTalent");
        }

        Talent freeTalent = null;
        if (freeTalentName != null) {
            freeTalent = resolveFreeTalent(
                    freeTalentName, explorer);
        }

        return new Specialty(
                specName, specDesc, freeTalent);
    }

    /**
     * Resolve a free talent by name from the
     * explorer's known talent pool.
     */
    private Talent resolveFreeTalent(
            String talentName,
            Explorer explorer) {
        // Search the explorer's key talents for
        // a matching name
        for (Talent t :
                explorer.getKeyTalents()) {
            if (t.getName()
                    .equalsIgnoreCase(
                            talentName)) {
                return t;
            }
        }
        // Fallback: create a minimal reference
        // talent (the full data will be in the
        // talents array)
        return new Talent(
                talentName, "",
                TalentCategory.GENERAL, 3,
                "Loaded from save");
    }

    // =========================================
    // Origin
    // =========================================

    private void deserializeOrigin(
            JSONObject originObj,
            Explorer explorer,
            String filePath)
            throws CharacterCorruptionException {
        String location = requireString(
                originObj, "location",
                "origin.location", filePath);

        String faction = null;
        if (originObj.has("faction")
                && !originObj.isNull("faction")) {
            faction =
                    originObj.getString("faction");
        }

        String contact = null;
        if (originObj.has("contact")
                && !originObj.isNull("contact")) {
            contact =
                    originObj.getString("contact");
        }

        String freeTalentName = null;
        if (originObj.has("freeTalent")
                && !originObj.isNull(
                "freeTalent")) {
            freeTalentName =
                    originObj.getString(
                            "freeTalent");
        }

        int d66Low = 0;
        int d66High = 0;
        if (originObj.has("d66Range")) {
            JSONArray range;
            try {
                range = originObj.getJSONArray(
                        "d66Range");
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        "origin.d66Range",
                        "Expected array", e);
            }
            if (range.length() != 2) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        "origin.d66Range",
                        "Expected 2 elements, "
                                + "got "
                                + range.length());
            }
            d66Low = range.getInt(0);
            d66High = range.getInt(1);
        }

        // Build a minimal free talent if named
        Talent freeTalent = null;
        if (freeTalentName != null) {
            freeTalent = new Talent(
                    freeTalentName, "",
                    TalentCategory.GENERAL, 3,
                    "Loaded from save");
        }

        // Construct Origin via deserialization
        // constructor (no contacts needed —
        // resolved contact stored on Explorer)
        Origin origin = new Origin(
                location, freeTalent,
                faction != null ? faction : "Unknown",
                d66Low, d66High);
        explorer.setOrigin(origin);

        // Set resolved details directly
        explorer.setResolvedFaction(faction);
        explorer.setResolvedContact(contact);
    }

    // =========================================
    // Talents
    // =========================================

    private void deserializeTalents(
            JSONArray talentsArr,
            Explorer explorer,
            String filePath)
            throws CharacterCorruptionException {
        for (int i = 0;
             i < talentsArr.length(); i++) {
            String prefix = "talents[" + i + "]";
            JSONObject tObj;
            try {
                tObj =
                        talentsArr.getJSONObject(i);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix,
                        "Expected JSON object",
                        e);
            }

            String tName = requireString(
                    tObj, "name",
                    prefix + ".name", filePath);
            String catStr = requireString(
                    tObj, "category",
                    prefix + ".category", filePath);

            TalentCategory category;
            try {
                category =
                        TalentCategory.valueOf(catStr);
            } catch (
                    IllegalArgumentException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix + ".category",
                        "Unknown talent category: "
                                + catStr, e);
            }

            int currentLevel = requireInt(
                    tObj, "currentLevel",
                    prefix + ".currentLevel",
                    filePath);
            int maxLevel = requireInt(
                    tObj, "maxLevel",
                    prefix + ".maxLevel",
                    filePath);
            String effect = requireString(
                    tObj, "effect",
                    prefix + ".effect", filePath);

            if (currentLevel < 0
                    || currentLevel > maxLevel) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix + ".currentLevel",
                        String.format(
                                "value %d, must be "
                                        + "0-%d",
                                currentLevel,
                                maxLevel));
            }

            Talent talent = new Talent(
                    tName, "", category,
                    maxLevel, currentLevel, effect);
            explorer.addTalent(talent);
        }
    }

    // =========================================
    // Equipment
    // =========================================

    private void deserializeEquipment(
            JSONArray equipArr,
            Explorer explorer,
            String filePath)
            throws CharacterCorruptionException {
        List<Equipment> items = new ArrayList<>();
        for (int i = 0;
             i < equipArr.length(); i++) {
            String prefix =
                    "equipment[" + i + "]";
            JSONObject eObj;
            try {
                eObj =
                        equipArr.getJSONObject(i);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix,
                        "Expected JSON object",
                        e);
            }

            String eName = requireString(
                    eObj, "name",
                    prefix + ".name", filePath);
            String eDesc = requireString(
                    eObj, "description",
                    prefix + ".description",
                    filePath);
            String weightStr = requireString(
                    eObj, "weight",
                    prefix + ".weight", filePath);

            EquipmentWeight weight;
            try {
                weight = EquipmentWeight.valueOf(
                        weightStr);
            } catch (
                    IllegalArgumentException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix + ".weight",
                        "Unknown weight: "
                                + weightStr, e);
            }

            int gearBonus = requireInt(
                    eObj, "gearBonus",
                    prefix + ".gearBonus",
                    filePath);

            items.add(new Equipment(
                    eName, eDesc, weight,
                    gearBonus));
        }
        explorer.setEquipment(items);
    }

    // =========================================
    // Personal details
    // =========================================

    private void deserializePersonalDetails(
            JSONObject pdObj,
            Explorer explorer,
            String filePath)
            throws CharacterCorruptionException {
        if (pdObj.has("quirk")
                && !pdObj.isNull("quirk")) {
            explorer.setQuirk(
                    pdObj.getString("quirk"));
        }
        if (pdObj.has("keepsake")
                && !pdObj.isNull("keepsake")) {
            explorer.setKeepsake(
                    pdObj.getString("keepsake"));
        }
        if (pdObj.has("appearance")
                && !pdObj.isNull("appearance")) {
            explorer.setAppearance(
                    pdObj.getString("appearance"));
        }
    }

    // =========================================
    // Require helpers — centralized field access
    // with CharacterCorruptionException wrapping
    // =========================================

    /**
     * Require a string field from a JSONObject.
     * Uses the key as the field path in errors.
     */
    private String requireString(JSONObject obj,
                                 String key, String filePath)
            throws CharacterCorruptionException {
        return requireString(
                obj, key, key, filePath);
    }

    /**
     * Require a string field with a custom
     * dotted field path for error messages.
     */
    private String requireString(JSONObject obj,
                                 String key, String fieldPath,
                                 String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected string value", e);
        }
    }

    /**
     * Require an integer field with a custom
     * dotted field path for error messages.
     */
    private int requireInt(JSONObject obj,
                           String key, String fieldPath,
                           String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getInt(key);
        } catch (JSONException e) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected integer value", e);
        }
    }

    /**
     * Require a JSONObject field from a parent
     * object. Uses the key as the field path.
     */
    private JSONObject requireObject(
            JSONObject obj, String key,
            String filePath)
            throws CharacterCorruptionException {
        return requireObject(
                obj, key, key, filePath);
    }

    /**
     * Require a JSONObject field with a custom
     * dotted field path for error messages.
     */
    private JSONObject requireObject(
            JSONObject obj, String key,
            String fieldPath, String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getJSONObject(key);
        } catch (JSONException e) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected JSON object", e);
        }
    }

    /**
     * Require a JSONArray field from a parent
     * object.
     */
    private JSONArray requireArray(
            JSONObject obj, String key,
            String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    key,
                    "Required field not found");
        }
        try {
            return obj.getJSONArray(key);
        } catch (JSONException e) {
            throw new CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    key,
                    "Expected JSON array", e);
        }
    }
}