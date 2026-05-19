package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.health;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;

/**
 * The type Dynamo db health indicator.
 */
@Slf4j
@RequiredArgsConstructor
public class DynamoDbHealthIndicator implements HealthIndicator {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    @Override
    public Health health() {
        try {
            DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();

            DescribeTableResponse response = dynamoDbClient.describeTable(request);

            return Health.up()
                    .withDetail("table", tableName)
                    .withDetail("status", response.table().tableStatus().toString())
                    .withDetail("itemCount", response.table().itemCount())
                    .build();

        } catch (Exception e) {
            log.error("Error verificando salud de DynamoDB", e);
            return Health.down()
                    .withDetail("table", tableName)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
