package mx.att.digital.api.tmf632.infrastructure.out.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * The type Rest template config 632.
 */
@Configuration
@Slf4j
public class RestTemplateConfig632 {

  private static final int MAX_TOTAL_CONNECTIONS = 100;
  private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
  private static final int CONNECT_TIMEOUT_MS = 5000;
  private static final int CONNECTION_REQUEST_TIMEOUT_MS = 5000;
  private static final String LOG_SEPARATOR = "===============================================================";

  /**
   * Rest template 632 rest template.
   *
   * @return the rest template
   */
  @Bean(name = "restTemplate632")
  public RestTemplate restTemplate632() {
    try {
      log.info(LOG_SEPARATOR);
      log.info("Configuring RestTemplate with Apache HttpClient 5");
      log.info(LOG_SEPARATOR);

      // ───────────────────────────────────────────────────────────
      // Connection Manager con pooling
      // ───────────────────────────────────────────────────────────
      PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
      connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
      connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);

      log.debug("Connection Manager configured:");
      log.debug("  - MaxTotal: {} connections", MAX_TOTAL_CONNECTIONS);
      log.debug("  - MaxPerRoute: {} connections", MAX_CONNECTIONS_PER_ROUTE);

      // ───────────────────────────────────────────────────────────
      // HttpClient con soporte para GET con body
      // ───────────────────────────────────────────────────────────
      HttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager)
          .build();

      log.debug("Apache HttpClient 5 created");

      // ───────────────────────────────────────────────────────────
      // Request Factory con timeouts
      // ───────────────────────────────────────────────────────────
      HttpComponentsClientHttpRequestFactory requestFactory =
          new HttpComponentsClientHttpRequestFactory(httpClient);

      requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MS);
      requestFactory.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS);

      log.debug("Request Factory configured:");
      log.debug("  - ConnectTimeout: {}ms", CONNECT_TIMEOUT_MS);
      log.debug("  - ConnectionRequestTimeout: {}ms", CONNECTION_REQUEST_TIMEOUT_MS);

      // ───────────────────────────────────────────────────────────
      // RestTemplate final
      // ───────────────────────────────────────────────────────────
      RestTemplate restTemplate = new RestTemplate(requestFactory);

      log.info("RestTemplate configured successfully");
      log.info("  - GET with body support: ENABLED");
      log.info(LOG_SEPARATOR);

      return restTemplate;

    } catch (Exception ex) {
      throw new IllegalStateException("Error configurando SSL para TMF632", ex);
    }
  }
}
