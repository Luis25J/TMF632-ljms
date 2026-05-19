package mx.att.digital.api.tmf632.application.usecase;

import mx.att.digital.api.tmf632.application.usecase.util.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.application.port.in.PartyQueryPort;
import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.PostpaidConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.PrepaidConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.TMF629CustomerConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;

/**
 * The type Party query use case.
 */
@Slf4j
@Service("partyQueryUseCase")
@Primary
@RequiredArgsConstructor
public class PartyQueryUseCase implements PartyQueryPort {

  private final PrepaidConnectorPort prepaid;
  private final PostpaidConnectorPort postpaid;
  private final CustomSCIM2ConnectorPort customSCIM2Client;
  private final UserConnectorPort userConnectorClient;
  private final TMF629CustomerConnectorPort tmf629Port;
  private static final String STR_PREPAID = "prepaid";
  private static final String STR_POSTPAID = "postpaid";
  private static final String MSISDN_INVALID = "El msisdn no es valido para realizar recargas";
  private static final String MSISDN_NON_EXISTENT = "El msisdn no existe";

  @Override
  public Individual retrieveIndividual(String id, String segment) {
    if (!StringUtils.hasText(id)) {
      log.warn("[USE CASE] Invalid individual id");
      return null;
    }

    if (STR_PREPAID.equalsIgnoreCase(segment)) {
      return safeGet(() -> prepaid.retrieveIndividual(id));
    }
    if (STR_POSTPAID.equalsIgnoreCase(segment)) {
      return safeGet(() -> postpaid.retrieveIndividual(id));
    }

    Individual pre = safeGet(() -> prepaid.retrieveIndividual(id));
    Individual pos = safeGet(() -> postpaid.retrieveIndividual(id));

    if (pre != null && pos == null) {
      return pre;
    }
    if (pre == null && pos != null) {
      return pos;
    }
    if (pre != null && pos != null) {
      return pre; // prepaid wins
    }
    return null;
  }

  @Override
  public Organization retrieveOrganization(String id, String segment) {
    if (!StringUtils.hasText(id)) {
      log.warn("[USE CASE] Invalid organization id");
      return null;
    }

    if (STR_PREPAID.equalsIgnoreCase(segment)) {
      return safeGet(() -> prepaid.retrieveOrganization(id));
    }
    if (STR_POSTPAID.equalsIgnoreCase(segment)) {
      return safeGet(() -> postpaid.retrieveOrganization(id));
    }

    Organization pre = safeGet(() -> prepaid.retrieveOrganization(id));
    Organization pos = safeGet(() -> postpaid.retrieveOrganization(id));

    if (pre != null && pos == null) {
      return pre;
    }
    if (pre == null && pos != null) {
      return pos;
    }
    if (pre != null && pos != null) {
      return pre; // prepaid wins
    }
    return null;
  }


  private <T> T safeGet(SupplierWithException<T> supplier) {
    try {
      return supplier.get();
    } catch (RuntimeException e) {
      log.error("[USE CASE] Downstream connector error", e);
      return null;
    }
  }

  @FunctionalInterface
  private interface SupplierWithException<T> {
      /**
       * Get t.
       *
       * @return the t
       */
      T get();
  }

  @Override
  public  IndividualTMF632 retrieveUser(String id){
          if (!StringUtils.hasText(id)) {
            log.warn("[USE CASE - retrieveUser] Invalid individual id");
            return null;
          }

          SCIM2User userSCIM2 = null;
          UserConnector userConnector = null;
          // 1. Busqueda en SCIM2
          if(id.length() > 30){
            userSCIM2 = customSCIM2Client.retrieveUserInfoById(id);
          }else{
            userSCIM2 = customSCIM2Client.retrieveUserInfoByName(id);
          }
          
          if(userSCIM2 != null){
             userConnector = userConnectorClient.retrieveUserById(userSCIM2.getUserId());
          } else{
            log.warn("[USE CASE - retrieveUser] Invalid individual id");
            return null;
          }      
          
          IndividualTMF632ResponseUtil util = new IndividualTMF632ResponseUtil();
          return util.buildIndividualResponse(userSCIM2, userConnector, null);

  }

  @Override
  public  IndividualTMF632 retrieveUserByName(String userName){
          if (!StringUtils.hasText(userName)) {
            log.warn("[USE CASE - retrieveUserByName] Invalid individual userName");
            return null;
          }

          UserConnector userConnector = null;
          
          SCIM2User userSCIM2 = customSCIM2Client.retrieveUserInfoByName(userName);
          
          if(userSCIM2 != null){
             userConnector = userConnectorClient.retrieveUserById(userSCIM2.getUserId());
          } else{
            log.warn("[USE CASE - retrieveUserByName] Invalid individual userName");
            return null;
          }      
          
          IndividualTMF632ResponseUtil util = new IndividualTMF632ResponseUtil();
          return util.buildIndividualResponse(userSCIM2, userConnector, null);

  }

}    