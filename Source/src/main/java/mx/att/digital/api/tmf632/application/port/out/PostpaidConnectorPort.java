package mx.att.digital.api.tmf632.application.port.out;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;

/**
 * The interface Postpaid connector port.
 */
public interface PostpaidConnectorPort {

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
     * Retrieve individual by customerdn individual.
     *
     * @param customerdn the customerdn
     * @return the individual
     */
    Individual retrieveIndividualByCustomerdn(String customerdn);
}
