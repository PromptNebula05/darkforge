package darkforge.exception;

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

  /**
   * Player-facing message suitable for console display.
   * No stack traces, no class names, no technical jargon.
   */
  public String getUserMessage() {
    return userMessage;
  }

  /**
   * Developer-facing detail for logging/debugging.
   * Includes field names, values, and constraint descriptions.
   */
  public String getTechnicalDetail() {
    return technicalDetail;
  }
}
