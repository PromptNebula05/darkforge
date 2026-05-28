package darkforge.persistence;

import darkforge.data.ItemCatalog;
import darkforge.model.*;
import darkforge.crew.VehicleType;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Serializes and deserializes an ItemCatalog
 * to human-readable JSON using org.json.
 * Uses an "itemType" discriminator field for
 * polymorphic deserialization of the Item
 * hierarchy.
 */
public class JsonCatalogSerializer {

    public static final String
            FORMAT_VERSION = "4.0";

    // =========================================
    // Serialize
    // =========================================

    public String serialize(
            ItemCatalog catalog) {
        JSONObject root = new JSONObject();
        root.put("version",
                FORMAT_VERSION);
        JSONArray arr = new JSONArray();
        for (Item item
                : catalog.getAll()) {
            arr.put(
                    serializeItem(item));
        }
        root.put("items", arr);
        return root.toString(4);
    }

    public void serializeToFile(
            ItemCatalog catalog,
            Path outputPath)
            throws IOException {
        Files.writeString(outputPath,
                serialize(catalog),
                StandardCharsets.UTF_8);
    }

    // =========================================
    // Deserialize
    // =========================================

    public ItemCatalog deserialize(
            String json) {
        JSONObject root =
                new JSONObject(json);
        JSONArray arr =
                root.getJSONArray("items");
        List<Item> items =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            items.add(deserializeItem(
                    arr.getJSONObject(i)));
        }
        return new ItemCatalog(items);
    }

    public ItemCatalog deserializeFromFile(
            Path inputPath)
            throws IOException {
        String json =
                Files.readString(inputPath,
                        StandardCharsets.UTF_8);
        return deserialize(json);
    }

    // =========================================
    // Item serialization
    // =========================================

    private JSONObject serializeItem(
            Item item) {
        JSONObject obj = new JSONObject();
        // Common fields
        obj.put("itemType",
                item.getItemType());
        obj.put("name",
                item.getName());
        obj.put("description",
                item.getDescription());
        obj.put("weight",
                item.getWeight());
        obj.put("cost",
                item.getCost());
        obj.put("category",
                item.getCategory());
        obj.put("techLevel",
                item.getTechLevel().getCode());
        obj.put("restricted",
                item.isRestricted());

        // Type-specific fields
        switch (item) {
            case Weapon w -> {
                obj.put("damage",
                        w.getDamage());
                obj.put("crit",
                        w.getCritThreshold());
                obj.put("grip",
                        w.getGrip().getCode());
                obj.put("range",
                        w.getRange());
                obj.put("weaponType",
                        w.getWeaponType().name());
                obj.put("gearBonus",
                        w.getGearBonus());
                obj.put("features",
                        new JSONArray(
                                w.getFeatures()));
            }
            case Armor a -> {
                obj.put("armorRating",
                        a.getArmorRating());
                obj.put("blightProtection",
                        a.getBlightProtection());
                obj.put("extras",
                        a.getExtras());
                obj.put("gearBonus",
                        a.getGearBonus());
                obj.put("features",
                        new JSONArray(
                                a.getFeatures()));
            }
            case VehicleModule vm -> {
                obj.put("slotCost",
                        vm.getSlotCost());
                obj.put("cpCost",
                        vm.getCpCost());
                obj.put("moduleType",
                        vm.getModuleType());
                obj.put("effect",
                        vm.getEffect());
                obj.put("shuttleUpgrade",
                        vm.isShuttleUpgrade());
            }
            case CargoItem ci -> {
                obj.put("supplyPoints",
                        ci.getSupplyPoints());
                obj.put("cargoType",
                        ci.getCargoType());
            }
            case CharacterItem chi ->
                    obj.put("gearBonus",
                            chi.getGearBonus());
            default ->
                    throw new
                            IllegalArgumentException(
                            "Unknown item type: "
                                    + item.getClass()
                                    .getName());
        }
        return obj;
    }

    // =========================================
    // Item deserialization
    // =========================================

    private Item deserializeItem(
            JSONObject obj) {
        String type =
                obj.getString("itemType");
        return switch (type) {
            case "weapon" ->
                    deserializeWeapon(obj);
            case "armor" ->
                    deserializeArmor(obj);
            case "module" ->
                    deserializeModule(obj);
            case "cargo" ->
                    deserializeCargo(obj);
            case "equipment" ->
                    deserializeEquipment(obj);
            default ->
                    throw new
                            IllegalArgumentException(
                            "Unknown item type: "
                                    + type);
        };
    }

    private Weapon deserializeWeapon(
            JSONObject obj) {
        return new Weapon(
                obj.getString("name"),
                obj.getString("description"),
                obj.getDouble("weight"),
                obj.getInt("cost"),
                TechLevel.fromCode(
                        obj.getString("techLevel")),
                obj.getBoolean("restricted"),
                EquipmentWeight.fromWeight(
                        obj.getDouble("weight")),
                obj.getInt("gearBonus"),
                obj.getInt("damage"),
                obj.getInt("crit"),
                Grip.fromCode(
                        obj.getString("grip")),
                obj.getString("range"),
                WeaponType.valueOf(
                        obj.getString(
                                "weaponType")),
                jsonArrayToStringList(
                        obj.getJSONArray(
                                "features")));
    }

    private Armor deserializeArmor(
            JSONObject obj) {
        return new Armor(
                obj.getString("name"),
                obj.getString("description"),
                obj.getDouble("weight"),
                obj.getInt("cost"),
                obj.getString("category"),
                TechLevel.fromCode(
                        obj.getString("techLevel")),
                obj.getBoolean("restricted"),
                EquipmentWeight.fromWeight(
                        obj.getDouble("weight")),
                obj.optInt("gearBonus", 0),
                obj.getInt("armorRating"),
                obj.getInt(
                        "blightProtection"),
                obj.getInt("extras"),
                jsonArrayToStringList(
                        obj.getJSONArray(
                                "features")));
    }

    private VehicleModule
    deserializeModule(
            JSONObject obj) {
        return new VehicleModule(
                obj.getString("name"),
                obj.getString("description"),
                obj.getInt("slotCost"),
                obj.getInt("cpCost"),
                obj.getString("moduleType"),
                obj.getString("effect"),
                TechLevel.fromCode(
                        obj.getString("techLevel")),
                obj.getBoolean("restricted"),
                null,
                obj.getBoolean(
                        "shuttleUpgrade"));
    }

    private CargoItem deserializeCargo(
            JSONObject obj) {
        return new CargoItem(
                obj.getString("name"),
                obj.getString("description"),
                obj.getInt("supplyPoints"),
                obj.getString("cargoType"),
                obj.getInt("cost"),
                TechLevel.fromCode(
                        obj.getString(
                                "techLevel")));
    }

    private CharacterItem
    deserializeEquipment(
            JSONObject obj) {
        return new CharacterItem(
                obj.getString("name"),
                obj.getString("description"),
                obj.getDouble("weight"),
                obj.getInt("cost"),
                obj.getString("category"),
                TechLevel.fromCode(
                        obj.getString("techLevel")),
                obj.getBoolean("restricted"),
                obj.optInt("gearBonus", 0));
    }

    private List<String>
    jsonArrayToStringList(
            JSONArray arr) {
        List<String> list =
                new ArrayList<>();
        for (int i = 0;
             i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }
}