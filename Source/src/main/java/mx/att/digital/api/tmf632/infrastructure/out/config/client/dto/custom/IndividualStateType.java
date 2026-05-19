package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The enum Individual state type.
 */
public enum IndividualStateType {

	/**
	 * Initialized individual state type.
	 */
	INITIALIZED("initialized"),

	/**
	 * Validated individual state type.
	 */
	VALIDATED("validated"),

	/**
	 * Deceased individual state type.
	 */
	DECEASED("deceased");

	  private final String value;

	  IndividualStateType(String value) {
	    this.value = value;
	  }

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	@JsonValue
	  public String getValue() {
	    return value;
	  }

    @Override
	  public String toString() {
	    return String.valueOf(value);
	  }

	/**
	 * From value individual state type.
	 *
	 * @param value the value
	 * @return the individual state type
	 */
	@JsonCreator
	  public static IndividualStateType fromValue(String value) {
	    for (IndividualStateType b : IndividualStateType.values()) {
	      if (b.value.equals(value)) {
	        return b;
	      }
	    }
	    throw new IllegalArgumentException("Unexpected value '" + value + "'");
	  }
}
