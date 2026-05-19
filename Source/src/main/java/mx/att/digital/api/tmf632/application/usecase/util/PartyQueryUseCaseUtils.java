package mx.att.digital.api.tmf632.application.usecase.util;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMediumProcessResult;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The type Party query use case utils.
 */
@Slf4j
public class PartyQueryUseCaseUtils {
  private final ProcessContactMediumUtils processContactMediumUtils;
  private final ProcessPartyCharacteristicsUtils processPartyCharacteristicsUtils;
  private final ExecuteUpdates executeUpdates;

    /**
     * Instantiates a new Party query use case utils.
     *
     * @param processContactMediumUtils        the process contact medium utils
     * @param processPartyCharacteristicsUtils the process party characteristics utils
     * @param executeUpdates                   the execute updates
     */
    public PartyQueryUseCaseUtils(ProcessContactMediumUtils processContactMediumUtils,
                                ProcessPartyCharacteristicsUtils processPartyCharacteristicsUtils,
                                ExecuteUpdates executeUpdates) {
    this.processContactMediumUtils = processContactMediumUtils;
    this.processPartyCharacteristicsUtils = processPartyCharacteristicsUtils;
    this.executeUpdates = executeUpdates;
  }

    /**
     * Update extended database list.
     *
     * @param id                the id
     * @param individualRequest the individual request
     * @return the list
     * @throws JsonProcessingException 
     * @throws JsonMappingException 
     */
    public List<PartialError> updateExtendedDatabase(String id, IndividualTMF632 individualRequest){

        

         UserConnector userConnector = new UserConnector();


           ContactMediumProcessResult contactMediumResult = processContactMediumUtils.processContactMedium
                                                           (individualRequest, userConnector);
         

           PartyCharacteristicProcessResult partyResult = processPartyCharacteristicsUtils.processPartyCharacteristics(
                                                          individualRequest, userConnector);


        List<PartialError> errors = new ArrayList<>();

        errors.addAll(executeUpdates.executeAddressUpdates(id, contactMediumResult, partyResult, userConnector));
        errors.addAll(executeUpdates.executeConsentUpdates(id, contactMediumResult, partyResult, userConnector));
        errors.addAll(executeUpdates.executeContactUpdates(id, contactMediumResult, partyResult, userConnector));
        errors.addAll(executeUpdates.executeFullUserUpdate(
            id, individualRequest, contactMediumResult, partyResult, userConnector));
        errors.addAll(executeUpdates.executeDeletions(
            id, contactMediumResult.lstIdsForRemove(), partyResult.lstIdsForRemove()));

        return errors;
  }
}
