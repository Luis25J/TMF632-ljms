package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Partial error.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PartialError {
    
    private String path;
    private String code;
    private String message;
    
}
