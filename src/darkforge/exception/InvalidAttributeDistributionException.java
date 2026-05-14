package darkforge.exception;

import darkforge.model.Attribute;
import java.util.EnumMap;
import java.util.Map;
import java.util.Collections;

public class InvalidAttributeDistributionException
        extends DarkForgeException {

    private final int expectedTotal;
    private final int actualTotal;
    private final Map<Attribute, String> violations;

    public InvalidAttributeDistributionException(
            int expectedTotal, int actualTotal,
            Map<Attribute, String> violations) {
        super(
                buildUserMessage(expectedTotal, actualTotal,
                        violations),
                buildTechnicalDetail(expectedTotal,
                        actualTotal, violations)
        );
        this.expectedTotal = expectedTotal;
        this.actualTotal = actualTotal;
        this.violations = Collections.unmodifiableMap(
                new EnumMap<>(violations));
    }

    public int getExpectedTotal() { return expectedTotal; }
    public int getActualTotal() { return actualTotal; }
    public Map<Attribute, String> getViolations() {
        return violations;
    }

    private static String buildUserMessage(
            int expected, int actual,
            Map<Attribute, String> violations) {
        if (actual != expected) {
            return String.format(
                    "Attribute total must be %d, but you "
                            + "distributed %d points. Please "
                            + "redistribute.", expected, actual);
        }
        // Find first violation for user message
        Map.Entry<Attribute, String> first =
                violations.entrySet().iterator().next();
        return String.format("%s: %s",
                first.getKey().getDisplayName(),
                first.getValue());
    }

    private static String buildTechnicalDetail(
            int expected, int actual,
            Map<Attribute, String> violations) {
        return String.format(
                "AttributeDistributor.validate() failed: "
                        + "total=%d (expected=%d), violations=%s",
                actual, expected, violations);
    }
}