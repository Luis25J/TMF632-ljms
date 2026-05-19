package mx.att.digital.api.tmf632.infrastructure.exception;


import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.domain.constants.Constants;
import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;

import static mx.att.digital.api.tmf632.domain.constants.Constants.ContentError.EMPTY;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * The type Global exception handler.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle constraint violation response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ResponseException> handleConstraintViolation(
        MissingRequestHeaderException exception) {
        BuilderErrorEnum ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        log.error("HEADER {}", exception.getHeaderName());
        log.error("HEADER_EXCEPTION {} ", exception.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(new ResponseException(ex, exception.getHeaderName()));
    }

    /**
     * Handle http request method not supported exception response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseException> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException exception) {
        BuilderErrorEnum ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        log.error("METHOD {}", exception.getMethod());
        log.error("METHOD_EXCEPTION {} ", exception.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(new ResponseException(ex, exception.getMethod()));
    }

    /**
     * Handle unrecognized property exception response response entity.
     *
     * @param unrecognizedPropertyException the unrecognized property exception
     * @return the response entity
     */
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ResponseException> handleUnrecognizedPropertyExceptionResponse(
        UnrecognizedPropertyException unrecognizedPropertyException){
        BuilderErrorEnum ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        log.error("PROPERTY {}", unrecognizedPropertyException.getPropertyName());
        log.error("Element not recognized from definition {}", unrecognizedPropertyException.getMessage());
        return ResponseEntity.status(ex.getHttpStatus())
        .body(new ResponseException(ex, unrecognizedPropertyException.getPropertyName()));
    }

    /**
     * Handle constraint violation response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseException> handleConstraintViolation(ConstraintViolationException exception) {

        BuilderErrorEnum ex = null;

        if(exception.getConstraintViolations()
            .stream().anyMatch(e-> Constants.ContentError.HEADER_INCOMPLETE_PARAMS.equalsIgnoreCase(e.getMessage()) )) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        } else if(exception.getConstraintViolations()
            .stream().anyMatch(e-> Constants.ContentError.HEADER_FORMATTING_ERROR.equalsIgnoreCase(e.getMessage()) )) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        } else if(exception.getConstraintViolations()
            .stream().anyMatch(e-> Constants.ContentError.BODY_INCOMPLETE_PARAMS.equalsIgnoreCase(e.getMessage()) )) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        } else if(exception.getConstraintViolations()
            .stream().anyMatch(e-> Constants.ContentError.BODY_FORMATTING_ERROR.equalsIgnoreCase(e.getMessage()) )) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        }

        if(ex == null){
            log.error("CANNOT IDENTIFY ERROR CODE: ");
            ex = BuilderErrorEnum.UNEXPECTED_ERROR;
        }

        String propertyString;
        String message;
        for(ConstraintViolation<?> violation : exception.getConstraintViolations()){
            propertyString = violation.getPropertyPath()
            .toString().substring(violation.getPropertyPath().toString().indexOf('.')+1);
            message = violation.getMessage();
            log.error("CONSTRAINT_VIOLATED {}", propertyString);
            log.error("MESSAGE {}", message);
        }

        return ResponseEntity.status(ex.getHttpStatus()).body(new ResponseException(ex, EMPTY));
    }

    /**
     * Handle validation exception response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseException> handleValidationException(MethodArgumentNotValidException exception) {
        BuilderErrorEnum ex = BuilderErrorEnum.UNEXPECTED_ERROR;

        if(exception.getBindingResult().getFieldErrors()
            .stream().anyMatch(
        e-> Constants.ContentError.BODY_INCOMPLETE_PARAMS.equalsIgnoreCase(e.getDefaultMessage()))) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        } else if(exception.getBindingResult().getFieldErrors()
            .stream().anyMatch(e-> Constants.ContentError.BODY_FORMATTING_ERROR
                .equalsIgnoreCase(e.getDefaultMessage()))) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        }
        for (FieldError params : exception.getBindingResult().getFieldErrors()){
            log.error("PARAMETER {}", params.getField());
            log.error("DEFAULT_MESSAGE {}", params.getDefaultMessage());
        }
        return ResponseEntity.status(ex.getHttpStatus())
        .body(new ResponseException(ex, exception.getBindingResult().toString()));
    }

    /**
     * Handle unrecognized property exception response response entity.
     *
     * @param mediaTypePropertyException the media type property exception
     * @return the response entity
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ResponseException> handleUnrecognizedPropertyExceptionResponse(
        HttpMediaTypeNotSupportedException mediaTypePropertyException){
        BuilderErrorEnum ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        log.error("CONTENT-TYPE {}", mediaTypePropertyException.getContentType());
        log.error("MESSAGE_CONTENT_EXCEPTION {}", mediaTypePropertyException.getMessage());
        return ResponseEntity.status(
            ex.getHttpStatus())
            .body(new ResponseException(ex, mediaTypePropertyException.getContentType().toString()));
    }

    /**
     * Handle runtime exception response response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseException> handleRuntimeExceptionResponse(RuntimeException exception){
        BuilderErrorEnum ex = null;

        if(exception.getMessage() != null && isNotConnectedError(exception)) {
            ex = BuilderErrorEnum.NOT_CONNECTED;
        } else if(exception.getCause() != null && isBodyFormattingError(exception)) {
            ex = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        }

        if(ex == null) {
            ex = BuilderErrorEnum.UNEXPECTED_ERROR;
        }
        log.error("ERROR {}", exception.getClass());
        log.error("MSG_ERROR {}", exception.getMessage());
        return ResponseEntity.status(
            ex.getHttpStatus())
            .body(new ResponseException(ex, exception.getClass().toString()));
    }

    private boolean isNotConnectedError(RuntimeException exception) {
        String message = exception.getMessage();
        return (message.contains("Connection refused") || 
        message.contains("timed out") || exception.getMessage().toLowerCase().contains("timeout"));
    }

    private boolean isBodyFormattingError(RuntimeException exception) {
        String cause = exception.getCause().toString();
        return (cause.contains("Duplicate field") || 
        cause.contains("Unexpected character") || 
        cause.contains("Unexpected end-of-input") || 
        cause.contains("Illegal unquoted character") || 
        cause.contains("was expecting (JSON String") || cause.contains("Unrecognized field"));
    }

    /**
     * Handle runtime not found exception response response entity.
     *
     * @param exception the exception
     * @return the response entity
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseException> handleRuntimeNotFoundExceptionResponse(NotFoundException exception){
        BuilderErrorEnum ex = BuilderErrorEnum.NOT_FOUND;
        log.error("ERROR {}", exception.getClass());
        log.error("MSG_ERROR {}", exception.getMessage());
        return ResponseEntity.status(
            ex.getHttpStatus())
            .body(new ResponseException(ex, exception.getLocalizedMessage()));
    }
}
