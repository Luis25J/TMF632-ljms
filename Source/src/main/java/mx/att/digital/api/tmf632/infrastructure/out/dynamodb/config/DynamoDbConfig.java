package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity.SharedAuthTokenEntity;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.health.DynamoDbHealthIndicator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

/**
 * The type Dynamo db config.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamoDbConfig {

    @Value("${aws.userKey}")
    private String awsAccessKey;

    @Value("${aws.passwordKey}")
    private String awsSecretKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.tableName}")
    private String awsTableName;

    /**
     * Dynamo db client dynamo db client.
     *
     * @return the dynamo db client
     */
    @Bean
    public DynamoDbClient dynamoDbClient() {
        log.info("Configurando DynamoDbClient para región: {}", awsRegion);

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                awsAccessKey,
                awsSecretKey
        );

        DynamoDbClientBuilder builder = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials));

        return builder.build();
    }

    /**
     * Dynamo db enhanced client dynamo db enhanced client.
     *
     * @param dynamoDbClient the dynamo db client
     * @return the dynamo db enhanced client
     */
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    /**
     * Token table dynamo db table.
     *
     * @param enhancedClient the enhanced client
     * @return the dynamo db table
     */
    @Bean
    public DynamoDbTable<SharedAuthTokenEntity> tokenTable(DynamoDbEnhancedClient enhancedClient) {
        String tableName = awsTableName;
        log.info("Configurando tabla de DynamoDB: {}", tableName);

        return enhancedClient.table(tableName, TableSchema.fromBean(SharedAuthTokenEntity.class));
    }

    /**
     * Dynamo db health indicator dynamo db health indicator.
     *
     * @param dynamoDbClient the dynamo db client
     * @return the dynamo db health indicator
     */
    @Bean
    @ConditionalOnProperty(value = "aws.dynamodb.health-check.enabled", havingValue = "true", matchIfMissing = true)
    public DynamoDbHealthIndicator dynamoDbHealthIndicator(DynamoDbClient dynamoDbClient) {
        return new DynamoDbHealthIndicator(dynamoDbClient, awsTableName);
    }
}
 