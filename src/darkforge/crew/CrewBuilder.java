package darkforge.crew;

import darkforge.model.Explorer;

import java.util.*;

/**
 * Fluent builder that enforces all crew
 * composition rules from Coriolis:
 * The Great Dark (Ch. 2, Ch. 12).
 *
 * Rules enforced at build():
 *   - Name must be set
 *   - 4–5 members required
 *   - All 4 mandatory roles assigned
 *   - Each assigned explorer must be in roster
 *   - Bird must be set
 */
public class CrewBuilder {

    private String name;
    private final List<Explorer> members;
    private final EnumMap<CrewRole, Explorer>
            roleAssignments;
    private Bird bird;
    private Vehicle shuttle;
    private Vehicle rover;
    private int supply;
    private int crewPoints;

    // =========================================
    // Constructor
    // =========================================

    public CrewBuilder() {
        this.members = new ArrayList<>();
        this.roleAssignments =
                new EnumMap<>(CrewRole.class);
        this.supply = 0;
        this.crewPoints = 0;
    }

    // =========================================
    // Fluent setters
    // =========================================

    public CrewBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CrewBuilder addMember(
            Explorer explorer) {
        members.add(explorer);
        return this;
    }

    public CrewBuilder assignRole(CrewRole role,
                                  Explorer explorer) {
        roleAssignments.put(role, explorer);
        return this;
    }

    public CrewBuilder bird(Bird bird) {
        this.bird = bird;
        return this;
    }

    public CrewBuilder shuttle(Vehicle shuttle) {
        this.shuttle = shuttle;
        return this;
    }

    public CrewBuilder rover(Vehicle rover) {
        this.rover = rover;
        return this;
    }

    public CrewBuilder supply(int supply) {
        this.supply = supply;
        return this;
    }

    public CrewBuilder crewPoints(int cp) {
        this.crewPoints = cp;
        return this;
    }

    // =========================================
    // Build with validation
    // =========================================

    public Crew build() {
        validate();

        Crew crew = new Crew(name);

        for (Explorer member : members) {
            crew.addMember(member);
        }

        for (Map.Entry<CrewRole, Explorer> entry
                : roleAssignments.entrySet()) {
            crew.assignRole(
                    entry.getKey(),
                    entry.getValue());
        }

        crew.setBird(bird);

        if (shuttle != null) {
            crew.setShuttle(shuttle);
        }
        if (rover != null) {
            crew.setRover(rover);
        }

        if (supply > 0) {
            crew.addSupply(supply);
        }
        if (crewPoints > 0) {
            crew.addCrewPoints(crewPoints);
        }

        return crew;
    }

    // =========================================
    // Validation
    // =========================================

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException(
                    "Crew name is required");
        }

        if (members.size() < 4) {
            throw new IllegalStateException(
                    "Crew requires at least 4"
                            + " members, found "
                            + members.size());
        }

        if (members.size() > 5) {
            throw new IllegalStateException(
                    "Crew allows at most 5"
                            + " members, found "
                            + members.size());
        }

        if (bird == null) {
            throw new IllegalStateException(
                    "Crew requires a Bird"
                            + " companion");
        }

        // All mandatory roles must be assigned
        for (CrewRole role
                : CrewRole.values()) {
            if (!role.isOptional()
                    && !roleAssignments
                    .containsKey(role)) {
                throw new IllegalStateException(
                        "Mandatory role "
                                + role.getDisplayName()
                                + " is not assigned");
            }
        }

        // Every assigned explorer must be
        // in the roster
        for (Map.Entry<CrewRole, Explorer> entry
                : roleAssignments.entrySet()) {
            if (!members.contains(
                    entry.getValue())) {
                throw new IllegalStateException(
                        entry.getValue().getName()
                                + " is assigned to "
                                + entry.getKey()
                                .getDisplayName()
                                + " but is not in"
                                + " the roster");
            }
        }
    }
}