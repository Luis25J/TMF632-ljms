package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * The type String characteristic.
 */
@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StringCharacteristic extends AbstractPartyCharacteristic {

  private String value;
  
}
