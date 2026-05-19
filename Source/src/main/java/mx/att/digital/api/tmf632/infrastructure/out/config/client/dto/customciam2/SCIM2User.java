package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Scim 2 user.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class SCIM2User {

    private String userId;
    private String password;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String consent;
    private String urlPhoto;
    private String numberConsents;
    private String isAdmin;

}
