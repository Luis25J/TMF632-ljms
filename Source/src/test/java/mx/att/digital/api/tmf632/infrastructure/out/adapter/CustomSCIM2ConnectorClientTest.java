package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import mx.att.digital.api.tmf632.application.port.out.Tmf632OAuth2ServiceAdapterPort;
import mx.att.digital.api.tmf632.infrastructure.exception.ConnectorRequestException;
import mx.att.digital.api.tmf632.infrastructure.exception.NotFoundException;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomSCIM2ConnectorClientTest {

    @Mock
    private RestTemplate restTemplate632;

    @Mock
    private Tmf632OAuth2ServiceAdapterPort oAuth2ServiceAdapterPort;

    @InjectMocks
    private CustomSCIM2ConnectorClient client;

    private final ObjectMapper mapper = new ObjectMapper();
    private final String BASE = "http://scim2.api";

    @BeforeEach
    void init() {
        // inject baseUrl
        ReflectionTestUtils.setField(client, "baseUrl", BASE);
    }

    // Helper to build a SCIM2Response JSON
    private String toJson(SCIM2Response resp) throws Exception {
        return mapper.writeValueAsString(resp);
    }

    private String toJsonDelete(SCIM2DeleteResponse resp) throws Exception {
        return mapper.writeValueAsString(resp);
    }

    @Test
    void retrieveUserInfoByName_success() throws Exception {
        String userName = "alice";
        String token = "tok";
        SCIM2User user = new SCIM2User(); user.setUserName(userName);
        SCIM2Response resp = new SCIM2Response(); resp.setUser(user);

        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn(token);
        when(restTemplate632.exchange(
                eq(BASE + "?userName=" + userName),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(ResponseEntity.ok(toJson(resp)));

        SCIM2User result = client.retrieveUserInfoByName(userName);
        assertNotNull(result);
        assertEquals(userName, result.getUserName());
    }

    @Test
    void retrieveUserInfoByName_notFound() {
        String userName = "bob";
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("t");
        HttpClientErrorException nf = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "404", HttpHeaders.EMPTY, null, null);
        when(restTemplate632.exchange(
                anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(nf);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> client.retrieveUserInfoByName(userName));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void retrieveUserInfoByName_otherHttpError() {
        String userName = "charlie";
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("t");
        HttpClientErrorException e400 = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "400", HttpHeaders.EMPTY, null, null);
        when(restTemplate632.exchange(
                anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(e400);

        ConnectorRequestException ex = assertThrows(ConnectorRequestException.class,
                () -> client.retrieveUserInfoByName(userName));
        assertTrue(ex.getMessage().contains("Failed to retrieve user by name"));
    }

    @Test
    void retrieveUserInfoByName_genericError() {
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("t");
        when(restTemplate632.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenThrow(new RuntimeException("oops"));

        ConnectorRequestException ex = assertThrows(ConnectorRequestException.class,
                () -> client.retrieveUserInfoByName("any"));
        assertTrue(ex.getMessage().contains("Failed to retrieve user by name"));
    }

    @Test
    void retrieveUserInfoById_success() throws Exception {
        String id = "id1";
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tok");
        SCIM2User u = new SCIM2User(); u.setUserId(id);
        SCIM2Response r = new SCIM2Response(); r.setUser(u);
        when(restTemplate632.exchange(
                eq(BASE + "/" + id),
                eq(HttpMethod.GET),
                any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(toJson(r)));

        SCIM2User out = client.retrieveUserInfoById(id);
        assertEquals(id, out.getUserId());
    }

    @Test
    void updateUserById_notFound() {
        HttpClientErrorException nf = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "404", HttpHeaders.EMPTY, null, null);

        lenient().when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tok");
        // stub only the exact URL + method + headers match
        lenient().when(restTemplate632.exchange(
                        eq(BASE + "/x"),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(String.class)))
                .thenThrow(nf);

        assertThrows(NotFoundException.class,
                () -> client.updateUserById("x", new SCIM2User()));
    }


    @Test
    void updateUserById_otherHttpError_returnsNull() {
        HttpClientErrorException badReq = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "400", HttpHeaders.EMPTY, null, null);

        lenient().when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tok");
        lenient().when(restTemplate632.exchange(
                        eq(BASE + "/x"),
                        eq(HttpMethod.PUT),
                        any(HttpEntity.class),
                        eq(String.class)))
                .thenThrow(badReq);

        // client code catches non-404 and returns null
        assertNull(client.updateUserById("x", new SCIM2User()));
    }

    @Test
    void deleteUserById_success() throws Exception {
        String id = "del1";
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("T");
        SCIM2DeleteResponse dresp = new SCIM2DeleteResponse();
        SCIM2UserDelete ud = new SCIM2UserDelete(); ud.setUserId(id);
        dresp.setUser(ud);
        when(restTemplate632.exchange(
                eq(BASE + "/" + id),
                eq(HttpMethod.DELETE),
                any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(toJsonDelete(dresp)));

        SCIM2UserDelete out = client.deleteUserById(id);
        assertEquals(id, out.getUserId());
    }

    @Test
    void updateUserById_success() throws Exception {
        String id = "u1";
        SCIM2User in = new SCIM2User(); in.setUserId(id);
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tt");
        SCIM2Response r = new SCIM2Response(); r.setUser(in);
        when(restTemplate632.exchange(
                eq(BASE + "/" + id),
                eq(HttpMethod.PUT),
                any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(toJson(r)));

        SCIM2User out = client.updateUserById(id, in);
        assertEquals(id, out.getUserId());
    }

    @Test
    void updateUserById_otherError() {
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tt");
        HttpClientErrorException e = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "400", HttpHeaders.EMPTY, null, null);
        when(restTemplate632.exchange(any(), eq(HttpMethod.PUT), any(), eq(String.class)))
                .thenThrow(e);

        assertThrows(ConnectorRequestException.class,
                () -> client.updateUserById("x", new SCIM2User()));
    }

    @Test
    void addUser_success() throws Exception {
        SCIM2User u = new SCIM2User(); u.setUserName("newU");
        SCIM2Request req = new SCIM2Request(); req.setUser(u);
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tkn");
        SCIM2Response r = new SCIM2Response(); r.setUser(u);
        when(restTemplate632.exchange(
                eq(BASE),
                eq(HttpMethod.POST),
                any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mapper.writeValueAsString(r)));

        SCIM2Response out = client.addUser(req);
        assertEquals("newU", out.getUser().getUserName());
    }

    @Test
    void addUser_error() {
        SCIM2Request req = new SCIM2Request(); req.setUser(new SCIM2User());
        when(oAuth2ServiceAdapterPort.getValidAccessToken()).thenReturn("tkn");
        when(restTemplate632.exchange(any(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(new RuntimeException("bad"));

        ConnectorRequestException ex = assertThrows(ConnectorRequestException.class,
                () -> client.addUser(req));
        assertTrue(ex.getMessage().contains("Failed to create"));
    }
}
