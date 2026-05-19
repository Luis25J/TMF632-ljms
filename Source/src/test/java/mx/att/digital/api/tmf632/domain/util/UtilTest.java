package mx.att.digital.api.tmf632.domain.util;

import mx.att.digital.api.tmf632.infrastructure.exception.ResponseException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UtilTest {

    private final Util util = new Util();

    // Simple POJO for successful serialization
    static class SimpleDto {
        private final String foo;
        SimpleDto(String foo) { this.foo = foo; }
        public String getFoo() { return foo; }
    }

    // POJO whose getter throws, to trigger JsonProcessingException
    static class BadDto {
        public String getBar() { throw new RuntimeException("serialize-fail"); }
    }

    @Test
    void dtoToJson_success() {
        SimpleDto dto = new SimpleDto("hello");
        String json = util.dtoToJson(dto);
        assertThat(json).isEqualTo("{\"foo\":\"hello\"}");
    }

    @Test
    void dtoToJson_whenMapperThrows_shouldWrapInResponseException() {
        BadDto bad = new BadDto();
        ResponseException ex = catchThrowableOfType(
            () -> util.dtoToJson(bad),
            ResponseException.class
        );
        assertThat(ex.getMessage()).contains("serialize-fail");
    }

    @Test
    void getTimestamp_returnsFormattedString() {
        String ts = util.getTimestamp();
        // yyyy-MM-dd HH:mm:ss.SSS => length 23
        assertThat(ts).hasSize(23)
                      .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}");
    }
}
