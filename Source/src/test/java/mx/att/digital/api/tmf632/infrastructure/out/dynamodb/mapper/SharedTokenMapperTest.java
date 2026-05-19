package mx.att.digital.api.tmf632.infrastructure.out.dynamodb.mapper;

import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.dto.SharedAuthTokenDto;
import mx.att.digital.api.tmf632.infrastructure.out.dynamodb.entity.SharedAuthTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

class SharedTokenMapperTest {

    private SharedTokenMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SharedTokenMapper();
    }

    @Test
    void entityToDto_mapsAllFieldsCorrectly() throws ParseException {
        // Arrange
        SharedAuthTokenEntity entity = new SharedAuthTokenEntity();
        entity.setTokenKey("key123");
        entity.setAccessToken("access-xyz");

        String fechaStr = "2025-12-31 23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long milisegundos = sdf.parse(fechaStr).getTime();
        entity.setExpiresAt(milisegundos);
        entity.setTtl(3600L);
        SharedAuthTokenDto dto = mapper.entityToDto(entity);

        
        assertNotNull(dto);
        assertEquals("key123",   dto.getTokenKey());
        assertEquals("access-xyz", dto.getAccessToken());
        assertEquals(milisegundos, dto.getLockUntil());
        assertEquals(3600L,      dto.getTtl());
    }

    @Test
    void dtoToEntity_mapsAllFieldsCorrectly() throws ParseException {
        String fechaStr = "2030-01-01 00:00:00Z";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long milisegundos = sdf.parse(fechaStr).getTime();
        SharedAuthTokenDto dto = SharedAuthTokenDto.builder()
                .tokenKey("abc")
                .accessToken("token-abc")
                .lockUntil(milisegundos)
                .ttl(7200L)
                .build();

        // Act
        SharedAuthTokenEntity entity = mapper.dtoToEntity(dto);

        // Assert
        assertNotNull(entity);
        assertEquals("abc",     entity.getTokenKey());
        assertEquals("token-abc", entity.getAccessToken());
        assertEquals(7200L,     entity.getTtl());
    }

    @Test
    void roundTrip_entityToDtoAndBack_yieldsEquivalentEntity() throws ParseException {
        String fechaStr = "2040-06-30 12:00:00Z";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long milisegundos = sdf.parse(fechaStr).getTime();
        SharedAuthTokenEntity original = new SharedAuthTokenEntity();
        original.setTokenKey("round");
        original.setAccessToken("trip");
        original.setExpiresAt(milisegundos);
        original.setTtl(1800L);

        // Act
        SharedAuthTokenDto dto    = mapper.entityToDto(original);
        SharedAuthTokenEntity back = mapper.dtoToEntity(dto);

        // Assert that back matches original
        assertEquals(original.getTokenKey(),   back.getTokenKey());
        assertEquals(original.getAccessToken(), back.getAccessToken());
        assertEquals(original.getTtl(),         back.getTtl());
    }
}
