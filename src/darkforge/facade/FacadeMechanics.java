package darkforge.facade;

import darkforge.exception
        .InvalidAttributeDistributionException;
import darkforge.mechanics.*;
import darkforge.model.Attribute;
import java.util.*;

/**
 * Façade for the darkforge.mechanics package.
 * Wraps dice rolling, attribute validation,
 * and D66 table parsing behind simple methods.
 */
public class FacadeMechanics {
    private static final FacadeMechanics INSTANCE =
            new FacadeMechanics();
    private final Random rng = new Random();

    private FacadeMechanics() {}

    public static FacadeMechanics
    getTheInstance() {
        return INSTANCE;
    }

    /**
     * Create and roll a dice pool with base dice
     * and gear dice. Returns an int[] of
     * individual die results.
     *
     * @param baseDice number of base dice (>= 0)
     * @param gearDice number of gear dice (>= 0)
     * @return int[] of individual die results
     * @throws IllegalArgumentException if either
     *         argument is negative
     */
    public int[] rollDicePool(int baseDice,
                              int gearDice) {
        if (baseDice < 0) {
            throw new IllegalArgumentException(
                    "Base dice cannot be negative: "
                            + baseDice);
        }
        if (gearDice < 0) {
            throw new IllegalArgumentException(
                    "Gear dice cannot be negative: "
                            + gearDice);
        }
        int total = baseDice + gearDice;
        DicePool pool =
                new DicePool(baseDice, gearDice);
        pool.roll();
        int[] results = new int[total];
        for (int i = 0; i < total; i++) {
            results[i] = rng.nextInt(6) + 1;
        }
        return results;
    }

    /**
     * Roll a D66 value (two D6 dice combined:
     * tens digit * 10 + ones digit).
     *
     * @return int in valid D66 range (11-66,
     *         both digits 1-6)
     */
    public int rollD66() {
        int tens = rng.nextInt(6) + 1;
        int ones = rng.nextInt(6) + 1;
        return tens * 10 + ones;
    }

    /**
     * Roll on a D66 table and return the result.
     */
    public <T> T rollD66Table(
            D66Table<T> table) {
        table.roll();
        return table.getResult(
                table.getLastRollValue());
    }

    /**
     * Validate an attribute distribution against
     * Coriolis constraints.
     *
     * @throws InvalidAttributeDistributionException
     *         if any constraint is violated
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