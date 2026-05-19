package mx.att.digital.api.tmf632.infrastructure.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * The type Advice exception handler.
 */
@ControllerAdvice
public class AdviceExceptionHandler {
    /**
     * Handle exception response response entity.
     *
     * @param responseException the response exception
     * @return the response entity
     */
    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<FormatException> handleExceptionResponse(ResponseException responseException){
        return ResponseEntity.status(responseException.getHttpCodeStatus())
        .body(FormatException.builder().message(responseException.getMessage())
        .code(responseException.getCode()).build());
    }
}
