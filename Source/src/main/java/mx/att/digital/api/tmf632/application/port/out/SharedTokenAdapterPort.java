package mx.att.digital.api.tmf632.application.port.out;

import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;

/**
 * The interface Shared token adapter port.
 */
public interface SharedTokenAdapterPort {

    /**
     * Save access token.
     *
     * @param tokenId     the token id
     * @param accessToken the access token
     */
    void saveAccessToken(String tokenId, String accessToken);

    /**
     * Gets access token.
     *
     * @param tokenId the token id
     * @return the access token
     */
    SharedAuthTokenDto getAccessToken(String tokenId);

    /**
     * Is token expired boolean.
     *
     * @param token the token
     * @return the boolean
     */
    boolean isTokenExpired(SharedAuthTokenDto token);
}
