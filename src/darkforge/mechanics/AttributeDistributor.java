package darkforge.mechanics;

import darkforge.exception
        .InvalidAttributeDistributionException;
import darkforge.model.Attribute;
import java.util.*;

/**
 * Validates and distributes attribute points for
 * Explorer creation under Coriolis constraints.
 */
public class AttributeDistributor {
    public static final int REQUIRED_TOTAL = 24;
    public static final int MIN_VALUE = 2;
    public static final int MAX_KEY = 6;
    public static final int MAX_NON_KEY = 5;

    /**
     * Validate an attribute distribution against
     * Coriolis constraints.
     *
     * @param attributes   the proposed distribution
     * @param keyAttribute the profession's key
     *                     attribute (max 6)
     * @throws InvalidAttributeDistributionException
     *         if any constraint is violated
     */
    public static void validate(
            EnumMap<Attribute, Integer> attributes,
            Attribute keyAttribute)
            throws InvalidAttributeDistributionException {
        Map<Attribute, String> violations =
                new EnumMap<>(Attribute.class);
        int total = attributes.values().stream()
                .mapToInt(Integer::intValue).sum();

        for (Map.Entry<Attribute, Integer> entry :
                attributes.entrySet()) {
            int val = entry.getValue();
            Attribute attr = entry.getKey();
            if (val < MIN_VALUE) {
                violations.put(attr, String.format(
                        "must be at least %d, got %d",
                        MIN_VALUE, val));
            } else if (attr == keyAttribute
                    && val > MAX_KEY) {
                violations.put(attr, String.format(
                        "key attribute max is %d, got %d",
                        MAX_KEY, val));
            } else if (attr != keyAttribute
                    && val > MAX_NON_KEY) {
                violations.put(attr, String.format(
                        "non-key max is %d, got %d",
                        MAX_NON_KEY, val));
            }
        }

        if (total != REQUIRED_TOTAL
                || !violations.isEmpty()) {
            throw new
                    InvalidAttributeDistributionException(
                    REQUIRED_TOTAL, total, violations);
        }
    }

    /**
     * Distribute points randomly across attributes.
     * Guarantees total = 24 and all values in
     * \[2, max\] range.
     */
    public static EnumMap<Attribute, Integer>
    distribute(Attribute keyAttribute,
               Random rng) {
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        for (Attribute a : Attribute.values()) {
            attrs.put(a, MIN_VALUE);
        }
        int remaining = REQUIRED_TOTAL
                - (MIN_VALUE * Attribute.values().length);
        List<Attribute> pool = new ArrayList<>(
                Arrays.asList(Attribute.values()));
        while (remaining > 0) {
            Collections.shuffle(pool, rng);
            for (Attribute a : pool) {
                if (remaining <= 0) break;
                int max = (a == keyAttribute)
                        ? MAX_KEY : MAX_NON_KEY;
                if (attrs.get(a) < max) {
                    attrs.put(a, attrs.get(a) + 1);
                    remaining--;
                }
            }
        }
        return attrs;
    }
}