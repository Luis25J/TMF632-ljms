package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;
import java.util.Map;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;


/**
 * The type Contact medium process result.
 */
public record ContactMediumProcessResult(
    List<UserConnectorRequest> lstRequestUserAddress,
    List<UserConnectorAddress> lstAllAddress,
    List<Map<String, String>>  lstIdsForRemove
) {
}
