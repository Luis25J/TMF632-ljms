package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomTMF629ConnectorClientTest {

    @Mock
    private RestTemplate restTemplate632;

    @InjectMocks
    private CustomTMF629ConnectorClient client;

    @BeforeEach
    void setUp() {
        // Set the @Value-injected baseUrl
        ReflectionTestUtils.setField(client, "baseUrl", "http://api.example.com/customers");
    }

    @Test
    void getUser_whenRestTemplateReturnsArray_shouldReturnListOfResponses() {
        // Arrange
        String msisdn = "12345";
        String expectedUrl = "http://api.example.com/customers"
                           + "?msisdn=" + msisdn
                           + "&source=SuperApp";

        TMF629CustomResponse resp1 = new TMF629CustomResponse();
        TMF629CustomResponse resp2 = new TMF629CustomResponse();
        TMF629CustomResponse[] mockArray = new TMF629CustomResponse[]{ resp1, resp2 };

        when(restTemplate632.getForObject(expectedUrl, TMF629CustomResponse[].class))
            .thenReturn(mockArray);

        // Act
        List<TMF629CustomResponse> result = client.getUser(msisdn);

        // Assert
        assertThat(result).hasSize(2)
                          .containsExactly(resp1, resp2);

        // Verify URL and type passed to RestTemplate
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate632).getForObject(urlCaptor.capture(),
                                             eq(TMF629CustomResponse[].class));
        assertThat(urlCaptor.getValue()).isEqualTo(expectedUrl);
    }

    @Test
    void getUser_whenRestTemplateThrowsException_shouldReturnEmptyList() {
        // Arrange
        String msisdn = "99999";
        String expectedUrl = "http://api.example.com/customers"
                           + "?msisdn=" + msisdn
                           + "&source=SuperApp";

        when(restTemplate632.getForObject(expectedUrl, TMF629CustomResponse[].class))
            .thenThrow(new RestClientException("Service unavailable"));

        // Act
        List<TMF629CustomResponse> result = client.getUser(msisdn);

        // Assert
        assertThat(result).isEmpty();
        // Ensure we still attempted the call
        verify(restTemplate632).getForObject(expectedUrl, TMF629CustomResponse[].class);
    }
}
