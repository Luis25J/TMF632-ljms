package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.PostpaidConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.exception.ConnectorRequestException;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The type Customer postpaid connector client.
 */
@Slf4j
@Component
public class CustomerPostpaidConnectorClient implements PostpaidConnectorPort {

  private RestTemplate restTemplate632;

  @Value("${tmf632.customer-postpaid.base-url}")
  private String baseUrl;

  @Value("${tmf632.customer-postpaid.path}")
  private String path;

  @Value("${tmf632.customer-postpaid.username}")
  private String username;

  @Value("${tmf632.customer-postpaid.password}")
  private String password;

  private final ObjectMapper mapper = new ObjectMapper();

  private static final String FULL_NAME = "fullName";
  private static final String LEGAL_NAME = "legalName";
  private static final String FORMATTED_NAME = "formattedName";
  private static final String POSTPAID_FOUND_NAME = "[POSTPAID] Found name: {}";

  /**
   * Instantiates a new Customer postpaid connector client.
   *
   * @param restTemplate632 the rest template 632
   */
  @Autowired
  public CustomerPostpaidConnectorClient(@Qualifier("restTemplate632") RestTemplate restTemplate632) {
    this.restTemplate632 = restTemplate632;
  }

  /**
   * Sets base url.
   *
   * @param baseUrl the base url
   */
// Setters para tests
  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  /**
   * Sets path.
   *
   * @param path the path
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * Sets username.
   *
   * @param username the username
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Sets rest template 632.
   *
   * @param restTemplate632 the rest template 632
   */
  public void setRestTemplate632(RestTemplate restTemplate632) {
    this.restTemplate632 = restTemplate632;
  }

  @Override
  public Individual retrieveIndividual(String customerId) {
    String url = buildUrl(customerId);
    log.info("[POSTPAID] Calling real postpaid connector for individual URL={} customerId={}", url, customerId);

    try {
      ResponseEntity<String> resp = executeRequest(url);

      if (!isValidResponse(resp, url)) {
        return null;
      }

      String body = resp.getBody();
      if (body == null || body.isBlank()) {
        return null;
      }

      JsonNode customer = extractCustomerNode(body);
      if (customer == null) {
        return null;
      }

      return buildIndividualFromJson(customer, customerId);

    } catch (Exception e) {
      log.error("[POSTPAID] Error calling connector url={} id={}", url, customerId, e);
      throw new ConnectorRequestException(
          "Failed to retrieve individual from postpaid connector",
          "url=" + url + ", customerId=" + customerId,
          e
      );
    }
  }

  @Override
  public Organization retrieveOrganization(String customerId) {
    String url = buildUrl(customerId);
    log.info("[POSTPAID] Calling real postpaid connector for organization URL={} customerId={}",
        Encode.forJava(url), Encode.forJava(customerId));

    try {
      ResponseEntity<String> resp = executeRequest(url);

      if (!isValidResponse(resp, url)) {
        return null;
      }

      String body = resp.getBody();
      if (body == null || body.isBlank()) {
        log.info("[POSTPAID] Empty response body");
        return null;
      }

      JsonNode customer = extractCustomerNode(body);
      if (customer == null) {
        return null;
      }

      return buildOrganizationFromJson(customer, customerId);

    } catch (Exception e) {
      log.error("[POSTPAID] Error calling connector for organization url={} id={}",
          Encode.forJava(url), Encode.forJava(customerId));
      throw new ConnectorRequestException(
          "Failed to retrieve organization from postpaid connector",
          "url=" + url + ", customerId=" + customerId,
          e
      );
    }
  }

  private String buildUrl(String customerId) {
    return UriComponentsBuilder
        .fromUriString(baseUrl)
        .pathSegment(path)
        .queryParam("customerid", customerId)
        .encode()
        .toUriString();
  }

  private ResponseEntity<String> executeRequest(String url) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", createBasicAuthHeader());
    HttpEntity<String> entity = new HttpEntity<>(headers);
    return restTemplate632.exchange(url, HttpMethod.GET, entity, String.class);
  }

  private boolean isValidResponse(ResponseEntity<String> resp, String url) {
    if (resp == null || resp.getStatusCode() == null || !resp.getStatusCode().is2xxSuccessful()) {
      log.warn("[POSTPAID] Non-2xx response url={} status={}", Encode.forJava(url),
          (resp != null ? resp.getStatusCode() : null));
      return false;
    }
    return true;
  }

  private JsonNode extractCustomerNode(String body) throws Exception {
    JsonNode root = mapper.readTree(body);
    JsonNode customer = root.path("customer");

    if (customer.isMissingNode()) {
      log.info("[POSTPAID] No customer node found in response");
      return null;
    }
    return customer;
  }

  private Individual buildIndividualFromJson(JsonNode customer, String customerId) {
    String customerType = customer.path("type").asText(null);

    if (!"Individual".equals(customerType)) {
      log.info("[POSTPAID] Customer type is not Individual: {}", Encode.forJava(customerType));
      return null;
    }

    Individual out = new Individual();
    out.setId(customerId);
    String name = firstText(customer, FULL_NAME, "name", FORMATTED_NAME, LEGAL_NAME);

    if (name != null) {
      out.setName(name);
      log.info(POSTPAID_FOUND_NAME, Encode.forJava(name));
    } else {
      log.info("[POSTPAID] No name found in response");
    }

    return out;
  }

  private Organization buildOrganizationFromJson(JsonNode customer, String customerId) {
    String customerType = customer.path("type").asText(null);

    if (!"Organization".equals(customerType)) {
      log.info("[POSTPAID] Customer type is not Organization: {}", Encode.forJava(customerType));
      return null;
    }

    Organization out = new Organization();
    out.setId(customerId);
    String name = firstText(customer, LEGAL_NAME, "name", FORMATTED_NAME, FULL_NAME);

    if (name != null) {
      out.setName(name);
      log.info(POSTPAID_FOUND_NAME, Encode.forJava(name));
    } else {
      log.info("[POSTPAID] No name found in response");
    }

    return out;
  }

  private String createBasicAuthHeader() {
    String auth = username + ":" + password;
    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
    return "Basic " + new String(encodedAuth);
  }

  private String firstText(JsonNode node, String... keys) {
    for (String k : keys) {
      JsonNode v = node.path(k);
      if (v != null && v.isTextual() && !v.asText().isBlank()) {
        return v.asText();
      }
    }
    return null;
  }

  @Override
  public Individual retrieveIndividualByCustomerdn(String customerdn) {
    String url = UriComponentsBuilder
        .fromUriString(baseUrl)
        .path(path)
        .queryParam("customerdn", customerdn)
        .encode()
        .toUriString();
    log.info("[POSTPAID] Calling postpaid connector for individual by customerdn URL={}", Encode.forJava(url));

    try {
      ResponseEntity<String> resp = executeRequest(url);

      if (!isValidResponse(resp, url)) {
        return null;
      }

      String body = resp.getBody();
      if (body == null || body.isBlank()) {
        return null;
      }

      JsonNode customer = extractCustomerNode(body);
      if (customer == null) {
        return null;
      }

      // Extract customerId and name
      Individual out = new Individual();
      String customerId = customer.path("customerId").asText(null);
      if (customerId != null) {
        out.setId(customerId);
        log.info("[POSTPAID] Found customerId: {}", Encode.forJava(customerId));
      }

      String name = firstText(customer, FULL_NAME, "name", FORMATTED_NAME, LEGAL_NAME);
      if (name != null) {
        out.setName(name);
        log.info(POSTPAID_FOUND_NAME, Encode.forJava(name));
      }

      return out;

    } catch (Exception e) {
      log.error("[POSTPAID] Error calling connector for customerdn url={}", Encode.forJava(url));
      throw new ConnectorRequestException(
          "Failed to retrieve individual from postpaid connector by customerdn",
          "url=" + url + ", customerdn=" + customerdn,
          e
      );
    }
  }
}