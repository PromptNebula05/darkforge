package darkforge.data;

import darkforge.model.Origin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameDataProvider singleton.
 * Verifies initialization, D66 table loading,
 * origin lookups, and profession name retrieval.
 */
class GameDataProviderTest {

    private GameDataProvider provider;

    @BeforeEach
    void setUp() {
        provider = GameDataProvider.getTheInstance();
        provider.initialize();
    }

    // ── Singleton ─────────────────────────────────────────────

    @Test
    void shouldReturnSameInstance() {
        GameDataProvider first = GameDataProvider.getTheInstance();
        GameDataProvider second = GameDataProvider.getTheInstance();
        assertSame(first, second,
                "getInstance() should return the same Singleton");
    }

    @Test
    void shouldNotReturnNull() {
        assertNotNull(GameDataProvider.getTheInstance());
    }

    // ── Initialization ────────────────────────────────────────

    @Test
    void shouldInitializeWithoutException() {
        assertDoesNotThrow(() -> provider.initialize(),
                "Repeated initialization should be safe");
    }

    // ── Origins ───────────────────────────────────────────────

    @Test
    void shouldReturn13Origins() {
        List<Origin> origins = provider.getOrigins();
        assertEquals(13, origins.size());
    }

    @Test
    void originsShouldHaveLocations() {
        for (Origin origin : provider.getOrigins()) {
            assertNotNull(origin.getLocation(),
                    "Each origin should have a location");
            assertFalse(origin.getLocation().isBlank());
        }
    }

    @Test
    void shouldLookUpOriginByD66() {
        Origin origin = provider.getOriginByD66(11);
        assertNotNull(origin);
        assertNotNull(origin.getLocation());
    }

    @Test
    void shouldRejectInvalidD66ForOrigin() {
        assertThrows(IllegalArgumentException.class,
                () -> provider.getOriginByD66(77));
    }

    @Test
    void shouldRejectZeroD66ForOrigin() {
        assertThrows(IllegalArgumentException.class,
                () -> provider.getOriginByD66(0));
    }

    // ── D66 Flavor Tables ─────────────────────────────────────

    @Test
    void shouldReturnNonEmptyQuirks() {
        var quirks = provider.getQuirks();
        assertNotNull(quirks);
        assertFalse(quirks.isEmpty(),
                "Quirks should not be empty");
    }

    @Test
    void shouldReturnNonEmptyKeepsakes() {
        var keepsakes = provider.getKeepsakes();
        assertNotNull(keepsakes);
        assertFalse(keepsakes.isEmpty(),
                "Keepsakes should not be empty");
    }

    @Test
    void shouldReturnNonEmptyAppearances() {
        var appearances = provider.getAppearances();
        assertNotNull(appearances);
        assertFalse(appearances.isEmpty(),
                "Appearances should not be empty");
    }

    @Test
    void shouldReturnNonEmptyExplorerReasons() {
        var reasons = provider.getExplorerReasons();
        assertNotNull(reasons);
        assertFalse(reasons.isEmpty(),
                "Explorer reasons should not be empty");
    }

    // ── Profession Names ──────────────────────────────────────

    @Test
    void shouldReturnEightValidProfessionNames() {
        var names = provider.getValidProfessionNames();
        assertNotNull(names);
        assertEquals(8, names.size());
    }

    @Test
    void validProfessionNamesShouldIncludeScholar() {
        assertTrue(
                provider.getValidProfessionNames()
                        .contains("Scholar"));
    }

    @Test
    void validProfessionNamesShouldIncludeEnforcer() {
        assertTrue(
                provider.getValidProfessionNames()
                        .contains("Enforcer"));
    }

    // ── Name Tables ───────────────────────────────────────────

    @Test
    void shouldReturnFirstNamesForScholar() {
        var firstNames =
                provider.getFirstNames("Scholar");
        assertNotNull(firstNames);
        assertFalse(firstNames.isEmpty(),
                "Scholar first names should not be empty");
    }

    @Test
    void shouldReturnLastNamesForScholar() {
        var lastNames =
                provider.getLastNames("Scholar");
        assertNotNull(lastNames);
        assertFalse(lastNames.isEmpty(),
                "Scholar last names should not be empty");
    }
}