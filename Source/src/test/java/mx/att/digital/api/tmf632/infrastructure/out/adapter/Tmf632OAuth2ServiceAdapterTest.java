package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;
import mx.att.digital.api.tmf632.infrastructure.exception.ResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Tmf632OAuth2ServiceAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private Tmf632OAuth2ServiceAdapter adapter;

    private final String URL = "http://token.api";
    private final String USER = "userId";
    private final String PASS = "secret";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "urlTokenApi", URL);
        ReflectionTestUtils.setField(adapter, "username", USER);
        ReflectionTestUtils.setField(adapter, "password", PASS);
    }

    @Test
    void restClientResponse_nullBody_throwsNotAvailable() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenReturn(new ResponseEntity<>("null", HttpStatus.OK));

        ResponseException ex = catchThrowableOfType(
            () -> adapter.restClientResponse(),
            ResponseException.class
        );
        assertThat(ex.getMessage()).contains("response is null");
    }

    @Test
    void restClientResponse_404_throwsNotFound() {
        RestClientResponseException rcre =
            new RestClientResponseException("NotFound", 404, "Not Found", null, null, null);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(rcre);

        ResponseException ex = catchThrowableOfType(
            () -> adapter.restClientResponse(),
            ResponseException.class
        );
        assertThat(ex.getMessage()).contains("NotFound");
    }

    @Test
    void restClientResponse_500_throwsNotAvailable() {
        RestClientResponseException rcre =
            new RestClientResponseException("ServerErr", 500, "Error", null, null, null);
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(rcre);

        ResponseException ex = catchThrowableOfType(
            () -> adapter.restClientResponse(),
            ResponseException.class
        );
        assertThat(ex.getMessage()).contains("ServerErr");
    }

    @Test
    void restClientResponse_unexpectedException_throwsNotAvailable() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("boom"));

        ResponseException ex = catchThrowableOfType(
            () -> adapter.restClientResponse(),
            ResponseException.class
        );
        assertThat(ex.getMessage()).contains("boom");
    }

    @Test
    void createBasicAuthHeader_encodesCorrectly() {
        String header = ReflectionTestUtils.invokeMethod(adapter, "createBasicAuthHeader");
        String expected = "Basic " +
            Base64.getEncoder()
                  .encodeToString((USER + ":" + PASS)
                  .getBytes(StandardCharsets.UTF_8));
        assertThat(header).isEqualTo(expected);
    }
}
