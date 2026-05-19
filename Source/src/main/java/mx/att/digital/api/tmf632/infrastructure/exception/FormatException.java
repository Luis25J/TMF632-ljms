package mx.att.digital.api.tmf632.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.io.Serial;

/**
 * The type Format exception.
 */
@Getter
@Builder
@JsonIgnoreProperties({
    "cause",
    "stackTrace",
    "localizedMessage",
    "suppressed",
    "httpCodeStatus"
})
public class FormatException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  private final String code;
  private final String message;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private final Integer httpCodeStatus;

  /**
   * Instantiates a new Format exception.
   *
   * @param code           the code
   * @param message        the message
   * @param httpCodeStatus the http code status
   */
  public FormatException(String code, String message, Integer httpCodeStatus) {
    this.code = code;
    this.message = message;
    this.httpCodeStatus = httpCodeStatus;
  }
}
