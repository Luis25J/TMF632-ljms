package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * The type Contact array characteristic.
 */
@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactArrayCharacteristic extends AbstractPartyCharacteristic{

  private List<Map<String, Contact>> value;

}
