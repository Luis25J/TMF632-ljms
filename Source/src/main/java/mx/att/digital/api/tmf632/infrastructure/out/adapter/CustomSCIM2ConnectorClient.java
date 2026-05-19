package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.Tmf632OAuth2ServiceAdapterPort;
import mx.att.digital.api.tmf632.infrastructure.exception.ConnectorRequestException;
import mx.att.digital.api.tmf632.infrastructure.exception.NotFoundException;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.*;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * The type Custom scim 2 connector client.
 */
@Slf4j
@Component
public class CustomSCIM2ConnectorClient implements CustomSCIM2ConnectorPort {

  private final RestTemplate restTemplate632;
  private Tmf632OAuth2ServiceAdapterPort oAuth2ServiceAdapterPort;

  private String token;

  @Value("${tmf632.ciam2.base-url}")
  private String baseUrl;

  private final ObjectMapper mapper = new ObjectMapper();

  private static final String IDAD_BILBAO = "ADBILBAO";
  private static final String ERROR_MESSAGE = "[{}][CustomSCIM2] Error calling {}: {}";


  /**
   * Instantiates a new Custom scim 2 connector client.
   *
   * @param restTemplate632          the rest template 632
   * @param oAuth2ServiceAdapterPort the o auth 2 service adapter port
   */
  public CustomSCIM2ConnectorClient(@Qualifier("restTemplate632") RestTemplate restTemplate632,
                                    Tmf632OAuth2ServiceAdapterPort oAuth2ServiceAdapterPort) {
    this.restTemplate632 = restTemplate632;
    this.oAuth2ServiceAdapterPort = oAuth2ServiceAdapterPort;
  }


  @Override
  public SCIM2User retrieveUserInfoByName(String userName) {
    log.info("[" + Encode.forJava(userName) +
        "][CustomSCIM2] Calling CustomSCIM2 service retrieveUserInfoByName URL={}" +
        "userName={}", Encode.forJava(baseUrl), Encode.forJava(userName));

    try {
      token = oAuth2ServiceAdapterPort.getValidAccessToken();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);
      headers.add("IDAD", IDAD_BILBAO);

      HttpEntity<String> entity = new HttpEntity<>(headers);

      String urlCustomSCIM2 = UriComponentsBuilder
          .fromUriString(baseUrl)
          .queryParam("userName", userName)
          .encode()
          .toUriString();
      log.error("retrieveUserInfoByName:::" + Encode.forJava(urlCustomSCIM2));

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlCustomSCIM2, HttpMethod.GET,
          entity, String.class);

      log.info("[" + Encode.forJava(userName) + "][CustomSCIM2] Calling CustomSCIM2 service " +
          "retrieveUserInfoByName " + Encode.forJava(respEntity.getBody()));

      SCIM2Response resp = mapper.readValue(respEntity.getBody(), SCIM2Response.class);

      return resp.getUser();

    } catch (HttpClientErrorException notFoundEx) {
      if (notFoundEx.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.error("[{}][CustomSCIM2] User not found at {}: {}",
            Encode.forJava(userName), Encode.forJava(baseUrl), Encode.forJava(notFoundEx.getMessage()));
        throw new NotFoundException("User " + userName + " does not exist.", notFoundEx);
      } else {
        throw new ConnectorRequestException(
            "Failed to retrieve user by name", "url=" + baseUrl + ", userName=" +
            userName, notFoundEx);
      }
    } catch (Exception e) {
      log.error(ERROR_MESSAGE,
          Encode.forJava(userName), Encode.forJava(baseUrl), Encode.forJava(e.getMessage()), e);
      throw new ConnectorRequestException(
          "Failed to retrieve user by name", "url=" + baseUrl + ", userName=" +
          userName, e);
    }
  }

  @Override
  public SCIM2User retrieveUserInfoById(String id) {
    log.info("[" + Encode.forJava(id) + "][CustomSCIM2 Calling CustomSCIM2 service retrieveUserInfoById URL={}",
        Encode.forJava(baseUrl));
    try {
      token = oAuth2ServiceAdapterPort.getValidAccessToken();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);
      headers.add("IDAD", IDAD_BILBAO);

      HttpEntity<String> entity = new HttpEntity<>(headers);

      String urlCustomSCIM2 = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id)
          .encode()
          .toUriString();

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlCustomSCIM2, HttpMethod.GET,
          entity, String.class);

      log.info("[" + Encode.forJava(id) + "][CustomSCIM2] Calling CustomSCIM2 service " +
          "retrieveUserInfoById " + Encode.forJava(respEntity.getBody()));


      SCIM2Response resp = mapper.readValue(respEntity.getBody(), SCIM2Response.class);

      return resp.getUser();

    } catch (HttpClientErrorException notFoundEx) {
      if (notFoundEx.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.error("[{}][CustomSCIM2] User not found at {}: {}",
            Encode.forJava(id), Encode.forJava(baseUrl), Encode.forJava(notFoundEx.getMessage()));
        throw new NotFoundException("User " + id + " does not exist.", notFoundEx);
      }
    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl),
          Encode.forJava(e.getMessage()), e);
      throw new ConnectorRequestException(
          "Failed to retrieve user by id", "url=" + baseUrl + ", userName=" + id, e);
    }
    return null;
  }

  @Override
  public SCIM2UserDelete deleteUserById(String id) {
    log.info("[" + Encode.forJava(id) + "][CustomSCIM2] Calling CustomSCIM2 service deleteUserById URL={} id={}",
        Encode.forJava(baseUrl), Encode.forJava(id));

    try {
      token = oAuth2ServiceAdapterPort.getValidAccessToken();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);
      headers.add("IDAD", IDAD_BILBAO);

      HttpEntity<String> entity = new HttpEntity<>(headers);

      String urlCustomSCIM2 = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id)
          .encode()
          .toUriString();

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlCustomSCIM2, HttpMethod.DELETE,
          entity, String.class);

      log.info("[" + Encode.forJava(id) + "][CustomSCIM2] Calling CustomSCIM2 service " +
          "deleteUserById " + Encode.forJava(respEntity.getBody()));

      SCIM2DeleteResponse resp = mapper.readValue(respEntity.getBody(), SCIM2DeleteResponse.class);

      return resp.getUser();

    } catch (HttpClientErrorException notFoundEx) {
      if (notFoundEx.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.error("[{}][CustomSCIM2 - DELETE] User not found at {}: {}",
            Encode.forJava(id), Encode.forJava(baseUrl), Encode.forJava(notFoundEx.getMessage()));
        throw new NotFoundException("User " + id + " does not exist.", notFoundEx);
      }
    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl),
          Encode.forJava(e.getMessage()), e);
      throw new ConnectorRequestException(
          "Failed to delete user ", "url=" + baseUrl + ", userName=" + id, e);
    }
    return null;
  }

  @Override
  public SCIM2User updateUserById(String id, SCIM2User user) {
    log.info("[" + Encode.forJava(id) + "][CustomSCIM2] Calling CustomSCIM2 service putUserById URL={} id={}",
        Encode.forJava(baseUrl), Encode.forJava(id));

    try {
      token = oAuth2ServiceAdapterPort.getValidAccessToken();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);

      SCIM2Reference reference = new SCIM2Reference();
      reference.setIdad(IDAD_BILBAO);
      SCIM2Request request = new SCIM2Request();
      request.setReference(reference);
      request.setUser(user);

      HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(request), headers);

      String urlCustomSCIM2 = UriComponentsBuilder
          .fromUriString(baseUrl)
          .pathSegment(id)
          .encode()
          .toUriString();

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlCustomSCIM2, HttpMethod.PUT,
          entity, String.class);

      log.info("[" + Encode.forJava(id) + "][CustomSCIM2] Calling CustomSCIM2 service " +
          "updateUserById " + Encode.forJava(respEntity.getBody()));

      SCIM2Response resp = mapper.readValue(respEntity.getBody(), SCIM2Response.class);

      return resp.getUser();

    } catch (HttpClientErrorException notFoundEx) {
      if (notFoundEx.getStatusCode() == HttpStatus.NOT_FOUND) {
        log.error("[{}][CustomSCIM2 - UPDATE] User not found at {}: {}",
            Encode.forJava(id), Encode.forJava(baseUrl), Encode.forJava(notFoundEx.getMessage()));
        throw new NotFoundException("User " + id + " does not exist.", notFoundEx);
      }
    } catch (Exception e) {
      log.error(ERROR_MESSAGE, Encode.forJava(id), Encode.forJava(baseUrl),
          Encode.forJava(e.getMessage()), e);
      throw new ConnectorRequestException(
          "Failed to update user ", "url=" + baseUrl + ", userName=" + id, e);
    }
    return null;
  }

  @Override
  public SCIM2Response addUser(SCIM2Request request) {
    log.info("[" + Encode.forJava(request.getUser().getUserName()) +
        "][CustomSCIM2] Calling CustomSCIM2 service create User" +
        "URL={} id={}", Encode.forJava(baseUrl), request.getUser());

    try {

      token = oAuth2ServiceAdapterPort.getValidAccessToken();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setBearerAuth(token);

      HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(request), headers);

      String urlCustomSCIM2 = baseUrl;

      ResponseEntity<String> respEntity = restTemplate632.exchange(urlCustomSCIM2, HttpMethod.POST,
          entity, String.class);

      log.info("[" + Encode.forJava(request.getUser().getUserName()) + "][CustomSCIM2] Calling CustomSCIM2 service " +
          "createUser " + Encode.forJava(respEntity.getBody()));

      return mapper.readValue(respEntity.getBody(), SCIM2Response.class);

    } catch (Exception e) {
      log.error("[" + Encode.forJava(request.getUser().getUserName()) +
              "][CustomSCIM2] Error calling createUser url={} id={}",
          Encode.forJava(baseUrl), Encode.forJava(request.getUser().getUserName()), e);
      throw new ConnectorRequestException(
          "Failed to create individual from CustomSCIM2 connector",
          "url=" + baseUrl + ", id=" + request.getUser().getUserName(), e);
    }
  }

}
