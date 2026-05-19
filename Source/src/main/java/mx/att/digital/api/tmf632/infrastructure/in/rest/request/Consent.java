package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Consent.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Consent {

    private String consentId;
    private String consentType;
    private String status;
    private String createdAt;
    private String updatedAt;

}
