package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


/**
 * The type Medium characteristic.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediumCharacteristic {

    @JsonProperty("@type")
    private String type;
    private String neighborhood;
    private String municipality;
    private String reference;

}
