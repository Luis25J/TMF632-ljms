package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

/**
 * The type Object array characteristic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectArrayCharacteristic extends AbstractPartyCharacteristic {

  private List<Map<String, Object>> value;

}
