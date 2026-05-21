package darkforge.persistence;

import darkforge.crew.*;
import darkforge.exception
        .CharacterCorruptionException;
import darkforge.exception
        .CharacterCorruptionException
        .CorruptionType;
import darkforge.model.*;
import org.json.*;

import java.util.*;

/**
 * Reconstructs a Crew from a v3.0 JSON string
 * produced by CrewSerializer.
 *
 * Delegates Explorer reconstruction to
 * ExplorerDeserializer. Validates structure,
 * field values, role completeness, and member
 * references — wrapping all errors in
 * CharacterCorruptionException.
 */
public class CrewDeserializer {

    private final ExplorerDeserializer
            explorerDeserializer;

    public CrewDeserializer() {
        this.explorerDeserializer =
                new ExplorerDeserializer();
    }

    // =========================================
    // Public API
    // =========================================

    /**
     * Deserialize a JSON string into a fully
     * validated Crew.
     *
     * @param json the JSON string to parse
     * @param filePath source file path (used
     *     in error messages)
     * @return a reconstructed Crew
     * @throws CharacterCorruptionException if
     *     the JSON is malformed, missing fields,
     *     contains invalid values, or has a
     *     version mismatch
     */
    public Crew deserialize(String json,
                            String filePath)
            throws CharacterCorruptionException {
        JSONObject root =
                parseRoot(json, filePath);

        // --- Version check ---
        String version = requireString(
                root, "version", filePath);
        if (!version.equals(
                CrewSerializer
                        .FORMAT_VERSION)) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType
                            .VERSION_MISMATCH,
                    "version",
                    "Expected "
                            + CrewSerializer
                            .FORMAT_VERSION
                            + ", got " + version);
        }

        // --- Crew name ---
        String crewName = requireString(
                root, "crewName", filePath);
        Crew crew = new Crew(crewName);

        // --- Supply ---
        int supply = requireInt(
                root, "totalSupply", filePath);
        if (supply < 0) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    "totalSupply",
                    "Supply cannot be negative: "
                            + supply);
        }
        crew.addSupply(supply);

        // --- Learned maneuvers (cost 0) ---
        JSONArray maneuversArr = requireArray(
                root, "learnedManeuvers",
                filePath);
        for (int i = 0;
             i < maneuversArr.length();
             i++) {
            String m;
            try {
                m = maneuversArr.getString(i);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        "learnedManeuvers["
                                + i + "]",
                        "Expected string", e);
            }
            crew.learnManeuver(m, 0);
        }

        // --- Crew points (after maneuvers) ---
        int cp = requireInt(
                root, "crewPoints", filePath);
        if (cp < 0) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    "crewPoints",
                    "CP cannot be negative: "
                            + cp);
        }
        crew.addCrewPoints(cp);

        // --- Members ---
        JSONArray membersArr = requireArray(
                root, "members", filePath);
        Map<String, Explorer> membersByName =
                deserializeMembers(
                        membersArr, filePath);
        for (Explorer member :
                membersByName.values()) {
            crew.addMember(member);
        }

        // --- Bird ---
        if (root.has("bird")
                && !root.isNull("bird")) {
            crew.setBird(deserializeBird(
                    requireObject(root,
                            "bird", filePath),
                    filePath));
        }

        // --- Shuttle ---
        if (root.has("shuttle")
                && !root.isNull("shuttle")) {
            crew.setShuttle(
                    deserializeVehicle(
                            requireObject(root,
                                    "shuttle", filePath),
                            "shuttle", filePath));
        }

        // --- Rover ---
        if (root.has("rover")
                && !root.isNull("rover")) {
            crew.setRover(
                    deserializeVehicle(
                            requireObject(root,
                                    "rover", filePath),
                            "rover", filePath));
        }

        // --- Role assignments (last) ---
        JSONObject rolesObj = requireObject(
                root, "roleAssignments",
                filePath);
        deserializeRoleAssignments(
                rolesObj, crew,
                membersByName, filePath);

        return crew;
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
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType
                            .MALFORMED_FORMAT,
                    "root",
                    "Not valid JSON", e);
        }
    }

    // =========================================
    // Members
    // =========================================

    private Map<String, Explorer>
    deserializeMembers(
            JSONArray membersArr,
            String filePath)
            throws CharacterCorruptionException {
        Map<String, Explorer> byName =
                new LinkedHashMap<>();
        for (int i = 0;
             i < membersArr.length();
             i++) {
            String prefix =
                    "members[" + i + "]";
            JSONObject mObj;
            try {
                mObj = membersArr
                        .getJSONObject(i);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix,
                        "Expected JSON object", e);
            }
            Explorer explorer =
                    explorerDeserializer
                            .deserialize(
                                    mObj.toString(),
                                    filePath);
            byName.put(
                    explorer.getName(), explorer);
        }
        return byName;
    }

    // =========================================
    // Role assignments
    // =========================================

    private void deserializeRoleAssignments(
            JSONObject rolesObj, Crew crew,
            Map<String, Explorer> membersByName,
            String filePath)
            throws CharacterCorruptionException {
        for (String roleKey :
                rolesObj.keySet()) {
            String fieldPath =
                    "roleAssignments." + roleKey;

            CrewRole role;
            try {
                role =
                        CrewRole.valueOf(roleKey);
            } catch (
                    IllegalArgumentException
                            e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        fieldPath,
                        "Unknown role: "
                                + roleKey, e);
            }

            String memberName;
            try {
                memberName =
                        rolesObj.getString(
                                roleKey);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        fieldPath,
                        "Expected string", e);
            }

            Explorer member =
                    membersByName.get(memberName);
            if (member == null) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        fieldPath,
                        "Member not found: "
                                + memberName);
            }
            crew.assignRole(role, member);
        }

        if (!crew
                .areAllMandatoryRolesFilled()) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    "roleAssignments",
                    "Not all mandatory roles "
                            + "are assigned");
        }
    }

    // =========================================
    // Bird
    // =========================================

    private Bird deserializeBird(
            JSONObject birdObj,
            String filePath)
            throws CharacterCorruptionException {
        String name = requireString(
                birdObj, "name",
                "bird.name", filePath);
        String typeStr = requireString(
                birdObj, "type",
                "bird.type", filePath);

        BirdType type;
        try {
            type = BirdType.valueOf(typeStr);
        } catch (
                IllegalArgumentException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    "bird.type",
                    "Unknown bird type: "
                            + typeStr, e);
        }

        String color = requireString(
                birdObj, "color",
                "bird.color", filePath);
        String bodyFeature = requireString(
                birdObj, "bodyFeature",
                "bird.bodyFeature", filePath);
        String personality = requireString(
                birdObj, "personality",
                "bird.personality", filePath);

        Bird bird = new Bird(
                name, type, color,
                bodyFeature, personality);

        // Restore upgraded stats
        int maxHealth = requireInt(
                birdObj, "maxHealth",
                "bird.maxHealth", filePath);
        int maxEnergy = requireInt(
                birdObj, "maxEnergy",
                "bird.maxEnergy", filePath);

        int healthUpgrades =
                maxHealth - bird.getMaxHealth();
        for (int i = 0;
             i < healthUpgrades; i++) {
            bird.upgradeHealth();
        }
        int energyUpgrades =
                maxEnergy - bird.getMaxEnergy();
        for (int i = 0;
             i < energyUpgrades; i++) {
            bird.upgradeEnergy();
        }

        // Restore current state
        int currentHealth = requireInt(
                birdObj, "currentHealth",
                "bird.currentHealth", filePath);
        int currentEnergy = requireInt(
                birdObj, "currentEnergy",
                "bird.currentEnergy", filePath);

        int hpDamage =
                maxHealth - currentHealth;
        if (hpDamage > 0) {
            bird.takeDamage(hpDamage);
        }
        int epSpent =
                maxEnergy - currentEnergy;
        if (epSpent > 0) {
            bird.spendEnergy(epSpent);
        }

        // Restore learned powers
        JSONArray powersArr = requireArray(
                birdObj, "powers",
                "bird.powers", filePath);
        for (int i = 0;
             i < powersArr.length(); i++) {
            JSONObject pObj;
            try {
                pObj = powersArr
                        .getJSONObject(i);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        "bird.powers[" + i + "]",
                        "Expected JSON object",
                        e);
            }
            String pName;
            try {
                pName =
                        pObj.getString("name");
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        "bird.powers[" + i
                                + "].name",
                        "Expected string", e);
            }
            if (!bird.hasPower(pName)) {
                bird.learnPower(
                        deserializePower(
                                pObj, i, filePath));
            }
        }

        return bird;
    }

    private GarudaPower deserializePower(
            JSONObject pObj, int index,
            String filePath)
            throws CharacterCorruptionException {
        String prefix =
                "bird.powers[" + index + "]";
        String name = requireString(
                pObj, "name",
                prefix + ".name", filePath);
        String description = requireString(
                pObj, "description",
                prefix + ".description",
                filePath);
        String effect = requireString(
                pObj, "effect",
                prefix + ".effect", filePath);
        int energyCost = requireInt(
                pObj, "energyCost",
                prefix + ".energyCost",
                filePath);
        boolean isBasic = requireBoolean(
                pObj, "isBasic",
                prefix + ".isBasic", filePath);

        Set<BirdType> nativeTypes =
                EnumSet.noneOf(BirdType.class);
        if (pObj.has("nativeTypes")) {
            JSONArray ntArr;
            try {
                ntArr = pObj.getJSONArray(
                        "nativeTypes");
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        prefix + ".nativeTypes",
                        "Expected array", e);
            }
            for (int i = 0;
                 i < ntArr.length(); i++) {
                try {
                    nativeTypes.add(
                            BirdType.valueOf(
                                    ntArr.getString(
                                            i)));
                } catch (
                        IllegalArgumentException
                                e) {
                    throw new
                            CharacterCorruptionException(
                            filePath,
                            CorruptionType
                                    .INVALID_VALUE,
                            prefix
                                    + ".nativeTypes["
                                    + i + "]",
                            "Unknown bird type",
                            e);
                }
            }
        }

        return new GarudaPower(
                name, description, effect,
                isBasic, nativeTypes, energyCost);
    }

    // =========================================
    // Vehicles
    // =========================================

    private Vehicle deserializeVehicle(
            JSONObject vObj,
            String vehicleField,
            String filePath)
            throws CharacterCorruptionException {
        String name = requireString(
                vObj, "name",
                vehicleField + ".name", filePath);
        String typeStr = requireString(
                vObj, "type",
                vehicleField + ".type", filePath);

        VehicleType type;
        try {
            type =
                    VehicleType.valueOf(typeStr);
        } catch (
                IllegalArgumentException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    vehicleField + ".type",
                    "Unknown vehicle type: "
                            + typeStr, e);
        }

        String paintColor = requireString(
                vObj, "paintColor",
                vehicleField + ".paintColor",
                filePath);

        Vehicle vehicle = new Vehicle(
                name, type, paintColor);

        // Restore hull damage
        int currentHull = requireInt(
                vObj, "currentHull",
                vehicleField + ".currentHull",
                filePath);
        int hullDamage =
                vehicle.getMaxHull()
                        - currentHull;
        if (hullDamage > 0) {
            vehicle.takeDamage(hullDamage);
        }

        // Restore upgrades
        JSONArray upgradesArr = requireArray(
                vObj, "upgrades",
                vehicleField + ".upgrades",
                filePath);
        for (int i = 0;
             i < upgradesArr.length();
             i++) {
            String uPrefix = vehicleField
                    + ".upgrades[" + i + "]";
            JSONObject uObj;
            try {
                uObj = upgradesArr
                        .getJSONObject(i);
            } catch (JSONException e) {
                throw new
                        CharacterCorruptionException(
                        filePath,
                        CorruptionType
                                .INVALID_VALUE,
                        uPrefix,
                        "Expected JSON object",
                        e);
            }
            vehicle.installUpgrade(
                    deserializeEquipment(
                            uObj, uPrefix, filePath));
        }

        return vehicle;
    }

    private Equipment deserializeEquipment(
            JSONObject eObj, String prefix,
            String filePath)
            throws CharacterCorruptionException {
        String name = requireString(
                eObj, "name",
                prefix + ".name", filePath);
        String description = requireString(
                eObj, "description",
                prefix + ".description",
                filePath);
        String weightStr = requireString(
                eObj, "weight",
                prefix + ".weight", filePath);

        EquipmentWeight weight;
        try {
            weight =
                    EquipmentWeight.valueOf(
                            weightStr);
        } catch (
                IllegalArgumentException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    prefix + ".weight",
                    "Unknown weight: "
                            + weightStr, e);
        }

        int gearBonus = requireInt(
                eObj, "gearBonus",
                prefix + ".gearBonus", filePath);
        boolean isWeapon =
                eObj.has("isWeapon")
                        && eObj.getBoolean(
                        "isWeapon");

        return new Equipment(
                name, description, weight,
                gearBonus, isWeapon);
    }

    // =========================================
    // Require helpers
    // =========================================

    private String requireString(
            JSONObject obj, String key,
            String filePath)
            throws CharacterCorruptionException {
        return requireString(
                obj, key, key, filePath);
    }

    private String requireString(
            JSONObject obj, String key,
            String fieldPath, String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getString(key);
        } catch (JSONException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected string value", e);
        }
    }

    private int requireInt(
            JSONObject obj, String key,
            String filePath)
            throws CharacterCorruptionException {
        return requireInt(
                obj, key, key, filePath);
    }

    private int requireInt(
            JSONObject obj, String key,
            String fieldPath, String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getInt(key);
        } catch (JSONException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected integer value", e);
        }
    }

    private boolean requireBoolean(
            JSONObject obj, String key,
            String fieldPath, String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getBoolean(key);
        } catch (JSONException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected boolean value", e);
        }
    }

    private JSONObject requireObject(
            JSONObject obj, String key,
            String filePath)
            throws CharacterCorruptionException {
        return requireObject(
                obj, key, key, filePath);
    }

    private JSONObject requireObject(
            JSONObject obj, String key,
            String fieldPath, String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getJSONObject(key);
        } catch (JSONException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected JSON object", e);
        }
    }

    private JSONArray requireArray(
            JSONObject obj, String key,
            String filePath)
            throws CharacterCorruptionException {
        return requireArray(
                obj, key, key, filePath);
    }

    private JSONArray requireArray(
            JSONObject obj, String key,
            String fieldPath, String filePath)
            throws CharacterCorruptionException {
        if (!obj.has(key)) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.MISSING_FIELD,
                    fieldPath,
                    "Required field not found");
        }
        try {
            return obj.getJSONArray(key);
        } catch (JSONException e) {
            throw new
                    CharacterCorruptionException(
                    filePath,
                    CorruptionType.INVALID_VALUE,
                    fieldPath,
                    "Expected JSON array", e);
        }
    }
}