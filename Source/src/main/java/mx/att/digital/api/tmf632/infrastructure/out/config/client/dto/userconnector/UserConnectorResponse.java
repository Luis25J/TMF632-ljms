package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type User connector response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserConnectorResponse {

    private String codigo;
    private String mensaje;
    private String userId;

}
