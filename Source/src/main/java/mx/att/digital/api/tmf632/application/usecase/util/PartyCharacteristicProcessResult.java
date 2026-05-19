package mx.att.digital.api.tmf632.application.usecase.util;

import java.util.List;
import java.util.Map;


import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorContact;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorConsent;

/**
 * The type Party characteristic process result.
 */
public record PartyCharacteristicProcessResult(
    List<UserConnectorRequest> lstRequestConsent,
    List<UserConnectorRequest> lstRequestContact,
    List<UserConnectorConsent> lstAllConsents,
    List<UserConnectorContact> lstAllContacts,
    List<Map<String, String>>  lstIdsForRemove
){
}
