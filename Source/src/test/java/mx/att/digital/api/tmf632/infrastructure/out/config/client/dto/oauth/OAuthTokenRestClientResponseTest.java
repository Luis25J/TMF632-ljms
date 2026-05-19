package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.oauth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OAuthTokenRestClientResponseTest {

    @Test
    void accessorsReturnConstructorValues() {
        OAuthTokenRestClientResponse resp =
                new OAuthTokenRestClientResponse("token123", 3600L);

        assertEquals("token123", resp.accessToken());
        assertEquals(3600L, resp.expiresIn());
    }

    @Test
    void equalsHashCodeAndToStringBehaveAsExpected() {
        OAuthTokenRestClientResponse r1 =
                new OAuthTokenRestClientResponse("foo", 100L);
        OAuthTokenRestClientResponse r2 =
                new OAuthTokenRestClientResponse("foo", 100L);
        OAuthTokenRestClientResponse r3 =
                new OAuthTokenRestClientResponse("bar", 100L);

        // equals & hashCode
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotEquals(r1, r3);

        // toString contains field names and values
        String ts = r1.toString();
        assertTrue(ts.contains("accessToken=foo"));
        assertTrue(ts.contains("expiresIn=100"));
    }

    @Test
    void jacksonIgnoresUnknownPropertiesOnDeserialization() throws Exception {
        String json = """
            {
              "access_token":"abc",
              "expires_in":42,
              "extra_field":"ignored"
            }
            """;

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        OAuthTokenRestClientResponse resp =
                mapper.readValue(json, OAuthTokenRestClientResponse.class);

        assertEquals("abc", resp.accessToken());
        assertEquals(42L, resp.expiresIn());
    }
}
