package darkforge.persistence;

import darkforge.data.ItemCatalog;
import darkforge.model.Item;
import java.io.*;
import java.nio.file.Path;
import java.util.List;

/**
 * Serializes and deserializes an ItemCatalog
 * using Java's ObjectOutputStream and
 * ObjectInputStream. Relies on Serializable
 * on the Item hierarchy.
 */
public class BinaryCatalogSerializer {

    // =========================================
    // Serialize
    // =========================================

    public void serialize(
            ItemCatalog catalog,
            Path outputPath)
            throws IOException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(
                             new BufferedOutputStream(
                                     new FileOutputStream(
                                             outputPath
                                                     .toFile())))) {
            List<Item> items =
                    catalog.getAll();
            oos.writeInt(items.size());
            for (Item item : items) {
                oos.writeObject(item);
            }
        }
    }

    // =========================================
    // Deserialize
    // =========================================

    public ItemCatalog deserialize(
            Path inputPath)
            throws IOException,
            ClassNotFoundException {
        try (ObjectInputStream ois =
                     new ObjectInputStream(
                             new BufferedInputStream(
                                     new FileInputStream(
                                             inputPath
                                                     .toFile())))) {
            int count = ois.readInt();
            List<Item> items =
                    new java.util.ArrayList<>(
                            count);
            for (int i = 0;
                 i < count; i++) {
                items.add(
                        (Item) ois
                                .readObject());
            }
            return new ItemCatalog(items);
        }
    }
}