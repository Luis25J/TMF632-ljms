package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User connector.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserConnector {

    private String userId;
    private String msisdn;
    private String email;
    private Boolean isPasswordTMP;
    private List<String> interestedTAGs;
    private Boolean verifiedEmail;
    private UserConnectorProfile profile;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    private List<UserConnectorAddress> addresses;
    private List<UserConnectorConsent> consents;
    private List<UserConnectorContact> contacts;

}
