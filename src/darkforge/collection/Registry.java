package darkforge.collection;

import darkforge.model.GameEntity;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Two-parameter generic container that groups game entities
 * by key. Supports register/retrieve, filtered getAll with
 * wildcard bound, immutable views, and per-key/total counts.
 *
 * Concrete usages:
 *   Registry<TalentCategory, Talent>
 *   Registry<BirdType, GarudaPower>
 *   Registry<EquipmentWeight, Equipment>
 *   Registry<String, Equipment>  (by source)
 */
public class Registry<K, V extends GameEntity> {

    private final Map<K, List<V>> entries;

    // =========================================
    // Constructor
    // =========================================

    public Registry() {
        this.entries = new LinkedHashMap<>();
    }

    // =========================================
    // Registration
    // =========================================

    // Add value under key (creates list if absent).
    public void register(K key, V value) {
        entries.computeIfAbsent(key,
                k -> new ArrayList<>()).add(value);
    }

    // =========================================
    // Retrieval
    // =========================================

    // Immutable list for key (empty if absent — never null).
    public List<V> getByKey(K key) {
        return Collections.unmodifiableList(
                entries.getOrDefault(key, List.of()));
    }

    // All values across all keys, flattened.
    public List<V> getAll() {
        return entries.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    // WILDCARD BOUND: filtered across all keys.
    public List<V> getAll(
            Predicate<? super V> filter) {
        return entries.values().stream()
                .flatMap(List::stream)
                .filter(filter)
                .collect(Collectors.toList());
    }

    // All registered keys.
    public Set<K> keys() {
        return Collections.unmodifiableSet(
                entries.keySet());
    }

    // =========================================
    // Counts
    // =========================================

    public int countByKey(K key) {
        return entries.getOrDefault(
                key, List.of()).size();
    }

    public int totalCount() {
        return entries.values().stream()
                .mapToInt(List::size).sum();
    }
}