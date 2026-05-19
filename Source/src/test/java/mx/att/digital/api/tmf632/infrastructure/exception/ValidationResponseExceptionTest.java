package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ValidationResponseExceptionTest {

    @Test
    void constructor_withNullPointerCause_setsAllFieldsAndCause() {
        // Arrange
        String code    = "CODE1";
        String reason  = "Reason message";
        String details = "Detail info";
        NullPointerException cause = new NullPointerException("np cause");

        // Act
        ValidationResponseException ex =
                new ValidationResponseException(code, reason, details, cause);

        // Assert
        assertEquals(reason, ex.getMessage());
        assertSame(cause, ex.getCause());
        assertEquals(code,    ex.getCode());
        assertEquals(reason,  ex.getReason());
        assertEquals(details, ex.getDetails());
    }

    @Test
    void constructor_withReflectiveOperationCause_setsAllFieldsAndCause() {
        // Arrange
        String code    = "CODE2";
        String reason  = "Another reason";
        String details = "Other details";
        ReflectiveOperationException cause = new ReflectiveOperationException("reflective cause");

        // Act
        ValidationResponseException ex =
                new ValidationResponseException(code, reason, details, cause);

        // Assert
        assertEquals(reason, ex.getMessage());
        assertSame(cause, ex.getCause());
        assertEquals(code,    ex.getCode());
        assertEquals(reason,  ex.getReason());
        assertEquals(details, ex.getDetails());
    }

    @Test
    void constructor_withoutCause_setsFieldsAndLeavesCauseNull() {
        // Arrange
        String code    = "CODE3";
        String reason  = "No cause reason";
        String details = "No cause details";

        // Act
        ValidationResponseException ex =
                new ValidationResponseException(code, reason, details);

        // Assert
        assertEquals(reason, ex.getMessage());
        assertNull(ex.getCause(), "Cause should be null when none provided");
        assertEquals(code,    ex.getCode());
        assertEquals(reason,  ex.getReason());
        assertEquals(details, ex.getDetails());
    }
}
