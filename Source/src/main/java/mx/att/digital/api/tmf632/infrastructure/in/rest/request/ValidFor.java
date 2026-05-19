package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Valid for.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidFor {

       private String startDateTime;
       private String endDateTime;

}
