package darkforge.exception;

/**
 * Abstract root of the DARKFORGE custom exception hierarchy.
 * Extends Exception (checked) because every DARKFORGE failure
 * is recoverable at the CLI — re-enter attributes, choose a
 * different file, pick a valid talent.
 *
 * Dual message fields:
 * - userMessage: player-facing, no jargon, displayed by CLI
 * - technicalDetail: developer-facing, used by getMessage()
 */
public abstract class DarkForgeException extends Exception {
  private final String userMessage;
  private final String technicalDetail;

  protected DarkForgeException(String userMessage,
                               String technicalDetail, Throwable cause) {
    super(technicalDetail, cause);
    this.userMessage = userMessage;
    this.technicalDetail = technicalDetail;
  }

  protected DarkForgeException(String userMessage,
                               String technicalDetail) {
    super(technicalDetail);
    this.userMessage = userMessage;
    this.technicalDetail = technicalDetail;
  }

  /** Player-facing message suitable for console display. */
  public String getUserMessage() { return userMessage; }

  /** Developer-facing detail for logging/debugging. */
  public String getTechnicalDetail() {
    return technicalDetail;
  }
}