package darkforge.facade;

import darkforge.exception
        .InvalidAttributeDistributionException;
import darkforge.mechanics.*;
import darkforge.model.Attribute;
import java.util.*;

/**
 * Façade singleton for the darkforge.mechanics
 * package. Provides dice rolling, attribute
 * validation, and D66 table parsing.
 */
public class FacadeMechanics {
    private static final FacadeMechanics INSTANCE =
            new FacadeMechanics();

    private final Random rng;

    private FacadeMechanics() {
        this.rng = new Random();
    }

    public static FacadeMechanics getTheInstance() {
        return INSTANCE;
    }

    /**
     * Roll a dice pool with base dice and gear
     * dice.
     * @return array of individual die results
     */
    public int[] rollDicePool(int baseDice, int gearDice) {
        DicePool pool = new DicePool(baseDice, gearDice, rng);
        pool.roll(); // triggers the roll internally (returns int successes — not what we want)
        int[] baseResults = pool.getLastBaseResults();
        int[] gearResults = pool.getLastGearResults();
        int[] combined = new int[baseResults.length + gearResults.length];
        System.arraycopy(baseResults, 0, combined, 0, baseResults.length);
        System.arraycopy(gearResults, 0, combined, baseResults.length, gearResults.length);
        return combined;
    }

    /**
     * Roll a D66 value (both digits 1-6).
     */
    public int rollD66() {
        int tens = rng.nextInt(6) + 1;
        int ones = rng.nextInt(6) + 1;
        return tens * 10 + ones;
    }

    /**
     * Validate an attribute distribution against
     * Coriolis rules. Throws if invalid.
     */
    public void validateAttributes(
            EnumMap<Attribute, Integer> attributes,
            Attribute keyAttribute)
            throws
            InvalidAttributeDistributionException {
        AttributeDistributor.validate(
                attributes, keyAttribute);
    }

    /**
     * Parse a text-format D66 table into a map.
     */
    public Map<Integer, String> parseD66Table(
            String tableText) {
        return D66TableParser.parse(tableText);
    }
}