package darkforge.creation;

import darkforge.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SearchUtil case-insensitive search,
 * relevance ranking, and result formatting.
 */
class SearchUtilTest {

    /** Minimal concrete subclass for testing. */
    private static class TestEntity extends GameEntity {
        TestEntity(String name) {
            super(name, "test description");
        }
        @Override
        public String display() { return getName(); }
    }

    private final List<TestEntity> entities = List.of(
            new TestEntity("Smart Scholar"),
            new TestEntity("Artist Supreme"),
            new TestEntity("Smart Enforcer"),
            new TestEntity("Roughneck")
    );

    @Test
    void shouldFindCaseInsensitiveMatch() {
        List<TestEntity> results =
                SearchUtil.searchByName(entities, "smart");
        assertEquals(2, results.size());
    }

    @Test
    void shouldFindPartialMatch() {
        List<TestEntity> results =
                SearchUtil.searchByName(entities, "art");
        assertTrue(results.size() >= 2,
                "'art' should match Smart Scholar, "
                        + "Artist Supreme, and Smart Enforcer");
    }

    @Test
    void shouldReturnEmptyForNoMatch() {
        List<TestEntity> results =
                SearchUtil.searchByName(entities, "Wizard");
        assertTrue(results.isEmpty());
    }

    @Test
    void shouldHandleEmptyQuery() {
        List<TestEntity> results =
                SearchUtil.searchByName(entities, "");
        assertEquals(entities.size(), results.size(),
                "Empty query should return all entities");
    }

    @Test
    void shouldTrimQueryWhitespace() {
        List<TestEntity> results =
                SearchUtil.searchByName(
                        entities, "  smart  ");
        assertEquals(2, results.size());
    }

    @Test
    void shouldRankExactMatchFirst() {
        List<TestEntity> results =
                SearchUtil.searchByNameRanked(
                        entities, "Roughneck");
        assertEquals("Roughneck",
                results.get(0).getName(),
                "Exact match should be first");
    }

    @Test
    void shouldRankStartsWithBeforeContains() {
        List<TestEntity> results =
                SearchUtil.searchByNameRanked(
                        entities, "Smart");
        assertTrue(
                results.get(0).getName().startsWith("Smart"),
                "Starts-with should rank before contains");
    }

    @Test
    void shouldFormatResultsAsNumberedList() {
        List<TestEntity> results =
                SearchUtil.searchByName(entities, "smart");
        String formatted =
                SearchUtil.formatSearchResults(results);
        assertTrue(formatted.contains("1."));
        assertTrue(formatted.contains("2."));
    }

    @Test
    void shouldFormatEmptyResultsWithMessage() {
        String formatted =
                SearchUtil.formatSearchResults(List.of());
        assertTrue(
                formatted.contains("No matches found"));
    }

    @Test
    void shouldReturnResultsContainingEntityNames() {
        List<TestEntity> results =
                SearchUtil.searchByName(
                        entities, "Scholar");
        assertEquals(1, results.size());
        assertEquals("Smart Scholar",
                results.get(0).getName());
    }
}