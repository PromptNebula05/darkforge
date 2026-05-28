package darkforge.persistence;

import darkforge.data.ItemCatalog;
import java.io.IOException;
import java.nio.file.*;

/**
 * Benchmarks all three serialization approaches
 * on the full catalog, measuring roundtrip time
 * and output file size.
 */
public class SerializationBenchmark {

    private static final int ROUNDS = 1000;

    public static void main(String[] args)
            throws Exception {
        darkforge.facade.FacadeDarkforge
                .getTheInstance().initialize();
        ItemCatalog catalog =
                darkforge.data.GameDataProvider
                        .getTheInstance()
                        .getItemCatalog();

        Path dir = Path.of(
                System.getProperty(
                        "java.io.tmpdir"),
                "darkforge-bench");
        Files.createDirectories(dir);

        System.out.println(
                "=== Serialization Benchmark"
                        + " ===");
        System.out.printf(
                "Catalog size: %d items%n%n",
                catalog.size());

        benchmarkBinary(catalog, dir);
        benchmarkJson(catalog, dir);
        benchmarkGson(catalog, dir);
    }

    private static void benchmarkBinary(
            ItemCatalog catalog,
            Path dir) throws Exception {
        BinaryCatalogSerializer ser =
                new BinaryCatalogSerializer();
        Path file = dir.resolve(
                "catalog.bin");

        long start = System.nanoTime();
        for (int i = 0;
             i < ROUNDS; i++) {
            ser.serialize(catalog, file);
            ser.deserialize(file);
        }
        long elapsed =
                System.nanoTime() - start;
        long fileSize =
                Files.size(file);

        System.out.printf(
                "Binary:  %,d ms |"
                        + " %,d bytes%n",
                elapsed / 1_000_000,
                fileSize);
    }

    private static void benchmarkJson(
            ItemCatalog catalog,
            Path dir) throws Exception {
        JsonCatalogSerializer ser =
                new JsonCatalogSerializer();
        Path file = dir.resolve(
                "catalog.json");

        long start = System.nanoTime();
        for (int i = 0;
             i < ROUNDS; i++) {
            ser.serializeToFile(
                    catalog, file);
            ser.deserializeFromFile(file);
        }
        long elapsed =
                System.nanoTime() - start;
        long fileSize =
                Files.size(file);

        System.out.printf(
                "JSON:    %,d ms |"
                        + " %,d bytes%n",
                elapsed / 1_000_000,
                fileSize);
    }

    private static void benchmarkGson(
            ItemCatalog catalog,
            Path dir) throws Exception {
        GsonCatalogSerializer ser =
                new GsonCatalogSerializer();
        Path file = dir.resolve(
                "catalog-gson.json");

        long start = System.nanoTime();
        for (int i = 0;
             i < ROUNDS; i++) {
            ser.serializeToFile(
                    catalog, file);
            ser.deserializeFromFile(file);
        }
        long elapsed =
                System.nanoTime() - start;
        long fileSize =
                Files.size(file);

        System.out.printf(
                "Gson:    %,d ms |"
                        + " %,d bytes%n",
                elapsed / 1_000_000,
                fileSize);
    }
}