package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mx.att.digital.api.tmf632.infrastructure.exception.ConnectorRequestException;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConnectorClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserConnectorClient client;

    private final String BASE = "http://api.user";
    private final String USER = "u1";
    private final String PASS = "p1";
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "baseUrl", BASE);
        ReflectionTestUtils.setField(client, "username", USER);
        ReflectionTestUtils.setField(client, "password", PASS);
    }

    private String basicAuth() {
        String auth = USER + ":" + PASS;
        return "Basic " +
            Base64.getEncoder()
                  .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void retrieveUserById_success() throws JsonProcessingException {
        // Arrange
        String id = "id1";
        UserConnector uc = new UserConnector();
        uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(uc);
        String json = mapper.writeValueAsString(req);
        ResponseEntity<String> resp = new ResponseEntity<>(json, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE + "/" + id),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(resp);

        // Act
        UserConnector result = client.retrieveUserById(id);

        // Assert
        assertThat(result).isNotNull().extracting(UserConnector::getUserId)
                          .isEqualTo(id);
    }

    @Test
    void retrieveUserById_errorReturnsNull() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("fail"));

        assertThat(client.retrieveUserById("x")).isNull();
    }

    @Test
    void updateUser_success() throws Exception {
        String id = "u1";
        UserConnector uc = new UserConnector(); uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest(); req.setUser(uc);
        UserConnectorResponse respDto = new UserConnectorResponse("00","ok",id);
        String body = mapper.writeValueAsString(respDto);
        ResponseEntity<String> resp = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(BASE + "/" + id),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(resp);

        UserConnectorResponse out = client.updateUser(id, req);
        assertThat(out.getCodigo()).isEqualTo("00");
        verify(restTemplate).exchange(anyString(), any(), any(), eq(String.class));
    }

    @Test
    void updateUser_errorThrows() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("err"));
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(new UserConnector());
        assertThatThrownBy(() -> client.updateUser("i", req))
            .isInstanceOf(ConnectorRequestException.class)
            .hasMessageContaining("Failed to updateUser");
    }

    @Test
    void updateResource_success() throws Exception {
        String id="u1", rid="r1", type="t";
        UserConnectorResponse dto = new UserConnectorResponse("00","ok",id);
        String json = mapper.writeValueAsString(dto);
        ResponseEntity<String> resp = new ResponseEntity<>(json, HttpStatus.OK);

        String url = BASE + "/" + id + "/" + type + "/" + rid;
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(resp);

        UserConnectorResponse out = client.updateResource(id, rid, type, new UserConnectorRequest());
        assertThat(out.getCodigo()).isEqualTo("00");
    }

    @Test
    void updateResource_errorReturns02() {
        String id="u1", rid="r1", type="t";
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("fail"));
        UserConnectorResponse out = client.updateResource(id, rid, type, new UserConnectorRequest());
        assertThat(out.getCodigo()).isEqualTo("02");
        assertThat(out.getUserId()).isEqualTo(id);
                               
    }

    @Test
    void createUser_success() throws Exception {
        String id = "u1";
        UserConnector uc = new UserConnector();
        uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(uc);

        UserConnectorResponse respDto = new UserConnectorResponse("00", "ok", id);
        String json = mapper.writeValueAsString(respDto);

        when(restTemplate.exchange(
                eq(BASE),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        UserConnectorResponse out = client.createUser(req);

        assertThat(out).isNotNull();
        assertThat(out.getCodigo()).isEqualTo("00");
        assertThat(out.getUserId()).isEqualTo(id);

        verify(restTemplate).exchange(
            eq(BASE), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    void createUser_errorReturnsNull() {
        String id = "u1";
        UserConnector uc = new UserConnector();
        uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(uc);

        when(restTemplate.exchange(
                eq(BASE),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenThrow(new RuntimeException("err"));

        assertThat(client.createUser(req)).isNull();

        verify(restTemplate).exchange(eq(BASE), eq(HttpMethod.POST), any(), eq(String.class));
    }

    @Test
    void deleteUser_success() throws Exception {
        String id="u1";
        UserConnectorResponse dto = new UserConnectorResponse("00","ok",id);
        String json = mapper.writeValueAsString(dto);
        when(restTemplate.exchange(
                eq(BASE + "/" + id),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        UserConnectorResponse out = client.deleteUser(id);
        assertThat(out.getCodigo()).isEqualTo("00");
    }

    @Test
    void deleteUser_errorReturnsNull() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("err"));
        assertThat(client.deleteUser("x")).isNull();
    }

    @Test
    void deleteResource_success() throws Exception {
        String id="u1", rid="r1", type="t";
        UserConnectorResponse dto = new UserConnectorResponse("00","ok",id);
        String json = mapper.writeValueAsString(dto);
        String url = BASE + "/" + id + "/" + type + "/" + rid;
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        UserConnectorResponse out = client.deleteResource(id, rid, type);
        assertThat(out.getCodigo()).isEqualTo("00");
    }

    @Test
    void deleteResource_errorReturns02() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("err"));
        UserConnectorResponse out = client.deleteResource("u","r","t");
        assertThat(out.getCodigo()).isEqualTo("02");
    }

    @Test
    void addContactsUser_success() throws Exception {
        String id = "u1";
        UserConnector uc = new UserConnector();
        uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(uc);

        UserConnectorResponse respDto = new UserConnectorResponse("00", "ok", id);
        String json = mapper.writeValueAsString(respDto);

        String url = BASE + "/" + id + "/contacts";
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        UserConnectorResponse out = client.addContactsUser(req);

        assertThat(out).isNotNull();
        assertThat(out.getCodigo()).isEqualTo("00");
        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(String.class));
    }

    @Test
    void addContactsUser_errorThrows() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("err"));
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(new UserConnector());
        assertThatThrownBy(() -> client.addContactsUser(req))
            .isInstanceOf(ConnectorRequestException.class);
    }

    @Test
    void addConsentsUser_success() throws Exception {
        String id = "u1";
        UserConnector uc = new UserConnector();
        uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(uc);

        UserConnectorResponse respDto = new UserConnectorResponse("00", "ok", id);
        String json = mapper.writeValueAsString(respDto);

        String url = BASE + "/" + id + "/consents";
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        UserConnectorResponse out = client.addConsentsUser(req);

        assertThat(out).isNotNull();
        assertThat(out.getCodigo()).isEqualTo("00");

        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(String.class));
    }

    @Test
    void addConsentsUser_errorThrows() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("err"));
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(new UserConnector());
        assertThatThrownBy(() -> client.addConsentsUser(req))
            .isInstanceOf(ConnectorRequestException.class);
    }

    @Test
    void addAddressUser_success() throws Exception {
        String id = "u1";
        UserConnector uc = new UserConnector();
        uc.setUserId(id);
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(uc);

        UserConnectorResponse respDto = new UserConnectorResponse("00", "ok", id);
        String json = mapper.writeValueAsString(respDto);

        String url = BASE + "/" + id + "/addresses";

        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        UserConnectorResponse out = client.addAddressUser(req);

        assertThat(out).isNotNull();
        assertThat(out.getCodigo()).isEqualTo("00");

        verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), any(), eq(String.class));
    }

    @Test
    void addAddressUser_errorThrows() {
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("err"));
        UserConnectorRequest req = new UserConnectorRequest();
        req.setUser(new UserConnector());
        assertThatThrownBy(() -> client.addAddressUser(req))
            .isInstanceOf(ConnectorRequestException.class);
    }
}
