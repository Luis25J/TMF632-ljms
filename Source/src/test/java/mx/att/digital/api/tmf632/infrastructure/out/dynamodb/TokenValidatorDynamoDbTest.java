package mx.att.digital.api.tmf632.infrastructure.out.dynamodb;

import mx.att.digital.api.tmf632.application.port.out.SharedTokenAdapterPort;
import mx.att.digital.api.tmf632.application.port.out.Tmf632OAuth2ServiceAdapterPort;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static mx.att.digital.api.tmf632.domain.constants.Constants.TOKEN_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenValidatorDynamoDbTest {

    @Mock
    private SharedTokenAdapterPort sharedTokenAdapterPort;

    @Mock
    private Tmf632OAuth2ServiceAdapterPort oAuth2ServiceAdapterPort;

    private TokenValidatorDynamoDb validator;

    @BeforeEach
    void setUp() {
        validator = new TokenValidatorDynamoDb(sharedTokenAdapterPort, oAuth2ServiceAdapterPort);
    }

    @Test
    void whenNoTokenInDynamo_fetchesAndSavesNewToken() {
        // Given: no token stored
        when(sharedTokenAdapterPort.getAccessToken(TOKEN_ID)).thenReturn(null);
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("NEW_TOKEN");

        // When
        String result = validator.validateTokenDynamo();

        // Then
        assertEquals("NEW_TOKEN", result);
        verify(oAuth2ServiceAdapterPort, times(1)).getValidAccessToken();
        verify(sharedTokenAdapterPort, times(1)).saveAccessToken(TOKEN_ID, "NEW_TOKEN");
        verify(sharedTokenAdapterPort, never()).isTokenExpired(any());
    }

    @Test
    void whenTokenExpired_fetchesAndSavesRefreshedToken() {
        // Given: expired token in DB
        SharedAuthTokenDto expired = SharedAuthTokenDto.builder()
            .accessToken("OLD_TOKEN")
            .build();
        when(sharedTokenAdapterPort.getAccessToken(TOKEN_ID)).thenReturn(expired);
        when(sharedTokenAdapterPort.isTokenExpired(expired)).thenReturn(true);
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("REFRESHED");

        // When
        String result = validator.validateTokenDynamo();

        // Then
        assertEquals("REFRESHED", result);
        verify(sharedTokenAdapterPort).isTokenExpired(expired);
        verify(oAuth2ServiceAdapterPort).getValidAccessToken();
        verify(sharedTokenAdapterPort).saveAccessToken(TOKEN_ID, "REFRESHED");
    }

    @Test
    void whenTokenValid_returnsExistingAccessToken() {
        // Given: valid token in DB
        SharedAuthTokenDto dto = SharedAuthTokenDto.builder()
            .accessToken("VALID_TOKEN")
            .build();
        when(sharedTokenAdapterPort.getAccessToken(TOKEN_ID)).thenReturn(dto);
        when(sharedTokenAdapterPort.isTokenExpired(dto)).thenReturn(false);

        // When
        String result = validator.validateTokenDynamo();

        // Then
        assertEquals("VALID_TOKEN", result);
        verify(sharedTokenAdapterPort).isTokenExpired(dto);
        verify(oAuth2ServiceAdapterPort, never()).getValidAccessToken();
        verify(sharedTokenAdapterPort, never()).saveAccessToken(any(), any());
    }
}
