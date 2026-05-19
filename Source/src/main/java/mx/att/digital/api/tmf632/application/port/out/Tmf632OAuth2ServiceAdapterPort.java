package mx.att.digital.api.tmf632.application.port.out;

/**
 * The interface Tmf 632 o auth 2 service adapter port.
 */
@FunctionalInterface
public interface Tmf632OAuth2ServiceAdapterPort {
    /**
     * Gets valid access token.
     *
     * @return the valid access token
     */
    String getValidAccessToken();
}
