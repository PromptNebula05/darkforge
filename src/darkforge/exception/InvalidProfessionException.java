package darkforge.exception;

import java.util.List;

/**
 * Thrown when an unknown profession name is provided to ExplorerFactory.
 * Provides the attempted name and the list of valid professions
 * so the CLI can display choices.
 */
public class InvalidProfessionException
        extends DarkForgeException {

    private final String attemptedProfession;
    private final List<String> validProfessions;

    public InvalidProfessionException(
            String attemptedProfession,
            List<String> validProfessions) {
        super(
                String.format(
                        "'%s' is not a valid profession. "
                                + "Choose from: %s",
                        attemptedProfession,
                        String.join(", ", validProfessions)),
                String.format(
                        "InvalidProfessionException: "
                                + "attempted=%s, valid=%s",
                        attemptedProfession, validProfessions)
        );
        this.attemptedProfession = attemptedProfession;
        this.validProfessions =
                List.copyOf(validProfessions);
    }

    public String getAttemptedProfession() {
        return attemptedProfession;
    }
    public List<String> getValidProfessions() {
        return validProfessions;
    }
}