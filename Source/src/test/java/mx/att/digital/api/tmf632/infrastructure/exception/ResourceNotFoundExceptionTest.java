package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ResourceNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        // Arrange
        String msg = "Entity not found";

        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException(msg);

        // Assert
        assertEquals(msg, ex.getMessage(), "The exception message should match the constructor argument");
        assertNull(ex.getCause(), "No cause should be set");
        assertTrue(ex instanceof RuntimeException, "Should be a RuntimeException");
    }

    @Test
    void constructor_withNullMessage_shouldAllowNull() {
        // Act
        ResourceNotFoundException ex = new ResourceNotFoundException(null);

        // Assert
        assertNull(ex.getMessage(), "Message may be null if passed in as null");
        assertNull(ex.getCause(), "Cause remains null");
    }
}
