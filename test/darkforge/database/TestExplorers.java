package darkforge.database;

import darkforge.creation.ExplorerFactory;
import darkforge.exception.InvalidProfessionException;
import darkforge.model.Attribute;
import darkforge.model.Explorer;

import java.util.EnumMap;

/**
 * Test helper: build a fully-attributed Explorer of a
 * given profession via the real ExplorerFactory.
 * Attribute order: STR, AGL, LOG, PER, INS, EMP.
 */
final class TestExplorers {

    private static final ExplorerFactory FACTORY =
            new ExplorerFactory();

    private TestExplorers() {
    }

    static Explorer of(String profession, String name,
                       int str, int agl, int log, int per,
                       int ins, int emp)
            throws InvalidProfessionException {
        Explorer e = FACTORY.createProfessionInstance(
                profession, name);
        EnumMap<Attribute, Integer> attrs =
                new EnumMap<>(Attribute.class);
        attrs.put(Attribute.STRENGTH, str);
        attrs.put(Attribute.AGILITY, agl);
        attrs.put(Attribute.LOGIC, log);
        attrs.put(Attribute.PERCEPTION, per);
        attrs.put(Attribute.INSIGHT, ins);
        attrs.put(Attribute.EMPATHY, emp);
        e.setAttributes(attrs);
        return e;
    }
}
