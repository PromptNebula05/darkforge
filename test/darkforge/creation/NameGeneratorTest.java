package darkforge.creation;

import darkforge.data.GameDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for NameGenerator D66-based name tables.
 * Verifies generation, capitalization, hyphenated name handling,
 * and fallback for unknown professions.
 */
class NameGeneratorTest {

    private NameGenerator generator;

    @BeforeEach
    void setUp() {
        GameDataProvider.getTheInstance().initialize();
        generator = new NameGenerator(
                new Random(42), GameDataProvider.getTheInstance());
    }

    @Test
    void shouldGenerateNonEmptyName() {
        String name = generator.generateName("Scholar");
        assertNotNull(name);
        assertFalse(name.isBlank());
    }

    @Test
    void shouldGenerateTwoPartName() {
        String name = generator.generateName("Scholar");
        assertTrue(name.contains(" "),
                "Generated name should contain a space "
                        + "(first + last)");
    }

    @Test
    void shouldCapitalizeFirstLetter() {
        String name = generator.generateName("Scholar");
        String[] parts = name.split(" ");
        for (String part : parts) {
            if (!part.isEmpty()) {
                assertTrue(
                        Character.isUpperCase(part.charAt(0)),
                        "Each name part should start uppercase: "
                                + part);
            }
        }
    }

    @Test
    void shouldHandleHyphenatedNames() {
        NameGenerator seeded =
                new NameGenerator(new Random(12345),
                        GameDataProvider.getTheInstance());
        for (int i = 0; i < 100; i++) {
            String name = seeded.generateName("Scholar");
            if (name.contains("-")) {
                for (String part : name.split("-")) {
                    String trimmed = part.trim();
                    if (!trimmed.isEmpty()) {
                        assertTrue(
                                Character.isUpperCase(
                                        trimmed.charAt(0)),
                                "Hyphenated part should be "
                                        + "capitalized: " + part);
                    }
                }
            }
        }
    }

    @Test
    void shouldReturnFallbackForUnknownProfession() {
        String name = generator.generateName("Wizard");
        assertNotNull(name);
        assertEquals("Unknown Explorer", name);
    }

    @Test
    void shouldGenerateNamesForAllProfessions() {
        String[] professions = {
                "Scholar", "Enforcer", "Artist", "Esoteric",
                "Odd Jobber", "Roughneck", "Scoundrel", "Traveler"
        };
        for (String profession : professions) {
            String name =
                    generator.generateName(profession);
            assertNotNull(name,
                    profession + " should generate a name");
            assertNotEquals("Unknown Explorer", name,
                    profession + " should have name tables "
                            + "initialized");
        }
    }

    @Test
    void shouldGenerateDifferentNamesOnSubsequentCalls() {
        NameGenerator gen =
                new NameGenerator(new Random(42),
                        GameDataProvider.getTheInstance());
        String first = gen.generateName("Scholar");
        boolean foundDifferent = false;
        for (int i = 0; i < 100; i++) {
            if (!gen.generateName("Scholar")
                    .equals(first)) {
                foundDifferent = true;
                break;
            }
        }
        assertTrue(foundDifferent,
                "Should generate different names over "
                        + "multiple calls");
    }
}