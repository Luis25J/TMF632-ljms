package mx.att.digital.api.tmf632.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.in.PartyUpdatePort;
import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.application.usecase.util.*;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.util.List;

/**
 * The type Party update use case.
 */
@Slf4j
@Service("partyUpdateUseCase")
@Primary
@RequiredArgsConstructor
public class PartyUpdateUseCase implements PartyUpdatePort {

    private final CustomSCIM2ConnectorPort customSCIM2Client;
    private final UserConnectorPort userConnectorClient;

    @Override
    public IndividualTMF632 updateUser(String id, IndividualTMF632 request){
        PartyQueryUseCaseUtils utils = new PartyQueryUseCaseUtils(new ProcessContactMediumUtils(),
                new ProcessPartyCharacteristicsUtils(),
                new ExecuteUpdates(userConnectorClient));

        if (!StringUtils.hasText(id)) {
            log.warn("[USE CASE - updateUser] Invalid individual id");
            return null;
        }

        // 1. Actualizar en CIAM
        SCIM2User actualCIM2 = customSCIM2Client.retrieveUserInfoById(id);
        SCIM2User requestCIM2 = SCIAM2Util.buildUserToUpdate(request, actualCIM2);
        SCIM2User userCIM2response = customSCIM2Client.updateUserById(id, requestCIM2);

        // 2. Actualizar en DB extendida (sólo si el usuario existe)
        UserConnector userConnActual = userConnectorClient.retrieveUserById(id);
        List<PartialError> lstErrors = null;
        if (userConnActual != null) {
                lstErrors = utils.updateExtendedDatabase(id, request);
        }

        return new IndividualTMF632ResponseUtil().buildIndividualResponse(userCIM2response,
                userConnectorClient.retrieveUserById(id), lstErrors);
    }
}
