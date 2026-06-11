package darkforge.data;

import darkforge.collection.Registry;
import darkforge.model.Talent;
import darkforge.model.TalentCategory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Category-indexed registry of all talents
 * from Coriolis: The Great Dark.
 * Wraps Registry<TalentCategory, Talent> with
 * convenience methods for category lookup,
 * profession cross-referencing, name search,
 * and per-category counts.
 *
 * Constructed by GameDataProvider from
 * talents.json + profession talent associations.
 */
public class TalentRegistry {

    private final Registry<TalentCategory, Talent>
            registry;
    private final List<Talent> allTalents;
    private final Map<String, List<String>>
            professionTalentNames;

    // =========================================
    // Constructor
    // =========================================

    /**
     * @param talents all talents loaded from JSON
     * @param professionTalentNames mapping of
     *        profession name to its talent names
     *        (from professions.json)
     */
    public TalentRegistry(
            List<Talent> talents,
            Map<String, List<String>>
                    professionTalentNames) {
        this.registry = new Registry<>();
        this.allTalents =
                new ArrayList<>(talents);
        this.professionTalentNames =
                new LinkedHashMap<>(
                        professionTalentNames);

        for (Talent talent : talents) {
            registry.register(
                    talent.getCategory(), talent);
        }
    }

    // =========================================
    // Category lookup
    // =========================================

    public List<Talent> getByCategory(
            TalentCategory category) {
        return registry.getByKey(category);
    }

    // =========================================
    // Profession cross-reference
    // =========================================

    /**
     * Returns talents eligible for the given
     * profession, based on the profession's
     * talent name list from professions.json.
     */
    public List<Talent>
    getEligibleForProfession(
            String professionName) {
        List<String> names =
                professionTalentNames.getOrDefault(
                        professionName, List.of());
        return allTalents.stream()
                .filter(t -> names.contains(
                        t.getName()))
                .collect(Collectors.toList());
    }

    // =========================================
    // Name search
    // =========================================

    /**
     * Partial, case-insensitive match across
     * name, description, and effect.
     */
    public List<Talent> search(String query) {
        String q = query.toLowerCase();
        return allTalents.stream()
                .filter(t ->
                        t.getName().toLowerCase()
                                .contains(q)
                                || t.getDescription()
                                .toLowerCase().contains(q)
                                || t.getEffect().toLowerCase()
                                .contains(q))
                .collect(Collectors.toList());
    }

    // =========================================
    // Exact name lookup
    // =========================================

    public Talent getTalentByName(String name) {
        return allTalents.stream()
                .filter(t -> t.getName()
                        .equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    // =========================================
    // Category counts
    // =========================================

    /**
     * Map of each category to its talent count.
     * Values sum to total talent count.
     */
    public Map<TalentCategory, Integer>
    getCategoryCounts() {
        Map<TalentCategory, Integer> counts =
                new EnumMap<>(TalentCategory.class);
        for (TalentCategory cat
                : TalentCategory.values()) {
            counts.put(cat,
                    registry.countByKey(cat));
        }
        return counts;
    }

    // =========================================
    // Accessors
    // =========================================

    public List<Talent> getAllTalents() {
        return Collections.unmodifiableList(
                allTalents);
    }

    public int size() {
        return allTalents.size();
    }
}