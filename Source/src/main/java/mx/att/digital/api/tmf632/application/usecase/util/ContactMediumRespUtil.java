package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.MediumCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ValidFor;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Contact medium resp util.
 */
public class ContactMediumRespUtil {

    private ContactMediumRespUtil(){}

    /**
     * Build list contact medium list.
     *
     * @param userConnector the user connector
     * @return the list
     */
    public static List<ContactMedium> buildListContactMedium(UserConnector userConnector) {
        List<ContactMedium> contactMedium = new ArrayList<>();
        if(userConnector != null){
            ContactMedium contactMed;
            ValidFor validFor;
            MediumCharacteristic mediumCharacteristic;

            if (userConnector.getMsisdn() != null && !userConnector.getMsisdn().isBlank()) {
                contactMed = new ContactMedium();
                contactMed.setType("PhoneContactMedium");
                contactMed.setPreferred(true);
                contactMed.setMediumType("registerPhone");
                contactMed.setPhoneNumber(userConnector.getMsisdn());
                contactMedium.add(contactMed);
            }

            if (userConnector.getEmail()!= null && !userConnector.getEmail().isBlank()) {
                contactMed = new ContactMedium();
                contactMed.setType("EmailContactMedium");
                contactMed.setPreferred(true);
                contactMed.setMediumType("registerEmail");
                contactMed.setEmailAddress(userConnector.getEmail());
                contactMedium.add(contactMed);
            }

            if (userConnector.getAddresses() != null) {
                for (UserConnectorAddress address : userConnector.getAddresses()) {
                    contactMed = new ContactMedium();
                    contactMed.setType("GeographicAddressContactMedium");
                    contactMed.setId(address.getAddressId());
                    contactMed.setPreferred(true);
                    contactMed.setMediumType("addressList");
                    validFor = new ValidFor();
                    validFor.setEndDateTime(address.getCreatedAt().substring(0, 19)+"Z");
                    contactMed.setValidFor(validFor);
                    contactMed.setCity(address.getState());
                    contactMed.setPostCode(address.getPostalCode());
                    contactMed.setStreet1(address.getStreetAndNumber());
                    mediumCharacteristic = new MediumCharacteristic();
                    mediumCharacteristic.setType("GeographicAddressMX");
                    mediumCharacteristic.setNeighborhood(address.getNeighborhood());
                    mediumCharacteristic.setMunicipality(address.getMunicipality());
                    mediumCharacteristic.setReference(address.getReference());
                    contactMed.setMediumCharacteristic(mediumCharacteristic);
                    contactMedium.add(contactMed);
                }
            }
        }
        return contactMedium;
    }
}
