package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;


/**
 * The type String array characteristic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StringArrayCharacteristic extends AbstractPartyCharacteristic {

  private List<String> value;
  
}


