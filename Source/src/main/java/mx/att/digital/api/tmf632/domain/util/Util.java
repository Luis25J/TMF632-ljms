package mx.att.digital.api.tmf632.domain.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;
import mx.att.digital.api.tmf632.infrastructure.exception.ResponseException;

/**
 * The type Util.
 */
@Slf4j
@Component
public class Util {


    /**
     * Dto to json string.
     *
     * @param dto the dto
     * @return the string
     */
    public String dtoToJson(Object dto) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Error mapping dto to Json ", e);
            throw new ResponseException(BuilderErrorEnum.UNEXPECTED_ERROR, e.getMessage());
        }
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public String getTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.format(timestamp);
    }

}
