package mx.att.digital.api.tmf632.application.port.in;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;

/**
 * The interface Party delete port.
 */
public interface PartyDeletePort {

    /**
     * Delete user individual tmf 632.
     *
     * @param id the id
     * @return the individual tmf 632
     */
    IndividualTMF632 deleteUser(String id);

}
