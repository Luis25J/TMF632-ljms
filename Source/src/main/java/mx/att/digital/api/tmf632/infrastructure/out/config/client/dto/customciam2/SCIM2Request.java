package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2;

import lombok.Data;

/**
 * The type Scim 2 request.
 */
@Data
public class SCIM2Request {

    private SCIM2Reference reference;
    private SCIM2User user;
}
