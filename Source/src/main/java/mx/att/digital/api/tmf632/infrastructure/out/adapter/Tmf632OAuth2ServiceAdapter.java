package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.Tmf632OAuth2ServiceAdapterPort;
import mx.att.digital.api.tmf632.domain.constants.Constants;
import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.oauth.CachedOAuthToken;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.oauth.OAuthTokenRestClientResponse;
import mx.att.digital.api.tmf632.infrastructure.exception.ResponseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The type Tmf 632 o auth 2 service adapter.
 */
@Component
@Slf4j
public class Tmf632OAuth2ServiceAdapter implements Tmf632OAuth2ServiceAdapterPort {

    private RestTemplate restTemplate632;
    private AtomicReference<CachedOAuthToken> cachedToken;

    @Value("${tmf632.wso2-token.base-url}")
    private String urlTokenApi;

    @Value("${tmf632.wso2-token.customerId}")
     private String username;

    @Value("${tmf632.wso2-token.customerKey}")
    private String password;

    private ObjectMapper mapper = new ObjectMapper();


    /**
     * Instantiates a new Tmf 632 o auth 2 service adapter.
     *
     * @param restTemplate632 the rest template 632
     */
    @Autowired
  public Tmf632OAuth2ServiceAdapter(
         @Qualifier("restTemplate632") RestTemplate restTemplate632) {
         this.restTemplate632 = restTemplate632;
  }

  @Override
  public String getValidAccessToken() {
    cachedToken = restClientResponse();
    return cachedToken.get().getAccessToken();
  }

    /**
     * Rest client response atomic reference.
     *
     * @return the atomic reference
     */
    public AtomicReference<CachedOAuthToken> restClientResponse() {
         HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set("Authorization", createBasicAuthHeader());
            
            MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
                bodyParamMap.add("grant_type", "client_credentials");
    try {

          HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyParamMap, headers);
                        
          ResponseEntity<String> respEntity = restTemplate632.exchange(urlTokenApi, HttpMethod.POST, 
                                              requestEntity, String.class);
            
            OAuthTokenRestClientResponse response = mapper.readValue(respEntity.getBody(), 
                                                              OAuthTokenRestClientResponse.class); 
       
      if (response != null) {
        CachedOAuthToken cached = new CachedOAuthToken(response);
        cached.setAccessToken(response.accessToken());
        cached.setExpiresIn(Instant.ofEpochSecond(response.expiresIn()));
        return new AtomicReference<>(cached);
      } else {
        log.error("Error mapping response params because is null");
        throw new ResponseException(BuilderErrorEnum.OAUTH_NOT_AVAILABLE, "response is null");
      }

    } catch (RestClientResponseException e) {
      if (e.getStatusCode().value() == 404) {
        log.error("Not Found {}", e.getStatusCode().value());
        log.error("Exception catch ", e);
        throw new ResponseException(BuilderErrorEnum.OAUTH_NOT_FOUND, e.getMessage());
      }
      log.error("Error HTTP call {}", e.getStatusCode().value());
      log.error("Exception ", e);
      throw new ResponseException(BuilderErrorEnum.OAUTH_NOT_AVAILABLE, e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error calling endpoint ", e);
      throw new ResponseException(BuilderErrorEnum.OAUTH_NOT_AVAILABLE, e.getMessage());
    }
  }

  private String createBasicAuthHeader() {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth);
  }
}
