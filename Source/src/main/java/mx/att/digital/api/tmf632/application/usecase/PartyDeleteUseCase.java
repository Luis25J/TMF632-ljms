package mx.att.digital.api.tmf632.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.in.PartyDeletePort;
import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2UserDelete;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The type Party delete use case.
 */
@Slf4j
@Service("partyDeleteUseCase")
@Primary
@RequiredArgsConstructor
public class PartyDeleteUseCase implements PartyDeletePort {

    private final CustomSCIM2ConnectorPort customSCIM2Client;
    private final UserConnectorPort userConnectorClient;

    @Override
    public IndividualTMF632 deleteUser(String id){
        if (!StringUtils.hasText(id)) {
            log.warn("[USE CASE] Invalid individual id");
            return null;
        }
        SCIM2UserDelete resp;
        if (id.length() < 30) {
            resp = customSCIM2Client.deleteUserById(customSCIM2Client.retrieveUserInfoById(id).getUserId());
        } else {
            resp = customSCIM2Client.deleteUserById(id);
        }

        IndividualTMF632 individual = new IndividualTMF632();
        if(resp != null && resp.getDeleted()){
            individual.setId(resp.getUserId());
            individual.setStatus("inactive");
        }
        // Se elimina en DB - userConnector
        UserConnectorResponse userConnResp = userConnectorClient.deleteUser(id);
        if(userConnResp != null){
            log.error("Delete User-Connector Response:: " + userConnResp );
        }

        return individual;
    }

}
