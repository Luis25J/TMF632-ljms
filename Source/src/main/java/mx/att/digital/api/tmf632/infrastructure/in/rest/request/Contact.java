package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * The type Contact.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Contact {

    private String contactId;
    private String alias;
    private String contactPhone;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

}
