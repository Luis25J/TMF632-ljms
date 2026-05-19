package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.TMF629CustomerConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * The type Custom tmf 629 connector client.
 */
@Slf4j
@Component
public class CustomTMF629ConnectorClient implements TMF629CustomerConnectorPort {

    private final RestTemplate restTemplate632;

    @Value("${tmf632.tmf629-connector.base-url}")
    private String baseUrl;

    private static final String TMF629_SOURCE = "SuperApp";


    /**
     * Instantiates a new Custom tmf 629 connector client.
     *
     * @param restTemplate632 the rest template 632
     */
    public CustomTMF629ConnectorClient(@Qualifier("restTemplate632") RestTemplate restTemplate632) {
        this.restTemplate632 = restTemplate632;
    }    


    @Override
    public List<TMF629CustomResponse> getUser(String msisdn) {
        log.info("[" + msisdn + "][TMF629] Calling TMF629 service getUser URL={} id={}", baseUrl, msisdn);

        try {
             String url = baseUrl +
                "?msisdn=" +
                msisdn +
                "&source=" + TMF629_SOURCE
                ;

            TMF629CustomResponse[] response =
                    restTemplate632.getForObject(url,TMF629CustomResponse[].class);

            return Arrays.asList(response);

        } catch (Exception e) {
            log.error("[" + msisdn + "][TMF629] Error calling putUserById url={} id={}", baseUrl, msisdn, e);
        }
        return List.of();
    }

}
