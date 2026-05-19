package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * The type Consent array characteristic.
 */
@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsentArrayCharacteristic extends AbstractPartyCharacteristic {

  private List<Map<String, Consent>> value;

}