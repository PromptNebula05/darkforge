package darkforge.exception;

import java.util.List;

/**
 * Thrown when a talent assignment is invalid for the given profession.
 * Provides the talent name, profession, reason, and the list of
 * available talents so the CLI can display choices.
 */
public class IncompatibleTalentException
        extends DarkForgeException {

    private final String talentName;
    private final String professionName;
    private final String reason;
    private final List<String> availableTalents;

    public IncompatibleTalentException(
            String talentName, String professionName,
            String reason,
            List<String> availableTalents) {
        super(
                String.format(
                        "Talent '%s' is not valid for %s. %s"
                                + " Available: %s",
                        talentName, professionName, reason,
                        String.join(", ", availableTalents)),
                String.format(
                        "IncompatibleTalentException: "
                                + "talent=%s, profession=%s, reason=%s",
                        talentName, professionName, reason)
        );
        this.talentName = talentName;
        this.professionName = professionName;
        this.reason = reason;
        this.availableTalents =
                List.copyOf(availableTalents);
    }

    public String getTalentName() { return talentName; }
    public String getProfessionName() {
        return professionName;
    }
    public String getReason() { return reason; }
    public List<String> getAvailableTalents() {
        return availableTalents;
    }
}