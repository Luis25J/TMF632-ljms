package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.oauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type O auth token rest client response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthTokenRestClientResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("expires_in") long expiresIn
) {
}
