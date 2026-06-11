package darkforge.crew;

import darkforge.model.Attribute;
import darkforge.model.Explorer;
import darkforge.model.Talent;
import darkforge.model.TalentCategory;
import darkforge.model.Item;
import darkforge.model.Weapon;
import darkforge.model.WeaponType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics utility for Crew composition from
 * Coriolis: The Great Dark (Ch. 2, Ch. 12).
 *
 * Collections demonstrated:
 *   EnumMap<Attribute, Double>       — averages
 *   EnumMap<TalentCategory, Integer> — coverage
 *   stream().flatMap()               — flatten
 *   stream().mapToInt().average()    — averaging
 *   Comparator.comparingInt()        — sorting
 *   HashSet<Explorer>                — tracking
 */
public class CrewAnalytics {

    private final Crew crew;

    public CrewAnalytics(Crew crew) {
        this.crew = crew;
    }

    // =========================================
    // Attribute averages
    // =========================================

    /**
     * Average of each attribute across all
     * crew members. Returns 0.0 for all
     * attributes if crew is empty.
     */
    public EnumMap<Attribute, Double>
    getAttributeAverages() {
        EnumMap<Attribute, Double> averages =
                new EnumMap<>(Attribute.class);
        List<Explorer> members =
                crew.getMembers();

        for (Attribute attr
                : Attribute.values()) {
            if (members.isEmpty()) {
                averages.put(attr, 0.0);
            } else {
                double avg = members.stream()
                        .mapToInt(e ->
                                e.getAttribute(attr))
                        .average()
                        .orElse(0.0);
                averages.put(attr, avg);
            }
        }
        return averages;
    }

    // =========================================
    // Talent coverage
    // =========================================

    /**
     * Count of talents per category across
     * all crew members, flattened.
     */
    public EnumMap<TalentCategory, Integer>
    getTalentCoverage() {
        EnumMap<TalentCategory, Integer>
                coverage = new EnumMap<>(
                TalentCategory.class);

        for (TalentCategory cat
                : TalentCategory.values()) {
            coverage.put(cat, 0);
        }

        List<Talent> allTalents =
                crew.getMembers().stream()
                        .flatMap(e ->
                                e.getTalents().getAll()
                                        .stream())
                        .collect(Collectors.toList());

        for (Talent talent : allTalents) {
            coverage.merge(
                    talent.getCategory(),
                    1, Integer::sum);
        }

        return coverage;
    }

    /**
     * Categories with zero talents across
     * the entire crew.
     */
    public List<TalentCategory>
    getWeakCategories() {
        EnumMap<TalentCategory, Integer>
                coverage = getTalentCoverage();
        return Arrays.stream(
                        TalentCategory.values())
                .filter(cat ->
                        coverage.getOrDefault(
                                cat, 0) == 0)
                .collect(Collectors.toList());
    }

    /**
     * Categories with the most talent
     * coverage across the crew.
     */
    public List<TalentCategory>
    getStrongCategories() {
        EnumMap<TalentCategory, Integer>
                coverage = getTalentCoverage();
        int max = coverage.values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(0);
        if (max == 0) return List.of();
        return Arrays.stream(
                        TalentCategory.values())
                .filter(cat ->
                        coverage.getOrDefault(
                                cat, 0) == max)
                .collect(Collectors.toList());
    }

    // =========================================
    // Optimal role assignment
    // =========================================

    /**
     * Greedy best-fit assignment: iterate
     * mandatory roles by priority, assign
     * the highest-fitness unassigned member.
     * Then assign optional roles if members
     * remain.
     */
    public Map<CrewRole, Explorer>
    getOptimalRoleAssignment() {
        Map<CrewRole, Explorer> assignment =
                new EnumMap<>(CrewRole.class);
        Set<Explorer> assigned =
                new HashSet<>();
        List<Explorer> members =
                new ArrayList<>(crew.getMembers());

        // Mandatory roles first
        for (CrewRole role
                : CrewRole.values()) {
            if (role.isOptional()) continue;
            assignBestFit(role, members,
                    assigned, assignment);
        }

        // Optional roles with remaining
        for (CrewRole role
                : CrewRole.values()) {
            if (!role.isOptional()) continue;
            assignBestFit(role, members,
                    assigned, assignment);
        }

        return assignment;
    }

    private void assignBestFit(
            CrewRole role,
            List<Explorer> members,
            Set<Explorer> assigned,
            Map<CrewRole, Explorer> assignment) {
        members.stream()
                .filter(e -> !assigned.contains(e))
                .max(Comparator.comparingInt(
                        (Explorer e) ->
                                role.getRoleFitness(e)))
                .ifPresent(best -> {
                    assignment.put(role, best);
                    assigned.add(best);
                });
    }

    // =========================================
    // Catalog-aware crew analytics
    // =========================================

    /**
     * Total rukh value of all CharacterItems
     * across all crew members.
     * flatMap + mapToInt + sum
     */
    public int getCrewInventoryValue() {
        return crew.getMembers().stream()
                .flatMap(e ->
                        e.getAllItems().stream())
                .mapToInt(Item::getCost)
                .sum();
    }

    /**
     * Per-explorer carry load as a map of
     * name → load percentage.
     * collect(toMap)
     */
    public Map<String, Double>
    getCrewEquipLoadSummary() {
        return crew.getMembers().stream()
                .collect(Collectors.toMap(
                        Explorer::getName,
                        e -> {
                            double max =
                                    e.getMaxCarryWeight();
                            return max > 0
                                    ? e.getCurrentCarryWeight()
                                      / max * 100.0
                                    : 0.0;
                        }));
    }

    /**
     * Weapon type distribution across the
     * entire crew.
     * flatMap + filter + groupingBy + counting
     */
    public Map<WeaponType, Long>
    getCrewWeaponDistribution() {
        return crew.getMembers().stream()
                .flatMap(e ->
                        e.getAllItems().stream())
                .filter(Weapon.class::isInstance)
                .map(Weapon.class::cast)
                .collect(Collectors.groupingBy(
                        Weapon::getWeaponType,
                        Collectors.counting()));
    }

    /**
     * Explorer with the most equipped items.
     * stream().max(comparingInt)
     */
    public Optional<Explorer>
    getMostEquippedExplorer() {
        return crew.getMembers().stream()
                .max(Comparator.comparingInt(
                        e -> e.getEquipped().size()));
    }

    // =========================================
    // Summary
    // =========================================

    /**
     * Total talent count across all members.
     */
    public int getTotalTalentCount() {
        return crew.getMembers().stream()
                .mapToInt(e ->
                        e.getTalents().size())
                .sum();
    }
}