package darkforge.crew;

import darkforge.display.Displayable;
import darkforge.model.Explorer;

import java.util.*;

/**
 * The full crew aggregate from Coriolis:
 * The Great Dark (Ch. 2, Ch. 12).
 *
 * Collections demonstrated:
 *   List<Explorer>              — ordered roster
 *   EnumMap<CrewRole, Explorer>  — role mapping
 *   Set<String>                 — learned maneuvers
 *   Unmodifiable wrappers for safe external access
 */
public class Crew implements Displayable {

    private final String name;
    private final List<Explorer> members;
    private final EnumMap<CrewRole, Explorer>
            roleAssignments;
    private Bird bird;
    private Vehicle shuttle;
    private Vehicle rover;
    private int totalSupply;
    private int crewPoints;
    private final Set<String> learnedManeuvers;

    // =========================================
    // Constructor
    // =========================================

    public Crew(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.roleAssignments =
                new EnumMap<>(CrewRole.class);
        this.totalSupply = 0;
        this.crewPoints = 0;
        this.learnedManeuvers =
                new LinkedHashSet<>();
    }

    // =========================================
    // Roster management
    // =========================================

    public void addMember(Explorer explorer) {
        if (members.size() >= 5)
            throw new IllegalStateException(
                    "Crew full (max 5)");
        members.add(explorer);
    }

    public void removeMember(Explorer explorer) {
        members.remove(explorer);
        roleAssignments.values()
                .remove(explorer);
    }

    public List<Explorer> getMembers() {
        return Collections
                .unmodifiableList(members);
    }

    public int getCrewSize() {
        return members.size();
    }

    // =========================================
    // Role assignment (EnumMap)
    // =========================================

    public void assignRole(CrewRole role,
                           Explorer explorer) {
        if (!members.contains(explorer))
            throw new IllegalArgumentException(
                    explorer.getName()
                            + " is not in the crew");
        roleAssignments.put(role, explorer);
    }

    public Explorer getAssignedExplorer(
            CrewRole role) {
        return roleAssignments.get(role);
    }

    public CrewRole getAssignedRole(
            Explorer explorer) {
        return roleAssignments.entrySet()
                .stream()
                .filter(e -> e.getValue()
                        .equals(explorer))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

    public boolean areAllMandatoryRolesFilled() {
        return Arrays.stream(CrewRole.values())
                .filter(r -> !r.isOptional())
                .allMatch(roleAssignments
                        ::containsKey);
    }

    public Map<CrewRole, Explorer>
    getRoleAssignments() {
        return Collections.unmodifiableMap(
                roleAssignments);
    }

    public Map<CrewRole, List<Explorer>>
    suggestRoleAssignments() {
        Map<CrewRole, List<Explorer>> suggestions
                = new EnumMap<>(CrewRole.class);
        for (CrewRole role : CrewRole.values()) {
            List<Explorer> ranked =
                    new ArrayList<>(members);
            ranked.sort(Comparator.comparingInt(
                            (Explorer e) -> role
                                    .getRoleFitness(e))
                    .reversed());
            suggestions.put(role, ranked);
        }
        return suggestions;
    }

    // =========================================
    // Supply management
    // =========================================

    public void addSupply(int points) {
        totalSupply += points;
    }

    public boolean consumeSupply(int points) {
        if (totalSupply < points) return false;
        totalSupply -= points;
        return true;
    }

    public int getTotalSupply() {
        return totalSupply;
    }

    public int getSupplyPerExplorer() {
        return members.isEmpty() ? 0
                : totalSupply / members.size();
    }

    public boolean isLowOnSupply() {
        return getSupplyPerExplorer() <= 3;
    }

    // =========================================
    // Crew maneuvers
    // =========================================

    public Set<String> getAvailableManeuvers() {
        Set<String> all =
                new LinkedHashSet<>();
        for (Map.Entry<CrewRole, Explorer> entry
                : roleAssignments.entrySet()) {
            all.add(entry.getKey()
                    .getStartingManeuver());
        }
        all.addAll(learnedManeuvers);
        return Collections
                .unmodifiableSet(all);
    }

    public void learnManeuver(
            String maneuver, int cpCost) {
        if (crewPoints < cpCost)
            throw new IllegalStateException(
                    "Not enough CP");
        crewPoints -= cpCost;
        learnedManeuvers.add(maneuver);
    }

    // =========================================
    // Bird & Vehicles
    // =========================================

    public void setBird(Bird bird) {
        this.bird = bird;
    }

    public Bird getBird() { return bird; }

    public void setShuttle(Vehicle shuttle) {
        this.shuttle = shuttle;
    }

    public Vehicle getShuttle() {
        return shuttle;
    }

    public void setRover(Vehicle rover) {
        this.rover = rover;
    }

    public Vehicle getRover() { return rover; }

    // =========================================
    // Getters
    // =========================================

    public String getName() { return name; }

    public int getCrewPoints() {
        return crewPoints;
    }

    public void addCrewPoints(int points) {
        crewPoints += points;
    }

    public Set<String> getLearnedManeuvers() {
        return Collections
                .unmodifiableSet(learnedManeuvers);
    }

    // =========================================
    // Displayable
    // =========================================

    @Override
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Crew: ").append(name)
                .append(" ===").append("\n");
        sb.append("Members (").append(
                members.size()).append("/5):\n");

        for (Explorer member : members) {
            CrewRole role =
                    getAssignedRole(member);
            String roleStr = (role != null)
                    ? " [" + role.getDisplayName()
                      + "]"
                    : "";
            sb.append("  \u2022 ")
                    .append(member.getName())
                    .append(roleStr).append("\n");
        }

        sb.append("\nBird: ");
        if (bird != null) {
            sb.append(bird.getName())
                    .append(" (")
                    .append(bird.getType()
                            .getDisplayName())
                    .append(")");
        } else {
            sb.append("None");
        }
        sb.append("\n");

        sb.append("Shuttle: ");
        if (shuttle != null) {
            sb.append(shuttle.getName());
        } else {
            sb.append("None");
        }
        sb.append("\n");

        sb.append("Rover: ");
        if (rover != null) {
            sb.append(rover.getName());
        } else {
            sb.append("None");
        }
        sb.append("\n");

        sb.append("\nSupply: ")
                .append(totalSupply)
                .append(" (")
                .append(getSupplyPerExplorer())
                .append(" per explorer)\n");

        Set<String> maneuvers =
                getAvailableManeuvers();
        sb.append("Maneuvers: ");
        if (maneuvers.isEmpty()) {
            sb.append("None");
        } else {
            sb.append(
                    String.join(", ", maneuvers));
        }
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public String toSummary() {
        return String.format(
                "%s (%d members) \u2014 %s Bird: %s"
                        + " \u2014 Supply: %d",
                name, members.size(),
                bird != null
                        ? bird.getType().getDisplayName()
                        : "None",
                bird != null
                        ? bird.getName() : "None",
                totalSupply);
    }
}