package darkforge.persistence;

import darkforge.crew.*;
import darkforge.model.Equipment;
import darkforge.model.Explorer;
import org.json.*;

import java.util.Map;

/**
 * Serializes a complete Crew to JSON v3.0 format.
 * Delegates Explorer serialization to ExplorerSerializer.
 */
public class CrewSerializer {

    public static final String FORMAT_VERSION =
            "3.0";

    private final ExplorerSerializer
            explorerSerializer;

    public CrewSerializer() {
        this.explorerSerializer =
                new ExplorerSerializer();
    }

    // =========================================
    // Public API
    // =========================================

    public String serialize(Crew crew) {
        JSONObject root = new JSONObject();
        root.put("version", FORMAT_VERSION);
        root.put("crewName", crew.getName());
        root.put("totalSupply",
                crew.getTotalSupply());
        root.put("crewPoints",
                crew.getCrewPoints());
        root.put("learnedManeuvers",
                new JSONArray(
                        crew.getLearnedManeuvers()));
        root.put("members",
                serializeMembers(crew));
        root.put("roleAssignments",
                serializeRoleAssignments(crew));
        root.put("bird",
                serializeBird(crew.getBird()));
        root.put("shuttle",
                crew.getShuttle() != null
                        ? serializeVehicle(
                        crew.getShuttle())
                        : JSONObject.NULL);
        root.put("rover",
                crew.getRover() != null
                        ? serializeVehicle(
                        crew.getRover())
                        : JSONObject.NULL);
        return root.toString(4);
    }

    // =========================================
    // Members (delegates to ExplorerSerializer)
    // =========================================

    private JSONArray serializeMembers(
            Crew crew) {
        JSONArray arr = new JSONArray();
        for (Explorer member
                : crew.getMembers()) {
            String json =
                    explorerSerializer
                            .serialize(member);
            arr.put(new JSONObject(json));
        }
        return arr;
    }

    // =========================================
    // Role assignments
    // =========================================

    private JSONObject
    serializeRoleAssignments(
            Crew crew) {
        JSONObject roles = new JSONObject();
        for (Map.Entry<CrewRole, Explorer> entry
                : crew.getRoleAssignments()
                .entrySet()) {
            roles.put(
                    entry.getKey().name(),
                    entry.getValue().getName());
        }
        return roles;
    }

    // =========================================
    // Bird
    // =========================================

    private Object serializeBird(Bird bird) {
        if (bird == null)
            return JSONObject.NULL;

        JSONObject bObj = new JSONObject();
        bObj.put("name", bird.getName());
        bObj.put("type",
                bird.getType().name());
        bObj.put("maxHealth",
                bird.getMaxHealth());
        bObj.put("currentHealth",
                bird.getCurrentHealth());
        bObj.put("maxEnergy",
                bird.getMaxEnergy());
        bObj.put("currentEnergy",
                bird.getCurrentEnergy());
        bObj.put("color", bird.getColor());
        bObj.put("bodyFeature",
                bird.getBodyFeature());
        bObj.put("personality",
                bird.getPersonality());
        bObj.put("powers",
                serializePowers(bird));
        return bObj;
    }

    private JSONArray serializePowers(
            Bird bird) {
        JSONArray arr = new JSONArray();
        for (GarudaPower power
                : bird.getPowers()) {
            JSONObject pObj = new JSONObject();
            pObj.put("name", power.getName());
            pObj.put("description",
                    power.getDescription());
            pObj.put("effect",
                    power.getEffect());
            pObj.put("energyCost",
                    power.getEnergyCost());
            pObj.put("isBasic",
                    power.isBasic());

            JSONArray types = new JSONArray();
            for (BirdType type
                    : power.getNativeTypes()) {
                types.put(type.name());
            }
            pObj.put("nativeTypes", types);
            arr.put(pObj);
        }
        return arr;
    }

    // =========================================
    // Vehicles
    // =========================================

    private JSONObject serializeVehicle(
            Vehicle vehicle) {
        JSONObject vObj = new JSONObject();
        vObj.put("name", vehicle.getName());
        vObj.put("type",
                vehicle.getType().name());
        vObj.put("paintColor",
                vehicle.getPaintColor());
        vObj.put("currentHull",
                vehicle.getCurrentHull());
        vObj.put("upgrades",
                serializeUpgrades(vehicle));
        return vObj;
    }

    private JSONArray serializeUpgrades(
            Vehicle vehicle) {
        JSONArray arr = new JSONArray();
        for (Equipment eq
                : vehicle.getUpgrades()) {
            JSONObject eObj = new JSONObject();
            eObj.put("name", eq.getName());
            eObj.put("description",
                    eq.getDescription());
            eObj.put("weight",
                    eq.getWeight().name());
            eObj.put("gearBonus",
                    eq.getGearBonus());
            eObj.put("isWeapon",
                    eq.isWeapon());
            arr.put(eObj);
        }
        return arr;
    }
}