package mx.att.digital.api.tmf632.application.usecase.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.AbstractPartyCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Consent;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Contact;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ObjectArrayCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Remove;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.RemoveCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.RemoveItem;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.StringArrayCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorConsent;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorContact;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;


/**
 * The type Process party characteristics utils.
 */
@Slf4j
public class ProcessPartyCharacteristicsUtils {

  private ObjectMapper mapper;

    /**
     * Process party characteristics party characteristic process result.
     *
     * @param individualRequest the individual request
     * @param userConnector     the user connector
     * @return the party characteristic process result
     */
    public PartyCharacteristicProcessResult processPartyCharacteristics(IndividualTMF632 individualRequest,
                                                                      UserConnector userConnector) {
    List<UserConnectorRequest> lstRequestConsent = new ArrayList<>();
    List<UserConnectorRequest> lstRequestContact = new ArrayList<>();
    List<UserConnectorConsent> lstAllConsents = new ArrayList<>();
    List<UserConnectorContact> lstAllContacts = new ArrayList<>();
    List<Map<String, String>> lstIdsForRemove = new ArrayList<>();

    PartyCharacteristicProcessResult partyCharacteristicProcessResult = new PartyCharacteristicProcessResult(
        lstRequestConsent, lstRequestContact, lstAllConsents, lstAllContacts, lstIdsForRemove);

    if (individualRequest.getPartyCharacteristic() == null) {
        return partyCharacteristicProcessResult;
    }

    for (AbstractPartyCharacteristic ch : individualRequest.getPartyCharacteristic()) {
         processCharacteristic(ch, userConnector, lstRequestConsent, lstRequestContact,
                                     lstAllConsents, lstAllContacts, lstIdsForRemove);
    }

    return partyCharacteristicProcessResult;
  }

  private void processCharacteristic(AbstractPartyCharacteristic ch,
                                     UserConnector userConnector,
                                     List<UserConnectorRequest> lstRequestConsent,
                                     List<UserConnectorRequest> lstRequestContact,
                                     List<UserConnectorConsent> lstAllConsents,
                                     List<UserConnectorContact> lstAllContacts,
                                     List<Map<String, String>> lstIdsForRemove) {
      switch (ch.getName()) {
          case "interestingItem" -> userConnector.setInterestedTAGs(((StringArrayCharacteristic) ch).getValue());
          case "consents" -> processConsentCharacteristic(ch, lstRequestConsent, lstAllConsents, lstIdsForRemove);
          case "contacts" -> processContactCharacteristic(ch, lstRequestContact, lstAllContacts, lstIdsForRemove);
          default -> log.info("Not found Characteristics: {}", ch.getName());
      }
  }

  private void processConsentCharacteristic(AbstractPartyCharacteristic ch,
                                            List<UserConnectorRequest> lstRequests,
                                            List<UserConnectorConsent> lstAllConsents,
                                            List<Map<String, String>> lstIdsForRemove) {

          Consent consent;
          RemoveCharacteristic removeCh;
          mapper = new ObjectMapper();

          for (Map<String, Object> map : ((ObjectArrayCharacteristic) ch).getValue()){
              if(map.containsKey("@op")){
                  Remove remove = mapper.convertValue(map, Remove.class);
                  removeCh = new RemoveCharacteristic();
                  removeCh.setType("RemoveCharacteristic");
                  removeCh.setName(ch.getName());
                  removeCh.setValue(Collections.singletonList(remove));

                  collectRemovals(removeCh, "consents",
                        item -> item.getConsentId(), lstIdsForRemove);
                  return;
             }
             else{  
                  consent = mapper.convertValue(map, Consent.class);
                  UserConnectorConsent consentUserConnector = buildConsent(consent);
                  lstAllConsents.add(consentUserConnector);
                  lstRequests.add(buildRequestWithConsent(consentUserConnector));
             }
          }
  }


  private void processContactCharacteristic(AbstractPartyCharacteristic ch,
                                            List<UserConnectorRequest> lstRequests,
                                            List<UserConnectorContact> lstAllContacts,
                                            List<Map<String, String>> lstIdsForRemove) {

    Contact contact;
    RemoveCharacteristic removeCh;
    mapper = new ObjectMapper();

    for (Map<String, Object> map : ((ObjectArrayCharacteristic) ch).getValue()){
        if(map.containsKey("@op")){
           Remove remove = mapper.convertValue(map, Remove.class);
           removeCh = new RemoveCharacteristic();
           removeCh.setType("RemoveCharacteristic");
           removeCh.setName(ch.getName());
           removeCh.setValue(Collections.singletonList(remove));

           collectRemovals(removeCh, "contacts",
                   item -> item.getContactId(), lstIdsForRemove);
          return;
        }
        else{
          contact = mapper.convertValue(map, Contact.class);
          UserConnectorContact contactUserConnector = buildContact(contact);
          lstAllContacts.add(contactUserConnector);
          lstRequests.add(buildRequestWithContact(contactUserConnector));
        }
      }

  }

  private void collectRemovals(RemoveCharacteristic ch, String resourceType,
                               java.util.function.Function<RemoveItem, String> idExtractor,
                               List<Map<String, String>> lstIdsForRemove) {
    for (Remove removeDom : ch.getValue()) {
      for (RemoveItem item : removeDom.getItems()) {
        Map<String, String> map = new HashMap<>();
        map.put(resourceType, idExtractor.apply(item));
        lstIdsForRemove.add(map);
      }
    }
  }

  private UserConnectorConsent buildConsent(Consent cd) {
          UserConnectorConsent consent = new UserConnectorConsent();
          consent.setConsentId(cd.getConsentId());
          consent.setConsentType(cd.getConsentType());
          consent.setStatus(cd.getStatus());
          consent.setCreatedAt(cd.getCreatedAt());
          consent.setUpdatedAt(cd.getUpdatedAt());
          return consent;
  }

  private UserConnectorContact buildContact(Contact dd) {
          UserConnectorContact contact = new UserConnectorContact();
          contact.setContactId(dd.getContactId());
          contact.setAlias(dd.getAlias());
          contact.setContactPhone(dd.getContactPhone());
          contact.setIsActive(dd.getIsActive());
          contact.setCreatedAt(dd.getCreatedAt());
          contact.setUpdatedAt(dd.getUpdatedAt());
          return contact;
  }

  private UserConnectorRequest buildRequestWithConsent(UserConnectorConsent consent) {
          UserConnector resource = new UserConnector();
          resource.setConsents(List.of(consent));
          UserConnectorRequest request = new UserConnectorRequest();
          request.setUser(resource);
          return request;
  }

  private UserConnectorRequest buildRequestWithContact(UserConnectorContact contact) {
    UserConnector resource = new UserConnector();
    resource.setContacts(List.of(contact));
    UserConnectorRequest request = new UserConnectorRequest();
    request.setUser(resource);
    return request;
  }
}
