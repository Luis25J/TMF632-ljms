package mx.att.digital.api.tmf632.application.port.out;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;

/**
 * The interface Prepaid connector port.
 */
public interface PrepaidConnectorPort {

    /**
     * Retrieve individual individual.
     *
     * @param id the id
     * @return the individual
     */
    Individual retrieveIndividual(String id);

    /**
     * Retrieve organization organization.
     *
     * @param id the id
     * @return the organization
     */
    Organization retrieveOrganization(String id);

    /**
     * Retrieve individual by mdn individual.
     *
     * @param mdn the mdn
     * @return the individual
     */
    Individual retrieveIndividualByMdn(String mdn);
}
