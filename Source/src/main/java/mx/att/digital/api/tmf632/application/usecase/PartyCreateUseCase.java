package mx.att.digital.api.tmf632.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.in.PartyCreatePort;
import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.TMF629CustomerConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632Util;
import mx.att.digital.api.tmf632.application.usecase.util.SCIAM2Util;
import mx.att.digital.api.tmf632.application.usecase.util.Tmf629Util;
import mx.att.digital.api.tmf632.application.usecase.util.UserConnectorUtil;
import mx.att.digital.api.tmf632.infrastructure.exception.ValidationResponseException;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Contact;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.StringCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Request;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Response;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Party create use case.
 */
@Slf4j
@Service("partyCreateUseCase")
@Primary
@RequiredArgsConstructor
public class PartyCreateUseCase implements PartyCreatePort {

    private final CustomSCIM2ConnectorPort customSCIM2Client;
    private final UserConnectorPort userConnectorClient;
    private final TMF629CustomerConnectorPort tmf629Port;
    private static final String STR_PREPAID = "prepaid";
    private static final String STR_POSTPAID = "postpaid";
    private static final String MSISDN_INVALID = "El msisdn no es valido para realizar recargas";
    private static final String MSISDN_NON_EXISTENT = "El msisdn no existe";

    @Override
    public IndividualTMF632 createUser(String role, IndividualTMF632 request){
        List<UserConnectorContact> lstContactsUserConnector = null;
        List<PartialError> lstPartialErrors = null;
        String now = OffsetDateTime.now().toString();

        IndividualTMF632 tmf632Response = request;

        //3.user-connector - Create in DB
        // Conditionals for characteristics(address, contacts, consents)
        if(request.getId() != null && !request.getId().isBlank()){
            // 3.2 - address
            List<UserConnectorAddress> addresses = UserConnectorUtil.getAddressUser(request);
            if(addresses != null && !addresses.isEmpty()){
                UserConnectorResponse addressesUserResponse = userConnectorClient.addAddressUser
                        (UserConnectorUtil.getUserConnectorAddressRequest(request.getId(), addresses));
            }

            // 3.3 - consents
            List<UserConnectorConsent> consents = UserConnectorUtil.getConsentsUser(request);
            if(consents != null && !consents.isEmpty()){
                UserConnectorResponse consentsUserResponse = userConnectorClient.addConsentsUser
                        (UserConnectorUtil.getUserConnectorConsentsRequest(request.getId(), consents));
            }

            // 3.4 - contacts
            lstContactsUserConnector = new ArrayList<>();
            lstPartialErrors = new ArrayList<>();
            evaluateContacts(request, lstContactsUserConnector, lstPartialErrors, now);
            tmf632Response.setPartialErrors(lstPartialErrors);
            if(!lstContactsUserConnector.isEmpty()){
                UserConnectorResponse contactsUserResponse = userConnectorClient.addContactsUser
                        (UserConnectorUtil.getUserConnectorContactRequest(request.getId(), lstContactsUserConnector));
            }
        }else {
            // 1.CIAM - creacion / rollback
            SCIM2Request ciamRequest = SCIAM2Util.getCIAMRequestToCreate(request);
            SCIM2Response ciamResponse = customSCIM2Client.addUser(ciamRequest);

            // Se crea caracteristica userName y se agrega a response
            StringCharacteristic userNameCh = new StringCharacteristic();
            userNameCh.setType("StringCharacteristic");
            userNameCh.setValueType("string");
            userNameCh.setName("userName");
            userNameCh.setValue(ciamResponse.getUser().getUserName());
            tmf632Response.getPartyCharacteristic().add(userNameCh);

            //Extract contacts from request to iterate and evaluate by TMF629
            lstContactsUserConnector = new ArrayList<>();
            lstPartialErrors = new ArrayList<>();

            evaluateContacts(request, lstContactsUserConnector, lstPartialErrors, now);
            tmf632Response.setPartialErrors(lstPartialErrors);
            //3.user-connector - Create in DB
            UserConnectorProfile profile = new UserConnectorProfile(request.getBirthDate(),
                    request.getPreferredGivenName());
            UserConnectorRequest userConnectorRequest = new UserConnectorRequest(
                    new UserConnector(
                            ciamResponse.getUser().getUserId(),
                            ciamRequest.getUser().getPhone(),
                            ciamRequest.getUser().getEmail(),
                            false,
                            new ArrayList<>(),
                            false,
                            profile,
                            true,
                            now,
                            now,
                            UserConnectorUtil.getAddressUser(request),
                            UserConnectorUtil.getConsentsUser(request),
                            lstContactsUserConnector
                    )
            );

            UserConnectorResponse userConnectorResponse = userConnectorClient.createUser(userConnectorRequest);

            if(userConnectorResponse != null){
                tmf632Response.setId(ciamResponse.getUser().getUserId());
            }else{
                customSCIM2Client.deleteUserById(ciamResponse.getUser().getUserId());
                throw new ValidationResponseException("01", MSISDN_NON_EXISTENT, "");
            }
        }

        return tmf632Response;
    }


    /**
     * Evaluate contacts.
     *
     * @param request                  the request
     * @param lstContactsUserConnector the lst contacts user connector
     * @param lstPartialErrors         the lst partial errors
     * @param now                      the now
     */
    private void evaluateContacts(IndividualTMF632 request, List<UserConnectorContact> lstContactsUserConnector,
                                  List<PartialError> lstPartialErrors, String now){

        List<Contact> lstContacts = IndividualTMF632Util.getContactsFromRequest(request);
        for (Contact aContact : lstContacts) {
            List<TMF629CustomResponse> tmf629CustomResponses = tmf629Port.getUser(aContact.getContactPhone());
            boolean tmf629IsEmpty=true;
            if(tmf629CustomResponses != null && !tmf629CustomResponses.isEmpty()){
                tmf629IsEmpty = tmf629CustomResponses.getFirst().getCharacteristic() == null;
            }

            //IF -      Evaluate response from TMF629, if contact list is empty add Partial error
            if(tmf629IsEmpty){
                PartialError partialErrorDomain = new PartialError();
                partialErrorDomain.setPath("partyCharacteristic[name: " + aContact.getAlias() + "]");
                partialErrorDomain.setCode("01");
                partialErrorDomain.setMessage(MSISDN_NON_EXISTENT);

                lstPartialErrors.add(partialErrorDomain);

                //ELSE IF-  Evaluate response from TMF629, if contact list is NOT empty
                //           but it does not match with filters add Partial error
            }
            else if(!Tmf629Util.isValidPaymentCategory(tmf629CustomResponses)){
                PartialError partialErrorDomain = new PartialError();
                partialErrorDomain.setPath("partyCharacteristic[name: " + aContact.getAlias() + "]");
                partialErrorDomain.setCode("01");
                partialErrorDomain.setMessage(MSISDN_INVALID);

                lstPartialErrors.add(partialErrorDomain);

                //ELSE -    Add the current contact to the new list to can registe in User Connector later...
            }
            else {
                lstContactsUserConnector.add( new UserConnectorContact(
                        aContact.getContactId(),
                        aContact.getAlias(),
                        aContact.getContactPhone(),
                        Boolean.TRUE.equals(aContact.getIsActive()),
                        now,
                        now));
            }
        }
    }
}
