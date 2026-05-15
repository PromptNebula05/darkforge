package darkforge.persistence;

import darkforge.model.*;
import org.json.*;

/**
 * Converts a complete Explorer object into a
 * structured JSON string for file persistence.
 *
 * Canonical format (version 2.0):
 * - "version": format version for forward compat
 * - "profession": subclass simple name (String)
 * - "name": explorer name
 * - "specialty": {name, description, freeTalent}
 * - "origin": {location, faction, contact,
 *              freeTalent, d66Range}
 * - "attributes": {STRENGTH:n, AGILITY:n, ...}
 * - "talents": [{name, category, currentLevel,
 *               maxLevel, effect}, ...]
 * - "equipment": [{name, description, weight,
 *                  gearBonus}, ...]
 * - "personalDetails": {quirk, keepsake,
 *                       appearance}
 *
 * Enums are serialized as name() strings for
 * human readability and reorder-safety.
 * Talents use an array (not map) to preserve
 * insertion order and allow duplicate-name
 * talents at different levels.
 */
public class ExplorerSerializer {
    public static final String FORMAT_VERSION =
            "2.0";

    /**
     * Serialize an Explorer to a pretty-printed
     * JSON string.
     *
     * @param explorer the Explorer to serialize
     * @return JSON string with 4-space indentation
     */
    public String serialize(Explorer explorer) {
        JSONObject root = new JSONObject();
        root.put("version", FORMAT_VERSION);
        root.put("profession",
                explorer.getClass().getSimpleName());
        root.put("name", explorer.getName());
        root.put("specialty",
                serializeSpecialty(explorer));
        root.put("origin",
                serializeOrigin(explorer));
        root.put("attributes",
                serializeAttributes(explorer));
        root.put("talents",
                serializeTalents(explorer));
        root.put("equipment",
                serializeEquipment(explorer));
        root.put("personalDetails",
                serializePersonalDetails(explorer));
        return root.toString(4);
    }

    /**
     * Serialize all six attributes as enum-name
     * keys mapped to integer values.
     */
    private JSONObject serializeAttributes(
            Explorer explorer) {
        JSONObject attrs = new JSONObject();
        for (Attribute a : Attribute.values()) {
            attrs.put(a.name(),
                    explorer.getAttribute(a));
        }
        return attrs;
    }

    /**
     * Serialize talents as an ordered JSON array.
     * Each talent includes name, category (enum
     * name), currentLevel, maxLevel, and effect.
     */
    private JSONArray serializeTalents(
            Explorer explorer) {
        JSONArray arr = new JSONArray();
        for (Talent t : explorer.getTalents()) {
            JSONObject tObj = new JSONObject();
            tObj.put("name", t.getName());
            tObj.put("category",
                    t.getCategory().name());
            tObj.put("currentLevel",
                    t.getCurrentLevel());
            tObj.put("maxLevel",
                    t.getMaxLevel());
            tObj.put("effect", t.getEffect());
            arr.put(tObj);
        }
        return arr;
    }

    /**
     * Serialize equipment as a JSON array.
     * Each item includes name, description,
     * weight (enum name), and gearBonus.
     */
    private JSONArray serializeEquipment(
            Explorer explorer) {
        JSONArray arr = new JSONArray();
        for (Equipment e :
                explorer.getEquipment()) {
            JSONObject eObj = new JSONObject();
            eObj.put("name", e.getName());
            eObj.put("description",
                    e.getDescription());
            eObj.put("weight",
                    e.getWeight().name());
            eObj.put("gearBonus",
                    e.getGearBonus());
            arr.put(eObj);
        }
        return arr;
    }

    /**
     * Serialize the specialty as a JSON object
     * with name, description, and the name of
     * the free talent granted by the specialty.
     * Returns JSONObject.NULL if no specialty.
     */
    private Object serializeSpecialty(
            Explorer explorer) {
        Specialty spec = explorer.getSpecialty();
        if (spec == null) {
            return JSONObject.NULL;
        }
        JSONObject sObj = new JSONObject();
        sObj.put("name", spec.getName());
        sObj.put("description",
                spec.getDescription());
        sObj.put("freeTalent",
                spec.getFreeTalent() != null
                        ? spec.getFreeTalent().getName()
                        : JSONObject.NULL);
        return sObj;
    }

    /**
     * Serialize the origin as a JSON object.
     * Includes location, resolved faction,
     * resolved contact, free talent name, and
     * D66 range. Returns JSONObject.NULL if
     * no origin.
     */
    private Object serializeOrigin(
            Explorer explorer) {
        Origin origin = explorer.getOrigin();
        if (origin == null) {
            return JSONObject.NULL;
        }
        JSONObject oObj = new JSONObject();
        oObj.put("location",
                origin.getLocation());
        oObj.put("faction",
                explorer.getResolvedFaction() != null
                        ? explorer.getResolvedFaction()
                        : origin.getAssociatedFaction());
        oObj.put("contact",
                explorer.getResolvedContact() != null
                        ? explorer.getResolvedContact()
                        : JSONObject.NULL);
        oObj.put("freeTalent",
                origin.getFreeTalent() != null
                        ? origin.getFreeTalent().getName()
                        : JSONObject.NULL);
        JSONArray range = new JSONArray();
        range.put(origin.getD66RangeLow());
        range.put(origin.getD66RangeHigh());
        oObj.put("d66Range", range);
        return oObj;
    }

    /**
     * Serialize personal details (quirk, keepsake,
     * appearance) as a JSON object.
     */
    private JSONObject serializePersonalDetails(
            Explorer explorer) {
        JSONObject pd = new JSONObject();
        pd.put("quirk",
                explorer.getQuirk() != null
                        ? explorer.getQuirk()
                        : JSONObject.NULL);
        pd.put("keepsake",
                explorer.getKeepsake() != null
                        ? explorer.getKeepsake()
                        : JSONObject.NULL);
        pd.put("appearance",
                explorer.getAppearance() != null
                        ? explorer.getAppearance()
                        : JSONObject.NULL);
        return pd;
    }
}