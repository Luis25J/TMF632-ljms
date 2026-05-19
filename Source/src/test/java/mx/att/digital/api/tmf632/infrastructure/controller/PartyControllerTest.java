package mx.att.digital.api.tmf632.infrastructure.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import mx.att.digital.api.tmf632.application.port.in.PartyCreatePort;
import mx.att.digital.api.tmf632.application.port.in.PartyDeletePort;
import mx.att.digital.api.tmf632.application.port.in.PartyUpdatePort;
import mx.att.digital.api.tmf632.application.usecase.util.PartyResponseAdapter;
import mx.att.digital.api.tmf632.infrastructure.exception.ResourceNotFoundException;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import mx.att.digital.api.tmf632.infrastructure.out.response.OrganizationResponse;
import mx.att.digital.api.tmf632.application.port.in.PartyQueryPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PartyControllerTest {

    @Mock
    private PartyQueryPort queryPort;
    @Mock
    private PartyCreatePort createPort;
    @Mock
    private PartyUpdatePort updatePort;
    @Mock
    private PartyDeletePort deletePort;

    @Mock
    private PartyResponseAdapter responseAdapter;

    @InjectMocks
    private PartyController controller;


    // --- getOrganization ---

    @Test
    void getOrganization_notFound_throws() {
        String id = "org1";
        when(queryPort.retrieveOrganization(id, null)).thenReturn(null);

        var ex = assertThrows(ResourceNotFoundException.class, () ->
                controller.getOrganization(id)
        );
        assertTrue(ex.getMessage().contains("Organization not found: " + id));
    }

    @Test
    void getOrganization_found_returnsDto() {
        String id = "org2";
        Organization org = new Organization(); org.setId(id);
        OrganizationResponse dto = new OrganizationResponse(); dto.setId(id);

        when(queryPort.retrieveOrganization(id, null)).thenReturn(org);
        when(responseAdapter.toOrganizationDto(org, null)).thenReturn(dto);

        ResponseEntity<OrganizationResponse> resp = controller.getOrganization(id);
        assertEquals(200, resp.getStatusCodeValue());
        assertSame(dto, resp.getBody());
    }

    // --- getIndividualNew ---

    @Test
    void getIndividualNew_notFound_returnsNullBody() {
        String id = "u1";
        when(queryPort.retrieveUser(id)).thenReturn(null);

        ResponseEntity<IndividualTMF632> resp = controller.getIndividualNew(id, "role");
        assertEquals(200, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    void getIndividualNew_found_returnsEntity() {
        String id = "u2";
        IndividualTMF632 ind = new IndividualTMF632();
        ind.setId(id);

        when(queryPort.retrieveUser(id)).thenReturn(ind);

        ResponseEntity<IndividualTMF632> resp = controller.getIndividualNew(id, "role");
        assertEquals(200, resp.getStatusCodeValue());
        assertSame(ind, resp.getBody());
    }

    // --- deleteIndividual ---

    @Test
    void deleteIndividual_notFound_returnsNullBody() {
        String id = "du1";
        when(deletePort.deleteUser(id)).thenReturn(null);

        ResponseEntity<IndividualTMF632> resp = controller.deleteIndividual(id, "role");
        assertEquals(200, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    void deleteIndividual_found_returnsEntity() {
        String id = "du2";
        IndividualTMF632 ind = new IndividualTMF632();
        ind.setId(id);

        when(deletePort.deleteUser(id)).thenReturn(ind);

        ResponseEntity<IndividualTMF632> resp = controller.deleteIndividual(id, "role");
        assertEquals(200, resp.getStatusCodeValue());
        assertSame(ind, resp.getBody());
    }

    // --- createIndividual ---

    @Test
    void createIndividual_returnsEntity() {
        IndividualTMF632 req = new IndividualTMF632();
        req.setId("ci1");
        IndividualTMF632 created = new IndividualTMF632();
        created.setId("ci1");

        when(createPort.createUser("someRole", req)).thenReturn(created);

        ResponseEntity<IndividualTMF632> resp = controller.createIndividual(req, "someRole");
        assertEquals(200, resp.getStatusCodeValue());
        assertSame(created, resp.getBody());
    }

    @Test
    void createIndividual_nullReturned_returnsNullBody() {
        IndividualTMF632 req = new IndividualTMF632();
        when(createPort.createUser("roleX", req)).thenReturn(null);

        ResponseEntity<IndividualTMF632> resp = controller.createIndividual(req, "roleX");
        assertEquals(200, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    // --- updateIndividual ---

    @Test
    void updateIndividual_returnsEntity() {
        String id = "up1";
        IndividualTMF632 req = new IndividualTMF632();
        req.setId(id);
        IndividualTMF632 updated = new IndividualTMF632();
        updated.setId(id + "_updated");

        when(updatePort.updateUser(id, req)).thenReturn(updated);

        ResponseEntity<IndividualTMF632> resp = controller.updateIndividual(id, "role", req);
        assertEquals(200, resp.getStatusCodeValue());
        assertSame(updated, resp.getBody());
    }

    @Test
    void updateIndividual_nullReturned_returnsNullBody() {
        String id = "up2";
        IndividualTMF632 req = new IndividualTMF632();
        when(updatePort.updateUser(id, req)).thenReturn(null);

        ResponseEntity<IndividualTMF632> resp = controller.updateIndividual(id, "role", req);
        assertEquals(200, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

     // --- getIndividuaByName ---

    @Test
    void getIndividualByUserName_notFound_returnsNullBody() {
        String userName = "u1";
        when(queryPort.retrieveUserByName(userName)).thenReturn(null);

        ResponseEntity<List<IndividualTMF632>> resp = controller.getIndividualByUserName(userName, "role");
        assertEquals(200, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    void getIndividualByUserName_found_returnsEntity() {
        String userName = "u2";
        IndividualTMF632 ind = new IndividualTMF632();
        ind.setName(userName);

        when(queryPort.retrieveUserByName(userName)).thenReturn(ind);

        ResponseEntity<List<IndividualTMF632>> resp = controller.getIndividualByUserName(userName, "role");
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals(List.of(ind), resp.getBody());
    }
}
