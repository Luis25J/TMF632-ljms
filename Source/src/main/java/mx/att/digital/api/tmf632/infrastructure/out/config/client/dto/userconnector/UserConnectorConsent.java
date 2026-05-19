package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User connector consent.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConnectorConsent {

    private String consentId;
    private String consentType;
    private String status;
    private String createdAt;
    private String updatedAt;

}
