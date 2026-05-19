package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.oauth;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * The type Cached o auth token.
 */
@Getter
@Setter
public class CachedOAuthToken {

  private String accessToken;
  private Instant expiresIn;

  /**
   * Instantiates a new Cached o auth token.
   *
   * @param response the response
   */
  public CachedOAuthToken(OAuthTokenRestClientResponse response) {
    this.accessToken = response.accessToken();
    this.expiresIn = Instant.now().plusSeconds(response.expiresIn() - 30);
  }

  /**
   * Is expired boolean.
   *
   * @return the boolean
   */
  public boolean isExpired() {
    return Instant.now().isAfter(expiresIn);
  }
}
