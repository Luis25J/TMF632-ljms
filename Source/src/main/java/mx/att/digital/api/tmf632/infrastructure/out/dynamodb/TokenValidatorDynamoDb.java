package mx.att.digital.api.tmf632.infrastructure.out.dynamodb;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.out.SharedTokenAdapterPort;
import mx.att.digital.api.tmf632.application.port.out.Tmf632OAuth2ServiceAdapterPort;
import mx.att.digital.api.tmf632.application.port.out.TokenValidatorDynamoDbPort;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static mx.att.digital.api.tmf632.domain.constants.Constants.TOKEN_ID;

/**
 * The type Token validator dynamo db.
 */
@Slf4j
@Service
public class TokenValidatorDynamoDb implements TokenValidatorDynamoDbPort {

    private final SharedTokenAdapterPort sharedTokenAdapterPort;
    private final Tmf632OAuth2ServiceAdapterPort oAuth2ServiceAdapterPort;

    /**
     * Instantiates a new Token validator dynamo db.
     *
     * @param sharedTokenAdapterPort   the shared token adapter port
     * @param oAuth2ServiceAdapterPort the o auth 2 service adapter port
     */
    @Autowired
    public TokenValidatorDynamoDb(SharedTokenAdapterPort sharedTokenAdapterPort,
                                  Tmf632OAuth2ServiceAdapterPort oAuth2ServiceAdapterPort) {
        this.sharedTokenAdapterPort = sharedTokenAdapterPort;
        this.oAuth2ServiceAdapterPort = oAuth2ServiceAdapterPort;
    }

    @Override
    public String validateTokenDynamo() {
        String token;
        SharedAuthTokenDto tokenInDynamoDB = sharedTokenAdapterPort.getAccessToken(TOKEN_ID);
        log.info("Token of wso2 in dynamoDB: {}", tokenInDynamoDB);

        if (tokenInDynamoDB != null) {
            log.info("Using token from DynamoDB");
            if (sharedTokenAdapterPort.isTokenExpired(tokenInDynamoDB)) {
                log.info("Token in DynamoDB is expired, fetching new token");
                token = oAuth2ServiceAdapterPort.getValidAccessToken();

                sharedTokenAdapterPort
                        .saveAccessToken(TOKEN_ID, token);
            } else {
                log.info("Token in DynamoDB is valid, using existing token");
                token = tokenInDynamoDB.getAccessToken();
            }
        } else {
            log.info("No valid token in DynamoDB, fetching new token");
            token = oAuth2ServiceAdapterPort.getValidAccessToken();

            sharedTokenAdapterPort
                    .saveAccessToken(TOKEN_ID, token);
        }
        return token;
    }
}
