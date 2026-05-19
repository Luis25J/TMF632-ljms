package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity.SharedAuthTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SharedTokenAdapterTest {

    private DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<SharedAuthTokenEntity> tableMock;
    private SharedTokenAdapter adapter;

    @BeforeEach
    void setUp() {
        enhancedClient = mock(DynamoDbEnhancedClient.class);
        tableMock = mock(DynamoDbTable.class);

        // Stub any table(name, schema) call to return our mock
        when(enhancedClient.table(any(), any(TableSchema.class)))
            .thenReturn(tableMock);

        // Instantiate the adapter (awsTableName is not used at runtime for stub)
        adapter = new SharedTokenAdapter(enhancedClient);
    }

    @Test
        void getAccessToken_entityExists_returnsMappedDto() {
            String tokenId = "token-123";
            SharedAuthTokenEntity entity = new SharedAuthTokenEntity();
            entity.setTokenKey(tokenId);
            entity.setAccessToken("abc");
            entity.setExpiresAt(3600L);
            entity.setTtl(1_600_000_000L);

            doReturn(entity)
            .when(tableMock)
            .getItem(any(Key.class));

            SharedAuthTokenDto dto = adapter.getAccessToken(tokenId);

            assertThat(dto).isNotNull();
            assertThat(dto.getTokenKey()).isEqualTo(tokenId);
            assertThat(dto.getAccessToken()).isEqualTo("abc");
            assertThat(dto.getTtl()).isEqualTo(1_600_000_000L);

            ArgumentCaptor<Key> captor = ArgumentCaptor.forClass(Key.class);
            verify(tableMock).getItem(captor.capture());
            assertThat(captor.getValue().partitionKeyValue()).isEqualTo(AttributeValue.fromS(tokenId));
        }

    @Test
    void getAccessToken_entityMissing_returnsNull() {
        // Arrange
        when(tableMock.getItem(any(Key.class))).thenReturn(null);

        // Act
        SharedAuthTokenDto dto = adapter.getAccessToken("no-such-token");

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void saveAccessToken_buildsEntityAndPutsIt() {
        // Arrange
        String tokenId = "t-456";
        String accessToken = "XYZ";

        // Act
        adapter.saveAccessToken(tokenId, accessToken);

        // Assert
        ArgumentCaptor<SharedAuthTokenEntity> captor =
            ArgumentCaptor.forClass(SharedAuthTokenEntity.class);
        verify(tableMock).putItem(captor.capture());

        SharedAuthTokenEntity saved = captor.getValue();
        assertThat(saved.getTokenKey()).isEqualTo(tokenId);
        assertThat(saved.getAccessToken()).isEqualTo(accessToken);
        assertThat(saved.getExpiresAt()).isEqualTo(3600L);
        assertThat(saved.getTtl()).isGreaterThanOrEqualTo(
            Instant.now().plusSeconds(3000L).getEpochSecond() - 1
        );
    }

    @Test
    void isTokenExpired_whenNowBeforeTtl_returnsFalse() {
        // Arrange
        SharedAuthTokenDto dto = new SharedAuthTokenDto();
        dto.setTtl(Instant.now().getEpochSecond() + 10);

        // Act & Assert
        assertThat(adapter.isTokenExpired(dto)).isFalse();
    }

    @Test
    void isTokenExpired_whenNowAtOrAfterTtl_returnsTrue() {
        // Arrange
        SharedAuthTokenDto dto = new SharedAuthTokenDto();
        dto.setTtl(Instant.now().getEpochSecond());

        // Act & Assert
        assertThat(adapter.isTokenExpired(dto)).isTrue();
    }
}
