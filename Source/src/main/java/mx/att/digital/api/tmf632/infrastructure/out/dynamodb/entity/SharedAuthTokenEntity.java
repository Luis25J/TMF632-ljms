package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

/**
 * The type Shared auth token entity.
 */
@DynamoDbBean
@Data
@NoArgsConstructor
public class SharedAuthTokenEntity {
  private String tokenKey; // PK (ej: WSO2)
  private String accessToken;
  private Long expiresAt; // epoch seconds
  private Long lockUntil; // epoch seconds
  private Long ttl; // DynamoDB TTL


  /**
   * Gets token key.
   *
   * @return the token key
   */
  @DynamoDbPartitionKey
  public String getTokenKey() {
    return tokenKey;
  }
}
