package mx.att.digital.api.tmf632.infrastructure.exception;

import java.io.Serial;

import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;

/**
 * The type Response exception.
 */
public class ResponseException extends FormatException {
  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new Response exception.
   *
   * @param builderErrorEnum the builder error enum
   * @param exception        the exception
   */
  public ResponseException(BuilderErrorEnum builderErrorEnum, String exception) {
    super(
        builderErrorEnum.getCode(),
        builderErrorEnum.getMessage().concat(exception),
        builderErrorEnum.getHttpStatus());
  }

  /**
   * Instantiates a new Response exception.
   *
   * @param code       the code
   * @param message    the message
   * @param httpStatus the http status
   */
  public ResponseException(String code, String message, Integer httpStatus) {
    super(
        code,
        message,
        httpStatus);
  }
}

