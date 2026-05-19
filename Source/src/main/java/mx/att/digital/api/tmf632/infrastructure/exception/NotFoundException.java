package mx.att.digital.api.tmf632.infrastructure.exception;

/**
 * The type Not found exception.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Instantiates a new Not found exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
