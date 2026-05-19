package mx.att.digital.api.tmf632.infrastructure.exception;

/**
 * The type Connector request exception.
 */
public class ConnectorRequestException extends RuntimeException {

  private final String requestInfo;

  /**
   * Instantiates a new Connector request exception.
   *
   * @param message     the message
   * @param requestInfo the request info
   * @param cause       the cause
   */
  public ConnectorRequestException(String message, String requestInfo, Throwable cause) {
    super(message, cause);
    this.requestInfo = requestInfo;
  }

  /**
   * Gets request info.
   *
   * @return the request info
   */
  public String getRequestInfo() {
    return requestInfo;
  }
}

