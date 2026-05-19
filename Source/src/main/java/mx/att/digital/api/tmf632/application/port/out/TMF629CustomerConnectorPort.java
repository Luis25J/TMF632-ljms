package mx.att.digital.api.tmf632.application.port.out;

import java.util.List;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;


/**
 * The interface Tmf 629 customer connector port.
 */
public interface TMF629CustomerConnectorPort {

    /**
     * Gets user.
     *
     * @param msisdn the msisdn
     * @return the user
     */
    List<TMF629CustomResponse> getUser(String msisdn);

}

