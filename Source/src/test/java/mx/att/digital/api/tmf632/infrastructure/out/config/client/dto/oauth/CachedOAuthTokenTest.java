package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.oauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedOAuthTokenTest {

    @Mock
    private OAuthTokenRestClientResponse response;

    private final String TOKEN_VALUE = "abc123";
    private final long RESPONSE_EXPIRES_IN = 60L; // seconds

    @BeforeEach
    void setUp() {
        when(response.accessToken()).thenReturn(TOKEN_VALUE);
        when(response.expiresIn()).thenReturn(RESPONSE_EXPIRES_IN);
    }

    @Test
    void constructorShouldSetAccessTokenAndExpiresIn() {
        Instant before = Instant.now();
        CachedOAuthToken token = new CachedOAuthToken(response);
        Instant after = Instant.now();

        // accessToken must match mock
        assertEquals(TOKEN_VALUE, token.getAccessToken());

        // expiresIn = now + (60 - 30) seconds => approx 30 seconds in future
        Instant expiresIn = token.getExpiresIn();
        assertTrue(expiresIn.isAfter(before.plusSeconds(RESPONSE_EXPIRES_IN - 31)));
        assertTrue(expiresIn.isBefore(after.plusSeconds(RESPONSE_EXPIRES_IN - 29)));
    }

    @Test
    void isExpiredShouldReturnFalseWhenNotExpired() {
        CachedOAuthToken token = new CachedOAuthToken(response);
        // fresh token: expiresIn ~ now+30s => not expired
        assertFalse(token.isExpired());
    }

    @Test
    void isExpiredShouldReturnTrueWhenExpired() {
        CachedOAuthToken token = new CachedOAuthToken(response);
        // force in-the-past expiry
        token.setExpiresIn(Instant.now().minus(Duration.ofSeconds(1)));
        assertTrue(token.isExpired());
    }

    @Test
    void gettersAndSettersWorkCorrectly() {
        CachedOAuthToken token = new CachedOAuthToken(response);

        // test setter for accessToken
        token.setAccessToken("newToken");
        assertEquals("newToken", token.getAccessToken());

        // test setter for expiresIn
        Instant custom = Instant.parse("2000-01-01T00:00:00Z");
        token.setExpiresIn(custom);
        assertEquals(custom, token.getExpiresIn());
    }
}
