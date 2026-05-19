package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.exception.ConnectorRequestException;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


/**
 * The type User connector client.
 */
@Slf4j
@Component
public class UserConnectorClient implements UserConnectorPort {

  private final RestTemplate restTemplate632;

  @Value("${tmf632.user-connector.base-url}")
  private String baseUrl;

  @Value("${tmf632.user-connector.username}")
  private String username;

  @Value("${tmf632.user-connector.password}")
  private String password;

  private ObjectMapper mapper = new ObjectMapper();

  private static final String HEADER_AUTHORIZATION = "Authorization";
  private static final String ERROR_MESSAGE = "[{}] [UserConnector]  Error calling user connector url={}";

  /**
   * Instantiates a new User connector client.
   *
   * @param restTemplate632 the rest template 632
   */
  public UserConnectorClient(@Qualifier("restTemplate632") RestTemplate restTemplate632) {
    this.restTemplate632 = restTemplate632;
  }

  @Override
  public UserConnector retrieveUserById(String id) {
    log.info("[{}] [UserConnector] Calling UserConnector service retrieveUserById URL={}",
        Encode.forJava(id), Encode.forJava(baseUrl));

    try {
      String urlUserConnector = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id)
          .encode()
          .toUriString();
      ResponseEntity<String> respEntity = restTemplate632.exchange(urlUserConnector,
          HttpMethod.GET, this.buildEntity(), String.class);

      log.info("RESP retrieveUserById::: {}", Encode.forJava(respEntity.getBody()));

      UserConnectorRequest response = mapper.readValue(respEntity.getBody(), UserConnectorRequest.class);

      return response.getUser();


    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl), e);
      return null;
    }
  }

  @Override
  public UserConnectorResponse updateUser(String id, UserConnectorRequest request) {
    log.info("[{}] [UserConnector] Calling UserConnector service updateUser URL={}",
        Encode.forJava(id), Encode.forJava(baseUrl));

    try {
      String urlUserConnector = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id)
          .encode()
          .toUriString();

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlUserConnector,
          HttpMethod.PATCH, this.buildEntityWithRequest(request), String.class);

      log.info("RESP updateUser:::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl), e);
      throw new ConnectorRequestException(
          "Failed to updateUser from UserConnector",
          "url=" + baseUrl + ", userName=" + id, e);
    }
  }

  @Override
  public UserConnectorResponse updateResource(String id, String idResource, String typeResource,
                                              UserConnectorRequest request) {

    log.info("[{}] [UserConnector] Calling UserConnector service updateResource " +
            "idResource={}, typeResource={}, URL={}", Encode.forJava(id), Encode.forJava(idResource),
        Encode.forJava(typeResource), Encode.forJava(baseUrl));

    try {
      String urlUserConnector = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id, typeResource, idResource)
          .encode()
          .toUriString();
      ResponseEntity<String> respEntity = restTemplate632.exchange(urlUserConnector,
          HttpMethod.PATCH, this.buildEntityWithRequest(request), String.class);

      log.info("RESP updateResource:::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error("[{}] [UserConnector] Calling UserConnector service updateResource " +
              "idResource={}, typeResource={}, URL={}", Encode.forJava(id), Encode.forJava(idResource),
          Encode.forJava(typeResource), Encode.forJava(baseUrl), e);
      return new UserConnectorResponse("02", e.getMessage(), id);
    }
  }

  @Override
  public UserConnectorResponse createUser(UserConnectorRequest request) {
    log.info("[{}] [UserConnector] Calling UserConnector service createUser URL={}",
        Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl));
    try {

      ResponseEntity<String> respEntity = restTemplate632.exchange(baseUrl,
          HttpMethod.POST, this.buildEntityWithRequest(request), String.class);

      log.info("RESP createUser::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl), e);
    }
    return null;
  }

  @Override
  public UserConnectorResponse deleteUser(String id) {
    log.info("[{}] [UserConnector] Calling UserConnector service deleteUserById URL={}",
        Encode.forJava(id), Encode.forJava(baseUrl));

    try {
      String urlUserConnector = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id)
          .encode()
          .toUriString();

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlUserConnector,
          HttpMethod.DELETE, this.buildEntity(), String.class);

      log.info("$RESP deleteUser::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl), e);
    }
    return null;
  }

  @Override
  public UserConnectorResponse deleteResource(String id, String idResource, String typeResource) {
    log.info("[{}] [UserConnector] Calling UserConnector service deleteUserById URL={}",
        Encode.forJava(id), Encode.forJava(baseUrl));
    try {
      String urlUserConnector = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id, typeResource, idResource)
          .toUriString().replace("%20", " ");

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlUserConnector,
          HttpMethod.DELETE, this.buildEntity(), String.class);

      log.info("$RESP deleteResource {}:::: {}",
          Encode.forJava(typeResource), Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl), e);
      return new UserConnectorResponse("02", e.getMessage(), id);
    }
  }

  private String createBasicAuthHeader() {
    String auth = username + ":" + password;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    return "Basic " + new String(encodedAuth);
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(HEADER_AUTHORIZATION, createBasicAuthHeader());
    return headers;
  }

  private HttpEntity<String> buildEntity() {
    return new HttpEntity<>(getHeaders());
  }

  private HttpEntity<String> buildEntityWithRequest(UserConnectorRequest request) throws JsonProcessingException {
    return new HttpEntity<>(mapper.writeValueAsString(request), getHeaders());
  }

  @Override
  public UserConnectorResponse addContactsUser(UserConnectorRequest request) {
    log.info("[{}] [UserConnector] Calling UserConnector service createContacts URL={}",
        Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl));
    try {
      String contactsURL = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(request.getUser().getUserId(), "contacts")
          .encode()
          .toUriString();
      ResponseEntity<String> respEntity = restTemplate632.exchange(contactsURL,
          HttpMethod.POST, this.buildEntityWithRequest(request), String.class);

      log.info("RESP addContactsUser ::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl), e);
      throw new ConnectorRequestException(
          "Failed to createUser from UserConnector ",
          "url=" + baseUrl, e);
    }
  }

  @Override
  public UserConnectorResponse addConsentsUser(UserConnectorRequest request) {
    log.info("[{}] [UserConnector] Calling UserConnector service createConsents URL={}",
        Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl));
    try {
      String consentsURL = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(request.getUser().getUserId(), "consents")
          .encode()
          .toUriString();
      ResponseEntity<String> respEntity = restTemplate632.exchange(consentsURL,
          HttpMethod.POST, this.buildEntityWithRequest(request), String.class);

      log.info("RESP addConsentsUser::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.error(ERROR_MESSAGE,
          Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl), e);
      throw new ConnectorRequestException(
          "Failed to createUser from UserConnector ",
          "url=" + baseUrl, e);
    }
  }

  @Override
  public UserConnectorResponse addAddressUser(UserConnectorRequest request) {
    log.info("[{}] [UserConnector] Calling UserConnector service createAddress URL={}",
        Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl));
    try {
      String addressURL = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(request.getUser().getUserId(), "addresses")
          .encode()
          .toUriString();

      ResponseEntity<String> respEntity = restTemplate632.exchange(addressURL,
          HttpMethod.POST, this.buildEntityWithRequest(request), String.class);

      log.info("RESP addAddressUser::: {}", Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), UserConnectorResponse.class);

    } catch (Exception e) {
      log.info(ERROR_MESSAGE,
          Encode.forJava(request.getUser().getUserId()), Encode.forJava(baseUrl), e);
      throw new ConnectorRequestException(
          "Failed to createUser from UserConnector ",
          "url=" + baseUrl, e);
    }
  }
}
