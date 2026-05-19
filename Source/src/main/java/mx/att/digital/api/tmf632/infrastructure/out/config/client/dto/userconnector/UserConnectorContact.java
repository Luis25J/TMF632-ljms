package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User connector contact.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConnectorContact {

    private String contactId;
    private String alias;
    private String contactPhone;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

}
