package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.mapper;

import org.springframework.stereotype.Component;

import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity.SharedAuthTokenEntity;

/**
 * The type Shared token mapper.
 */
@Component
public class SharedTokenMapper {
    /**
     * Entity to dto shared auth token dto.
     *
     * @param entity the entity
     * @return the shared auth token dto
     */
    public SharedAuthTokenDto entityToDto(SharedAuthTokenEntity entity) {
        return SharedAuthTokenDto.builder()
                .tokenKey(entity.getTokenKey())
                .accessToken(entity.getAccessToken())
                .lockUntil(entity.getExpiresAt())
                .ttl(entity.getTtl())
                .build();
    }

    /**
     * Dto to entity shared auth token entity.
     *
     * @param dto the dto
     * @return the shared auth token entity
     */
    public SharedAuthTokenEntity dtoToEntity(SharedAuthTokenDto dto) {
        SharedAuthTokenEntity entityToken = new SharedAuthTokenEntity();
        entityToken.setTokenKey(dto.getTokenKey());
        entityToken.setAccessToken(dto.getAccessToken());
        entityToken.setExpiresAt(dto.getExpiresAt());
        entityToken.setTtl(dto.getTtl());
        return entityToken;
    }
}
