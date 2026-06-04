package darkforge.persistence;

import darkforge.data.GameDataProvider;
import darkforge.data.ItemCatalog;
import darkforge.facade.FacadeDarkforge;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Benchmarks all three serialization
 * approaches on the full catalog,
 * measuring roundtrip time and output
 * file size.
 *
 * Exposes an instance run() method that
 * returns a BenchmarkResult so callers
 * (including the GUI Tools menu) can
 * consume the measurements
 * programmatically. main() is preserved
 * for the existing CLI workflow and
 * prints result.summary().
 */
public class SerializationBenchmark {

    private static final int ROUNDS = 1000;

    public static void main(String[] args)
            throws Exception {
        BenchmarkResult result =
                new SerializationBenchmark()
                        .run();
        System.out.println(
                result.summary());
    }

    /**
     * Runs all three serialization
     * benchmarks and returns the
     * aggregated result. Safe to call
     * from a worker thread — no Swing
     * components are touched.
     */
    public BenchmarkResult run()
            throws Exception {
        FacadeDarkforge.getTheInstance()
                .initialize();
        ItemCatalog catalog =
                GameDataProvider
                        .getTheInstance()
                        .getItemCatalog();
        Path dir = Path.of(
                System.getProperty(
                        "java.io.tmpdir"),
                "darkforge-bench");
        Files.createDirectories(dir);

        BenchmarkResult result =
                new BenchmarkResult(ROUNDS);
        benchmarkBinary(
                result, catalog, dir);
        benchmarkJson(
                result, catalog, dir);
        benchmarkGson(
                result, catalog, dir);
        return result;
    }

    private void benchmarkBinary(
            BenchmarkResult result,
            ItemCatalog catalog,
            Path dir) throws Exception {
        BinaryCatalogSerializer ser =
                new BinaryCatalogSerializer();
        Path file = dir.resolve(
                "catalog.bin");
        long start = System.nanoTime();
        for (int i = 0; i < ROUNDS; i++) {
            ser.serialize(catalog, file);
            ser.deserialize(file);
        }
        long elapsed =
                System.nanoTime() - start;
        long fileSize = Files.size(file);
        result.record(
                "Binary", elapsed, fileSize);
    }

    private void benchmarkJson(
            BenchmarkResult result,
            ItemCatalog catalog,
            Path dir) throws Exception {
        JsonCatalogSerializer ser =
                new JsonCatalogSerializer();
        Path file = dir.resolve(
                "catalog.json");
        long start = System.nanoTime();
        for (int i = 0; i < ROUNDS; i++) {
            ser.serializeToFile(
                    catalog, file);
            ser.deserializeFromFile(file);
        }
        long elapsed =
                System.nanoTime() - start;
        long fileSize = Files.size(file);
        result.record(
                "JSON", elapsed, fileSize);
    }

    private void benchmarkGson(
            BenchmarkResult result,
            ItemCatalog catalog,
            Path dir) throws Exception {
        GsonCatalogSerializer ser =
                new GsonCatalogSerializer();
        Path file = dir.resolve(
                "catalog-gson.json");
        long start = System.nanoTime();
        for (int i = 0; i < ROUNDS; i++) {
            ser.serializeToFile(
                    catalog, file);
            ser.deserializeFromFile(file);
        }
        long elapsed =
                System.nanoTime() - start;
        long fileSize = Files.size(file);
        result.record(
                "Gson", elapsed, fileSize);
    }
}