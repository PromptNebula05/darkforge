package darkforge.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import darkforge.data.ItemCatalog;
import darkforge.model.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

/**
 * Serializes and deserializes an ItemCatalog
 * using Gson with a custom TypeAdapter for
 * polymorphic Item hierarchy discrimination
 * via the "itemType" field.
 */
public class GsonCatalogSerializer {

    private final Gson gson;

    public GsonCatalogSerializer() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(
                        Item.class,
                        new ItemTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    // =========================================
    // Serialize
    // =========================================

    public String serialize(
            ItemCatalog catalog) {
        return gson.toJson(
                catalog.getAll());
    }

    public void serializeToFile(
            ItemCatalog catalog,
            Path outputPath)
            throws IOException {
        try (Writer writer =
                     Files.newBufferedWriter(
                             outputPath,
                             StandardCharsets
                                     .UTF_8)) {
            gson.toJson(
                    catalog.getAll(),
                    writer);
        }
    }

    // =========================================
    // Deserialize
    // =========================================

    public ItemCatalog deserialize(
            String json) {
        Type listType =
                new TypeToken<List<Item>>(){}
                        .getType();
        List<Item> items =
                gson.fromJson(json, listType);
        return new ItemCatalog(items);
    }

    public ItemCatalog deserializeFromFile(
            Path inputPath)
            throws IOException {
        try (Reader reader =
                     Files.newBufferedReader(
                             inputPath,
                             StandardCharsets
                                     .UTF_8)) {
            Type listType =
                    new TypeToken<
                            List<Item>>(){}
                            .getType();
            List<Item> items =
                    gson.fromJson(
                            reader, listType);
            return new ItemCatalog(items);
        }
    }

    // =========================================
    // Custom TypeAdapter for Item hierarchy
    // =========================================

    private static class ItemTypeAdapter
            implements JsonSerializer<Item>,
            JsonDeserializer<Item> {

        @Override
        public JsonElement serialize(
                Item src, Type typeOfSrc,
                JsonSerializationContext
                        context) {
            JsonObject obj =
                    context.serialize(src,
                                    src.getClass())
                            .getAsJsonObject();
            obj.addProperty(
                    "itemType",
                    src.getItemType());
            return obj;
        }

        @Override
        public Item deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext
                        context)
                throws
                JsonParseException {
            JsonObject obj =
                    json.getAsJsonObject();
            String type =
                    obj.get("itemType")
                            .getAsString();
            return switch (type) {
                case "weapon" ->
                        context.deserialize(
                                obj,
                                Weapon.class);
                case "armor" ->
                        context.deserialize(
                                obj,
                                Armor.class);
                case "module" ->
                        context.deserialize(
                                obj,
                                VehicleModule
                                        .class);
                case "cargo" ->
                        context.deserialize(
                                obj,
                                CargoItem.class);
                case "equipment" ->
                        context.deserialize(
                                obj,
                                CharacterItem
                                        .class);
                default ->
                        throw new
                                JsonParseException(
                                "Unknown type: "
                                        + type);
            };
        }
    }
}