package darkforge.creation;

import darkforge.model.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Search utility for finding Explorers, talents,
 * and equipment by name with case-insensitive
 * matching and relevance ranking.
 *
 */
public class SearchUtil {
    private SearchUtil() {}

    /**
     * Search a list of entities by name.
     * Case-insensitive substring match.
     */
    public static <T extends GameEntity>
    List<T> searchByName(
            List<T> entities, String query) {
        String lowerQuery = query.toLowerCase().trim();
        return entities.stream()
                .filter(e -> e.getName().toLowerCase()
                        .contains(lowerQuery))
                .collect(Collectors.toList());
    }

    /**
     * Search with relevance ranking.
     * Exact match > starts-with > contains.
     */
    public static <T extends GameEntity>
    List<T> searchByNameRanked(
            List<T> entities, String query) {
        String lowerQuery = query.toLowerCase().trim();
        return entities.stream()
                .filter(e -> e.getName().toLowerCase()
                        .contains(lowerQuery))
                .sorted((a, b) -> {
                    String aLower =
                            a.getName().toLowerCase();
                    String bLower =
                            b.getName().toLowerCase();
                    boolean aExact =
                            aLower.equals(lowerQuery);
                    boolean bExact =
                            bLower.equals(lowerQuery);
                    if (aExact != bExact)
                        return aExact ? -1 : 1;
                    boolean aStarts =
                            aLower.startsWith(lowerQuery);
                    boolean bStarts =
                            bLower.startsWith(lowerQuery);
                    if (aStarts != bStarts)
                        return aStarts ? -1 : 1;
                    return aLower.compareTo(bLower);
                })
                .collect(Collectors.toList());
    }

    /**
     * Format search results as numbered list.
     */
    public static <T extends GameEntity>
    String formatSearchResults(
            List<T> results) {
        if (results.isEmpty()) {
            return "No matches found.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            sb.append(String.format("  %d. %s\n",
                    i + 1, results.get(i).getName()));
        }
        return sb.toString();
    }
}