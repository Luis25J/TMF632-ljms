package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConnectorRequestExceptionTest {

    @Test
    void constructor_shouldSetMessageCauseAndRequestInfo() {
        // Arrange
        String message = "Request failed";
        String info = "GET /api/resource";
        Throwable cause = new IllegalStateException("Underlying error");

        // Act
        ConnectorRequestException ex =
                new ConnectorRequestException(message, info, cause);

        // Assert
        assertEquals(message, ex.getMessage(), "Exception message should match");
        assertSame(cause, ex.getCause(), "Cause should be preserved");
        assertEquals(info, ex.getRequestInfo(), "RequestInfo should match");
    }

    @Test
    void getRequestInfo_shouldReturnTheSameValue() {
        var info = "POST /v1/users";
        var ex = new ConnectorRequestException("msg", info, null);

        assertEquals(info, ex.getRequestInfo(),
                "getRequestInfo() should return the value passed to the constructor");
    }
}
