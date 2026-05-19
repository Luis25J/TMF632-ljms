package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User connector address.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConnectorAddress {

    private String addressId;
    private String postalCode;
    private String neighborhood;
    private String state;
    private String municipality;
    private String streetAndNumber;
    private String reference;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

}
