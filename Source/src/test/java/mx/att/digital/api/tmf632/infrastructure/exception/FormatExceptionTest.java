package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FormatExceptionTest {

    @Test
    void constructor_setsFieldsProperly() {
        // Arrange
        String code = "E001";
        String msg = "Invalid format";
        Integer status = 400;

        // Act
        FormatException ex = new FormatException(code, msg, status);

        // Assert
        assertEquals(code, ex.getCode(), "code should match");
        assertEquals(msg, ex.getMessage(), "message should match");
        assertEquals(status, ex.getHttpCodeStatus(), "httpCodeStatus should match");
        assertTrue(ex instanceof RuntimeException, "should be a RuntimeException");
    }

    @Test
    void builder_andNullHttpCodeStatus() {
        // Arrange & Act
        FormatException ex = FormatException.builder()
                .code("E002")
                .message("Missing field")
                .httpCodeStatus(null)
                .build();

        // Assert
        assertEquals("E002", ex.getCode());
        assertEquals("Missing field", ex.getMessage());
        assertNull(ex.getHttpCodeStatus(), "httpCodeStatus should be null");
    }
}
