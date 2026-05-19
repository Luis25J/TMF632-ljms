package mx.att.digital.api.tmf632.application.usecase.util;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.*;

/**
 * The type User connector util.
 */
public class UserConnectorUtil {


    private UserConnectorUtil(){}

    /**
     * Gets address user.
     *
     * @param request the request
     * @return the address user
     */
    public static List<UserConnectorAddress> getAddressUser(IndividualTMF632 request) {

        String now = OffsetDateTime.now().toString();

        return Optional.ofNullable(request.getContactMedium())
                .orElse(Collections.emptyList())
                .stream()
                .filter(cm -> "GeographicAddressContactMedium".equals(cm.getType()))
                .map(cm -> new UserConnectorAddress(
                        cm.getId(),                                      // addressId
                        cm.getPostCode(),                                // postalCode
                        cm.getMediumCharacteristic() != null 
                            ? cm.getMediumCharacteristic().getNeighborhood()
                            : null,
                        cm.getCity(),                                    // state (modelo actual)
                        cm.getMediumCharacteristic() != null 
                            ? cm.getMediumCharacteristic().getMunicipality()
                            : null,
                        cm.getStreet1(),                                  // streetAndNumber
                        cm.getMediumCharacteristic() != null 
                            ? cm.getMediumCharacteristic().getReference()
                            : null,
                        Boolean.TRUE,                                     // isActive
                        now,
                        now
                ))
                .toList();
    }

    /**
     * Gets consents user.
     *
     * @param request the request
     * @return the consents user
     */
    public static List<UserConnectorConsent> getConsentsUser(IndividualTMF632 request) {
        var timestamp = OffsetDateTime.now().toString();
        return IndividualTMF632Util
            .getConsentsFromRequest(request).stream()
            .map(c -> new UserConnectorConsent(
                c.getConsentId(),
                c.getConsentType(),
                c.getStatus(),
                timestamp,
                timestamp))
            .toList();
    }

    /**
     * Get user connector address request user connector request.
     *
     * @param id        the id
     * @param addresses the addresses
     * @return the user connector request
     */
    public static UserConnectorRequest getUserConnectorAddressRequest(String id, List<UserConnectorAddress> addresses){
           UserConnector userConnector = new UserConnector();
           userConnector.setUserId(id);
           userConnector.setAddresses(addresses);

           return  new UserConnectorRequest(userConnector);

    }

    /**
     * Get user connector consents request user connector request.
     *
     * @param id       the id
     * @param consents the consents
     * @return the user connector request
     */
    public static UserConnectorRequest getUserConnectorConsentsRequest(String id, List<UserConnectorConsent> consents){
        UserConnector userConnector = new UserConnector();
        userConnector.setUserId(id);
        userConnector.setConsents(consents);

        return  new UserConnectorRequest(userConnector);

    }

    /**
     * Get user connector contact request user connector request.
     *
     * @param id       the id
     * @param contacts the contacts
     * @return the user connector request
     */
    public static UserConnectorRequest getUserConnectorContactRequest(String id, List<UserConnectorContact> contacts){
        UserConnector userConnector = new UserConnector();
        userConnector.setUserId(id);
        userConnector.setContacts(contacts);

        return  new UserConnectorRequest(userConnector);

    }

}
