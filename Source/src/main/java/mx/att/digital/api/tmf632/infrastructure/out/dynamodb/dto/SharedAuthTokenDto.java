package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Shared auth token dto.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedAuthTokenDto {
    private String tokenKey;
    private String accessToken;
    private Long expiresAt;
    private Long lockUntil;
    private Long ttl;
}
