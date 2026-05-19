package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.config;

import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity.SharedAuthTokenEntity;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.health.DynamoDbHealthIndicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DynamoDbConfigTest {

    private DynamoDbConfig config;

    @BeforeEach
    void setUp() {
        config = new DynamoDbConfig();
    }

    @Test
    void dynamoDbClient_returnsNonNullClient() {
        // Arrange: inject dummy AWS properties
        ReflectionTestUtils.setField(config, "awsAccessKey", "AK");
        ReflectionTestUtils.setField(config, "awsSecretKey", "SK");
        ReflectionTestUtils.setField(config, "awsRegion", "us-west-2");

        // Act
        DynamoDbClient client = config.dynamoDbClient();

        // Assert
        assertNotNull(client, "DynamoDbClient should not be null");
        // We can at least call a forbidden operation to ensure it's a real client
        assertThrows(DynamoDbException.class, client::listTables);
    }

    @Test
    void dynamoDbEnhancedClient_wrapsGivenClient() {
        DynamoDbClient mockClient = mock(DynamoDbClient.class);
        var enhanced = config.dynamoDbEnhancedClient(mockClient);
        assertNotNull(enhanced);
        // underlying service client call will delegate to our mock, e.g.:
        assertSame(mockClient, ReflectionTestUtils.getField(enhanced, "dynamoDbClient"));
    }

    @Test
    void tokenTable_usesInjectedTableName() {
        // Arrange
        String tableName = "MyTokens";
        ReflectionTestUtils.setField(config, "awsTableName", tableName);

        @SuppressWarnings("unchecked")
        DynamoDbEnhancedClient enhanced = mock(DynamoDbEnhancedClient.class);
        @SuppressWarnings("unchecked")
        DynamoDbTable<SharedAuthTokenEntity> dummyTable = mock(DynamoDbTable.class);
        when(enhanced.table(eq(tableName),
            eq(TableSchema.fromBean(SharedAuthTokenEntity.class))))
          .thenReturn(dummyTable);

        // Act
        DynamoDbTable<SharedAuthTokenEntity> result = config.tokenTable(enhanced);

        // Assert
        assertSame(dummyTable, result);
        verify(enhanced).table(tableName,
            TableSchema.fromBean(SharedAuthTokenEntity.class));
    }

    @Test
    void dynamoDbHealthIndicator_containsClientAndTableName() {
        // Arrange
        String tableName = "TokensTable";
        ReflectionTestUtils.setField(config, "awsTableName", tableName);
        DynamoDbClient mockClient = mock(DynamoDbClient.class);

        // Act
        DynamoDbHealthIndicator indicator =
            config.dynamoDbHealthIndicator(mockClient);

        // Assert
        assertNotNull(indicator);
        assertSame(mockClient,
            ReflectionTestUtils.getField(indicator, "dynamoDbClient"));
        assertEquals(tableName,
            ReflectionTestUtils.getField(indicator, "tableName"));
    }
}
