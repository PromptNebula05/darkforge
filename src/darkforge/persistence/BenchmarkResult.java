package darkforge.persistence;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable-ish (append-only) result of one
 * SerializationBenchmark run. Captures the
 * per-serializer measurements the benchmark
 * already produces (elapsed nanos and
 * serialized size in bytes), so the
 * GUI can render them in a JOptionPane.
 *
 * Insertion order is preserved so the dialog
 * reads in the same order as the CLI output.
 */
public final class BenchmarkResult {

    private final Map<String, Long> nanos =
            new LinkedHashMap<>();
    private final Map<String, Long> bytes =
            new LinkedHashMap<>();
    private final int sampleSize;

    public BenchmarkResult(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    public void record(String serializer,
                       long elapsedNanos,
                       long serializedBytes) {
        nanos.put(serializer, elapsedNanos);
        bytes.put(serializer,
                serializedBytes);
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public Map<String, Long> getNanos() {
        return nanos;
    }

    public Map<String, Long> getBytes() {
        return bytes;
    }

    /**
     * Human-readable multi-line summary
     * suitable for a JOptionPane dialog or
     * a System.out.println in main().
     */
    public String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append(
                        "Serialization benchmark (")
                .append(sampleSize)
                .append(" samples)\n\n");
        for (String name : nanos.keySet()) {
            long ns = nanos.get(name);
            long sz = bytes.getOrDefault(
                    name, -1L);
            sb.append(String.format(
                    "  %-20s %,12d ns",
                    name, ns));
            if (sz >= 0) {
                sb.append(String.format(
                        "   %,10d bytes", sz));
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}