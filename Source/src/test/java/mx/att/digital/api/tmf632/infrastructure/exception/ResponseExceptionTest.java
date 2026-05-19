package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import mx.att.digital.api.tmf632.domain.enums.BuilderErrorEnum;

class ResponseExceptionTest {

    @Test
    void ctor_withBuilderErrorEnum_appendsSuffixAndUsesEnumHttpStatus() {
        // Arrange
        BuilderErrorEnum err = BuilderErrorEnum.MISSING_REQUIRED_PARAM;
        String suffix = ": missing header X-Header";
        String expectedCode = err.getCode();
        String expectedMessage = err.getMessage() + suffix;
        int expectedStatus = err.getHttpStatus();

        // Act
        ResponseException ex = new ResponseException(err, suffix);

        // Assert
        assertEquals(expectedCode, ex.getCode(), "code should match enum code");
        assertEquals(expectedMessage, ex.getMessage(), "message should be enum message + suffix");
        assertEquals(expectedStatus, ex.getHttpCodeStatus().intValue(), "httpCodeStatus should match enum status");
        assertNull(ex.getCause(), "no cause should be set");
    }

    @Test
    void ctor_withExplicitParams_setsAllFieldsDirectly() {
        // Arrange
        String code = "C123";
        String message = "Custom error";
        Integer status = 409;

        // Act
        ResponseException ex = new ResponseException(code, message, status);

        // Assert
        assertEquals(code, ex.getCode(), "code should match constructor arg");
        assertEquals(message, ex.getMessage(), "message should match constructor arg");
        assertEquals(status, ex.getHttpCodeStatus(), "httpCodeStatus should match constructor arg");
        assertNull(ex.getCause(), "no cause should be set");
    }
}
