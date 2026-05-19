package mx.att.digital.api.tmf632.application.usecase.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMediumProcessResult;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;

/**
 * The type Process contact medium utils.
 */
public class ProcessContactMediumUtils {
  /**
   * Process contact medium process result.
   *
   * @param requestTMF632 the request tmf 632
   * @param userConnector the user connector
   * @return the contact medium process result
   */
  public ContactMediumProcessResult processContactMedium(IndividualTMF632 requestTMF632, UserConnector userConnector) {

    List<UserConnectorRequest> lstRequestUserAddress = new ArrayList<>();
    List<UserConnectorAddress> lstAllAddress = new ArrayList<>();
    List<Map<String, String>> lstIdsForRemove = new ArrayList<>();

    if (requestTMF632.getContactMedium() == null) {
      return new ContactMediumProcessResult(lstRequestUserAddress, lstAllAddress, lstIdsForRemove);
    }

    for (ContactMedium cm : requestTMF632.getContactMedium()) {
      collectAddressRemoval(cm, lstIdsForRemove);
      mapPhoneOrEmail(cm, userConnector);
      collectGeographicAddress(cm, lstRequestUserAddress, lstAllAddress);
    }

    return new ContactMediumProcessResult(lstRequestUserAddress, lstAllAddress, lstIdsForRemove);
  }

  private void collectAddressRemoval(ContactMedium cm, List<Map<String, String>> lstIdsForRemove) {
    if (cm.getValidFor() != null && cm.getValidFor().getEndDateTime() != null) {
      Map<String, String> map = new HashMap<>();
      map.put("addresses", cm.getId());
      lstIdsForRemove.add(map);
    }
  }

  private void mapPhoneOrEmail(ContactMedium cm, UserConnector userConnector) {
    if ("PhoneContactMedium".equals(cm.getType())) {
      userConnector.setMsisdn(cm.getPhoneNumber());
    }
    if ("EmailContactMedium".equals(cm.getType())) {
      userConnector.setEmail(cm.getEmailAddress());
    }
  }

  private void collectGeographicAddress(ContactMedium cm,
                                        List<UserConnectorRequest> lstRequests,
                                        List<UserConnectorAddress> lstAllAddress) {
    if (!"GeographicAddressContactMedium".equals(cm.getType())) return;

    UserConnectorAddress address = buildAddress(cm);
    lstAllAddress.add(address);

    UserConnector userResource = new UserConnector();
    userResource.setAddresses(List.of(address));
    UserConnectorRequest request = new UserConnectorRequest();
    request.setUser(userResource);
    lstRequests.add(request);
  }

  private UserConnectorAddress buildAddress(ContactMedium cm) {
    UserConnectorAddress address = new UserConnectorAddress();
    address.setAddressId(cm.getId());
    address.setState(cm.getCity());
    address.setPostalCode(cm.getPostCode());
    address.setStreetAndNumber(cm.getStreet1());
    if(cm.getMediumCharacteristic() != null){
      address.setNeighborhood(cm.getMediumCharacteristic().getNeighborhood());
      address.setMunicipality(cm.getMediumCharacteristic().getMunicipality());
      address.setReference(cm.getMediumCharacteristic().getReference());
    }
    return address;
  }
}
