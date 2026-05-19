package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.SharedTokenAdapterPort;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity.SharedAuthTokenEntity;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.mapper.SharedTokenMapper;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * The type Shared token adapter.
 */
@Service
@Slf4j
public class SharedTokenAdapter implements SharedTokenAdapterPort {

  private final DynamoDbTable<SharedAuthTokenEntity> sharedAuthTokenEntityDynamoDbTable;
  private final SharedTokenMapper mapper;

  @Value("${aws.tableName}")
  private String awsTableName;

  /**
   * Instantiates a new Shared token adapter.
   *
   * @param enhancedClient the enhanced client
   */
  public SharedTokenAdapter(DynamoDbEnhancedClient enhancedClient) {
    this.sharedAuthTokenEntityDynamoDbTable = enhancedClient.table(
        awsTableName,
        TableSchema.fromBean(SharedAuthTokenEntity.class)
    );
    this.mapper = new SharedTokenMapper();
  }

  @Override
  public SharedAuthTokenDto getAccessToken(String tokenId) {
    Key key = Key.builder().partitionValue(tokenId).build();
    SharedAuthTokenEntity entity = sharedAuthTokenEntityDynamoDbTable.getItem(key);
    if (entity != null) {
      return mapper.entityToDto(entity);
    } else {
      return null;
    }
  }

  @Override
  public void saveAccessToken(String tokenId, String accessToken) {
    log.info("Saving access token with tokenId: {}", tokenId);
    SharedAuthTokenEntity entity = new SharedAuthTokenEntity();
    entity.setTokenKey(tokenId);
    entity.setAccessToken(accessToken);
    entity.setExpiresAt(3600L);
    entity.setTtl(Instant.now()
        .plusSeconds(3000L)
        .getEpochSecond());

    sharedAuthTokenEntityDynamoDbTable.putItem(entity);
  }

  @Override
  public boolean isTokenExpired(SharedAuthTokenDto token) {
    return Instant.now().getEpochSecond() >= token.getTtl();
  }
}

