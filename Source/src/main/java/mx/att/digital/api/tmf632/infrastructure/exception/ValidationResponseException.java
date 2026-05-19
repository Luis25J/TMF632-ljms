package mx.att.digital.api.tmf632.infrastructure.exception;

import lombok.Getter;


/**
 * The type Validation response exception.
 */
@Getter
public class ValidationResponseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final String code;
	
	private final String reason;
	
	private final String details;

	/**
	 * Instantiates a new Validation response exception.
	 *
	 * @param code    the code
	 * @param reason  the reason
	 * @param details the details
	 * @param cause   the cause
	 */
	public ValidationResponseException(String code, String reason, String details,
			NullPointerException cause) {
		super(reason, cause); // Llamada explícita al constructor de RuntimeException con el mensaje
		this.code = code;
		this.reason = reason;
		this.details = details;
	}

	/**
	 * Instantiates a new Validation response exception.
	 *
	 * @param code    the code
	 * @param reason  the reason
	 * @param details the details
	 * @param cause   the cause
	 */
	public ValidationResponseException(String code, String reason,
			String details, ReflectiveOperationException cause) {
		super(reason, cause); // Llamada explícita al constructor de RuntimeException con el mensaje
		this.code = code;
		this.reason = reason;
		this.details = details;
	}

	/**
	 * Instantiates a new Validation response exception.
	 *
	 * @param code    the code
	 * @param reason  the reason
	 * @param details the details
	 */
	public ValidationResponseException(String code, String reason,
			String details) {
		super(reason); // Llamada explícita al constructor de RuntimeException con el mensaje
		this.code = code;
		this.reason = reason;
		this.details = details;
	}
}

