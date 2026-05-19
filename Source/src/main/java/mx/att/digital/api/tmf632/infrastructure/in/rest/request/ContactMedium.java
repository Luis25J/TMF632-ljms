package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

/**
 * The type Contact medium.
 */
@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactMedium {

       private String id;
       @JsonProperty("@type")
       private String type;
       private Boolean preferred;
       private String mediumType;
       private String phoneNumber;
       private String emailAddress;
       private ValidFor validFor;
       private String city;
       private String postCode;
       private String street1;
       private MediumCharacteristic mediumCharacteristic;


}

