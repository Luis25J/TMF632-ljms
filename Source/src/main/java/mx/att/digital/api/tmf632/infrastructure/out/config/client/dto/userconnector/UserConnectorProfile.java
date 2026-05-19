package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User connector profile.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConnectorProfile {
    /**
     * The Birth date.
     */
    private String birthDate;
    /**
     * The Alias.
     */
    private String alias;

}
