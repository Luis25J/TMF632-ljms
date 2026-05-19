package mx.att.digital.api.tmf632.infrastructure.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import mx.att.digital.api.tmf632.domain.constants.Constants;
import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static mx.att.digital.api.tmf632.domain.constants.Constants.ContentError.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void whenMissingRequestHeader_thenReturnsMissingRequiredParam() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("X-Header");
        when(ex.getMessage()).thenReturn("Header X-Header is required");

        ResponseEntity<ResponseException> resp = handler.handleConstraintViolation(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(), "Error en X-Header");
    }

    @Test
    void whenMethodNotSupported_thenReturnsMissingRequiredParam() {
        HttpRequestMethodNotSupportedException ex =
                new HttpRequestMethodNotSupportedException("POST");

        ResponseEntity<ResponseException> resp =
                handler.handleHttpRequestMethodNotSupportedException(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .contains(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(), "Error en POST");
    }

    @Test
    void whenUnrecognizedProperty_thenReturnsMissingRequiredParam() {
        UnrecognizedPropertyException ex = mock(UnrecognizedPropertyException.class);
        when(ex.getPropertyName()).thenReturn("unknownProp");
        when(ex.getMessage()).thenReturn("bad property");

        ResponseEntity<ResponseException> resp =
                handler.handleUnrecognizedPropertyExceptionResponse(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(), "Error en unknownProp");
    }

    @Test
    void whenConstraintViolation_withKnownMessages_thenMissingRequiredParam() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> vio = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("obj.header");
        when(vio.getPropertyPath()).thenReturn(path);
        when(vio.getMessage()).thenReturn(Constants.ContentError.HEADER_INCOMPLETE_PARAMS);

        ConstraintViolationException ex =
                new ConstraintViolationException(Set.of(vio));

        ResponseEntity<ResponseException> resp = handler.handleConstraintViolation(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(), "Error en ");
    }

    @Test
    void whenConstraintViolation_withUnknownMessage_thenUnexpectedError() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> vio = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("obj.field");
        when(vio.getPropertyPath()).thenReturn(path);
        when(vio.getMessage()).thenReturn("something else");

        ConstraintViolationException ex =
                new ConstraintViolationException(Set.of(vio));

        ResponseEntity<ResponseException> resp = handler.handleConstraintViolation(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.UNEXPECTED_ERROR.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(BuilderErrorEnum.UNEXPECTED_ERROR.getCode(), "General Exception: ");
    }

    @Test
    void whenMethodArgumentNotValid_withIncompleteBody_thenMissingRequiredParam() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        FieldError fe = new FieldError("obj", "fieldA", Constants.ContentError.BODY_INCOMPLETE_PARAMS);
        when(br.getFieldErrors()).thenReturn(List.of(fe));
        when(br.toString()).thenReturn("BR1");
        when(ex.getBindingResult()).thenReturn(br);

        ResponseEntity<ResponseException> resp = handler.handleValidationException(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(), "Error en BR1");
    }

    @Test
    void whenMethodArgumentNotValid_withOther_thenUnexpectedError() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult br = mock(BindingResult.class);
        FieldError fe = new FieldError("obj", "fieldB", "other");
        when(br.getFieldErrors()).thenReturn(List.of(fe));
        when(br.toString()).thenReturn("BR2");
        when(ex.getBindingResult()).thenReturn(br);

        ResponseEntity<ResponseException> resp = handler.handleValidationException(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.UNEXPECTED_ERROR.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(BuilderErrorEnum.UNEXPECTED_ERROR.getCode(), "General Exception: BR2");
    }

    @Test
    void whenMediaTypeNotSupported_thenMissingRequiredParam() {
        HttpMediaTypeNotSupportedException ex = mock(HttpMediaTypeNotSupportedException.class);
        when(ex.getContentType()).thenReturn(MediaType.APPLICATION_JSON);
        when(ex.getMessage()).thenReturn("unsupported media type");

        ResponseEntity<ResponseException> resp =
                handler.handleUnrecognizedPropertyExceptionResponse(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );

        ResponseException body = resp.getBody();
        assertNotNull(body);
        assertEquals(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(), body.getCode());
        assertEquals("Error en "+MediaType.APPLICATION_JSON.toString(), body.getMessage());
    }

    @Test
    void whenRuntimeException_connectionError_thenNotConnected() {
        RuntimeException ex = new RuntimeException("Connection refused on socket");
        ResponseEntity<ResponseException> resp = handler.handleRuntimeExceptionResponse(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.NOT_CONNECTED.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(
                        BuilderErrorEnum.NOT_CONNECTED.getCode(),
                        "Error en "+RuntimeException.class.toString()
                );
    }

    @Test
    void whenRuntimeException_causeFormattingError_thenMissingRequiredParam() {
        RuntimeException cause = new RuntimeException("Duplicate field 'x'");
        RuntimeException ex = new RuntimeException("ignored", cause);

        ResponseEntity<ResponseException> resp = handler.handleRuntimeExceptionResponse(ex);
        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.MISSING_REQUIRED_PARAM.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(
                        BuilderErrorEnum.MISSING_REQUIRED_PARAM.getCode(),
                        "Error en "+RuntimeException.class.toString()
                );
    }

    @Test
    void whenRuntimeException_other_thenUnexpectedError() {
        RuntimeException ex = new RuntimeException("nothing special");
        ResponseEntity<ResponseException> resp = handler.handleRuntimeExceptionResponse(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.UNEXPECTED_ERROR.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(
                        BuilderErrorEnum.UNEXPECTED_ERROR.getCode(),
                        "General Exception: "+RuntimeException.class.toString()
                );
    }

    @Test
    void whenNotFoundException_thenNotFound() {
        NotFoundException ex = new NotFoundException("item missing", new Throwable());
        ResponseEntity<ResponseException> resp =
                handler.handleRuntimeNotFoundExceptionResponse(ex);

        assertEquals(
                HttpStatus.valueOf(BuilderErrorEnum.NOT_FOUND.getHttpStatus()),
                resp.getStatusCode()
        );
        assertThat(resp.getBody())
                .extracting("code", "message")
                .containsExactly(
                        BuilderErrorEnum.NOT_FOUND.getCode(),
                        "item missing"
                );
    }
}
