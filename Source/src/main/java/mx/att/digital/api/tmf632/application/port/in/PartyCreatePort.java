package mx.att.digital.api.tmf632.application.port.in;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;

/**
 * The interface Party create port.
 */
public interface PartyCreatePort {

    /**
     * Create user individual tmf 632.
     *
     * @param id                the id
     * @param individualRequest the individual request
     * @return the individual tmf 632
     */
    IndividualTMF632 createUser(String id, IndividualTMF632 individualRequest);

}
