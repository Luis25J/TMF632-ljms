package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessageAndCause() {
        // Arrange
        String message = "Resource missing";
        Throwable cause = new IllegalStateException("Underlying");

        // Act
        NotFoundException ex = new NotFoundException(message, cause);

        // Assert
        assertEquals(message, ex.getMessage(), "Exception message should match");
        assertSame(cause, ex.getCause(), "Cause should be preserved");
        assertTrue(ex instanceof RuntimeException, "Should be a RuntimeException");
    }

    @Test
    void constructor_withNullCause_shouldStillSetMessage() {
        // Arrange
        String message = "Not found";

        // Act
        NotFoundException ex = new NotFoundException(message, null);

        // Assert
        assertEquals(message, ex.getMessage());
        assertNull(ex.getCause(), "Cause should be null when passed null");
    }
}
