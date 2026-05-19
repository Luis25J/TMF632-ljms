package mx.att.digital.api.tmf632.application.port.in;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;

/**
 * The interface Party query port.
 */
public interface PartyQueryPort {

    /**
     * Retrieve individual individual.
     *
     * @param id      the id
     * @param segment the segment
     * @return the individual
     */
    Individual retrieveIndividual(String id, String segment);

    /**
     * Retrieve organization organization.
     *
     * @param id      the id
     * @param segment the segment
     * @return the organization
     */
    Organization retrieveOrganization(String id, String segment);


    /**
     * Retrieve user individual tmf 632.
     *
     * @param id the id
     * @return the individual tmf 632
     */
    IndividualTMF632 retrieveUser(String id);

    /**
     * Retrieve user individual tmf 632.
     *
     * @param userName the userName
     * @return the individual tmf 632
     */
    IndividualTMF632 retrieveUserByName(String userName);

}
