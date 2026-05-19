package mx.att.digital.api.tmf632.infrastructure.exception;

/**
 * The type Field mapping exception.
 */
public class FieldMappingException extends RuntimeException {

    /**
     * Instantiates a new Field mapping exception.
     *
     * @param message the message
     */
    public FieldMappingException(String message){
        super(message);
    }

    /**
     * Instantiates a new Field mapping exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public FieldMappingException(String message, Throwable cause){
        super(message, cause);
    }
}
