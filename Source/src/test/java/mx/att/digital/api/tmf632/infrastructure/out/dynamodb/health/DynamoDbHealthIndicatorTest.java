package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamoDbHealthIndicatorTest {

    @Mock
    private DynamoDbClient mockClient;

    private static final String TABLE = "MyTable";
    private DynamoDbHealthIndicator indicator;

    private DescribeTableResponse mockResponse;
    private TableDescription mockTableDesc;

    @BeforeEach
    void setUp() {
        // Now indicator uses the real mockClient
        indicator = new DynamoDbHealthIndicator(mockClient, TABLE);
        mockResponse   = mock(DescribeTableResponse.class);
        mockTableDesc  = mock(TableDescription.class);
    }

    @Test
    void health_upWhenDescribeSucceeds() {
        when(mockClient.describeTable(any(DescribeTableRequest.class)))
            .thenReturn(mockResponse);
        when(mockResponse.table()).thenReturn(mockTableDesc);
        when(mockTableDesc.tableStatus()).thenReturn(TableStatus.ACTIVE);
        when(mockTableDesc.itemCount()).thenReturn(42L);

        Health health = indicator.health();

        assertEquals(Status.UP,   health.getStatus());
        assertEquals(TABLE,       health.getDetails().get("table"));
        assertEquals("ACTIVE",    health.getDetails().get("status"));
        assertEquals(42L,         health.getDetails().get("itemCount"));
    }

    @Test
    void health_downWhenDescribeThrows() {
        when(mockClient.describeTable(any(DescribeTableRequest.class)))
            .thenThrow(new RuntimeException("boom"));

        Health health = indicator.health();

        assertEquals(Status.DOWN,     health.getStatus());
        assertEquals(TABLE,           health.getDetails().get("table"));
        assertEquals("boom",          health.getDetails().get("error"));
    }
}