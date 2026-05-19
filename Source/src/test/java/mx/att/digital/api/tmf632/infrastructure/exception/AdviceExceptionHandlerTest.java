package mx.att.digital.api.tmf632.infrastructure.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdviceExceptionHandlerTest {

    private final AdviceExceptionHandler handler = new AdviceExceptionHandler();

    @Test
    void handleExceptionResponse_withCustomStatusAndCode_buildsFormatException() {
        // Arrange
        String message = "Something went wrong";
        String code = "E123";
        int status = 418; // any non-standard status
        ResponseException ex = new ResponseException(message, code, status);

        // Act
        ResponseEntity<FormatException> response = handler.handleExceptionResponse(ex);

        // Assert status matches exception’s HTTP code
        assertEquals(status, response.getStatusCode().value());

        // Assert body contains expected fields
        FormatException body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(message, body.getCode());
        assertEquals(code,    body.getMessage());
    }
}
