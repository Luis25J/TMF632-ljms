package mx.att.digital.api.tmf632.application.usecase.util;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMediumProcessResult;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorProfile;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The type Execute updates.
 */
@Slf4j
public class ExecuteUpdates {
  private final UserConnectorPort userConnectorClient;

  /**
   * Instantiates a new Execute updates.
   *
   * @param userConnectorClient the user connector client
   */
  public ExecuteUpdates(UserConnectorPort userConnectorClient) {
    this.userConnectorClient = userConnectorClient;
  }

  /**
   * Execute address updates list.
   *
   * @param id            the id
   * @param cmResult      the cm result
   * @param partyResult   the party result
   * @param userConnector the user connector
   * @return the list
   */
  public List<PartialError> executeAddressUpdates(String id,
                                                        ContactMediumProcessResult cmResult,
                                                        PartyCharacteristicProcessResult partyResult,
                                                        UserConnector userConnector) {
    boolean onlyAddresses = !cmResult.lstRequestUserAddress().isEmpty()
        && partyResult.lstRequestConsent().isEmpty()
        && userConnectorEmpty(userConnector);

    if (!onlyAddresses) return List.of();

    List<PartialError> errors = new ArrayList<>();
    for (UserConnectorRequest request : cmResult.lstRequestUserAddress()) {
      String addressId = request.getUser().getAddresses().getFirst().getAddressId();
      UserConnectorResponse resp = userConnectorClient.updateResource(id, addressId, "addresses", request);
      if (!resp.getCodigo().equals("00")) {
        errors.add(buildError(resp.getCodigo(),
            "contactMedium[type: GeographicAddressContactMedium].id: " + addressId));
      }
    }
    return errors;
  }

  /**
   * Execute consent updates list.
   *
   * @param id            the id
   * @param cmResult      the cm result
   * @param partyResult   the party result
   * @param userConnector the user connector
   * @return the list
   */
  public List<PartialError> executeConsentUpdates(String id,
                                                        ContactMediumProcessResult cmResult,
                                                        PartyCharacteristicProcessResult partyResult,
                                                        UserConnector userConnector) {
    boolean onlyConsents = cmResult.lstRequestUserAddress().isEmpty()
        && !partyResult.lstRequestConsent().isEmpty()
        && partyResult.lstRequestContact().isEmpty()
        && userConnectorEmpty(userConnector);

    if (!onlyConsents) return List.of();

    List<PartialError> errors = new ArrayList<>();
    for (UserConnectorRequest request : partyResult.lstRequestConsent()) {
      String consentId = request.getUser().getConsents().getFirst().getConsentId();
      UserConnectorResponse resp = userConnectorClient.updateResource(id, consentId, "consents", request);
      if (!resp.getCodigo().equals("00")) {
        log.error("Error ::: {}", resp);
        errors.add(buildError(resp.getCodigo(),
            "partyCharacteristic[name: consents].consentId: " + consentId));
      }
    }
    return errors;
  }

  /**
   * Execute contact updates list.
   *
   * @param id            the id
   * @param cmResult      the cm result
   * @param partyResult   the party result
   * @param userConnector the user connector
   * @return the list
   */
  public List<PartialError> executeContactUpdates(String id,
                                                        ContactMediumProcessResult cmResult,
                                                        PartyCharacteristicProcessResult partyResult,
                                                        UserConnector userConnector) {
    boolean onlyContacts = cmResult.lstRequestUserAddress().isEmpty()
        && partyResult.lstRequestConsent().isEmpty()
        && !partyResult.lstRequestContact().isEmpty()
        && userConnectorEmpty(userConnector);

    if (!onlyContacts) return List.of();

    List<PartialError> errors = new ArrayList<>();
    for (UserConnectorRequest request : partyResult.lstRequestContact()) {
      String contactId = request.getUser().getContacts().getFirst().getContactId();
      UserConnectorResponse resp = userConnectorClient.updateResource(id, contactId, "contacts", request);
      if (!resp.getCodigo().equals("00")) {
        errors.add(buildError(resp.getCodigo(),
            "partyCharacteristic[name: contact].contactId: " + contactId));
      }
    }
    return errors;
  }

  /**
   * Execute full user update list.
   *
   * @param id            the id
   * @param domain        the domain
   * @param cmResult      the cm result
   * @param partyResult   the party result
   * @param userConnector the user connector
   * @return the list
   */
  public List<PartialError> executeFullUserUpdate(String id,
                                                        IndividualTMF632 domain,
                                                        ContactMediumProcessResult cmResult,
                                                        PartyCharacteristicProcessResult partyResult,
                                                        UserConnector userConnector) {

    if(userConnectorEmpty(userConnector)){
       return List.of();  
    }                          
    
    enrichUserConnectorForFullUpdate(userConnector, domain, cmResult, partyResult);
    UserConnectorRequest userRequest = new UserConnectorRequest();
    userRequest.setUser(userConnector);
    UserConnectorResponse resp = userConnectorClient.updateUser(id, userRequest);

    if (!resp.getCodigo().equals("00")) {
      return List.of(buildError(resp.getCodigo(), "user.id: " + id));
    }
    return List.of();
  }

  /**
   * Enrich user connector for full update.
   *
   * @param userConnector the user connector
   * @param domain        the domain
   * @param cmResult      the cm result
   * @param partyResult   the party result
   */
  public void enrichUserConnectorForFullUpdate(UserConnector userConnector,
                                               IndividualTMF632 domain,
                                               ContactMediumProcessResult cmResult,
                                               PartyCharacteristicProcessResult partyResult) {
    if (!cmResult.lstAllAddress().isEmpty()) {
      userConnector.setAddresses(cmResult.lstAllAddress());
    }
    if (!partyResult.lstAllConsents().isEmpty()) {
      userConnector.setConsents(partyResult.lstAllConsents());
    }
    if (!partyResult.lstAllContacts().isEmpty()) {
      userConnector.setContacts(partyResult.lstAllContacts());
    }

    UserConnectorProfile profile = new UserConnectorProfile();
    if (domain.getBirthDate() != null) {
      profile.setBirthDate(domain.getBirthDate());
    }
    if (domain.getPreferredGivenName() != null) {
      profile.setAlias(domain.getPreferredGivenName());
    }
    userConnector.setProfile(profile);
  }

  /**
   * Execute deletions list.
   *
   * @param id              the id
   * @param addressRemovals the address removals
   * @param charRemovals    the char removals
   * @return the list
   */
  public List<PartialError> executeDeletions(String id,
                                                   List<Map<String, String>> addressRemovals,
                                                   List<Map<String, String>> charRemovals) {
    List<Map<String, String>> allRemovals = new ArrayList<>();
    allRemovals.addAll(addressRemovals);
    allRemovals.addAll(charRemovals);

    log.error("Lista de elementos a borrar:: {}", allRemovals);
    if (allRemovals.isEmpty()) return List.of();

    List<PartialError> errors = new ArrayList<>();
    for (Map<String, String> map : allRemovals) {
      for (Map.Entry<String, String> entry : map.entrySet()) {
        UserConnectorResponse resp = userConnectorClient.deleteResource(id, entry.getValue(), entry.getKey());
        if (!resp.getCodigo().equals("00")) {
          errors.add(buildError(resp.getCodigo(),
              "partyCharacteristic[name: " + entry.getKey() + "]." + entry.getKey() + "Id: " + entry.getValue()));
        }
      }
    }
    return errors;
  }

  private PartialError buildError(String code, String path) {
    PartialError error = new PartialError();
    error.setCode(code);
    error.setMessage("Error al acceder a la base de datos");
    error.setPath(path);
    return error;
  }

  private boolean userConnectorEmpty(UserConnector user) {
    return user.getMsisdn() == null &&
        user.getEmail() == null &&
        user.getCreatedAt() == null &&
        user.getUpdatedAt() == null &&
        user.getInterestedTAGs() == null &&
        user.getVerifiedEmail() == null &&
        user.getIsActive() == null &&
        user.getIsPasswordTMP() == null;
  }
}
