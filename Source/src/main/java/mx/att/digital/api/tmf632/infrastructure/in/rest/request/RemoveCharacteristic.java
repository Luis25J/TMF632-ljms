package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Remove characteristic.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveCharacteristic  extends AbstractPartyCharacteristic{

    private List<Remove> value;

}
