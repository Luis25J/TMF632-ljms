package mx.att.digital.api.tmf632.application.usecase;

import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.TMF629CustomerConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632ResponseUtil;
import mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632Util;
import mx.att.digital.api.tmf632.application.usecase.util.SCIAM2Util;
import mx.att.digital.api.tmf632.infrastructure.exception.ValidationResponseException;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Contact;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Request;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Response;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyCreateUseCaseTest {

    @Mock
    CustomSCIM2ConnectorPort customSCIM2;
    @Mock
    UserConnectorPort userConnectorClient;
    @Mock
    TMF629CustomerConnectorPort tmf629Port;

    private PartyCreateUseCase useCase;

    @BeforeEach
    void init() {
        useCase = new PartyCreateUseCase(
                customSCIM2, userConnectorClient, tmf629Port
        );
    }

    @Test
    void createUser_nonBlankId_invokesAllSubsteps_andReturnsModifiedRequest() {
        IndividualTMF632 req = spy(new IndividualTMF632());
        req.setId("ID");
        req.setPartyCharacteristic(new ArrayList<>());
        Contact c = new Contact(); c.setAlias("A"); c.setContactPhone("P");
        try (MockedStatic<IndividualTMF632Util> cid =
                     mockStatic(mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632Util.class)) {
            cid.when(() ->
                            mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632Util.getContactsFromRequest(req))
                    .thenReturn(List.of(c));
            when(tmf629Port.getUser("P")).thenReturn(List.of(new TMF629CustomResponse()));
            lenient().when(userConnectorClient.addAddressUser(any()))
                    .thenReturn(new UserConnectorResponse());
            lenient().when(userConnectorClient.addConsentsUser(any()))
                    .thenReturn(new UserConnectorResponse());
            lenient().when(userConnectorClient.addContactsUser(any()))
                    .thenReturn(new UserConnectorResponse());

            IndividualTMF632 out = useCase.createUser("role", req);
            assertSame(req, out);
            assertFalse(out.getPartialErrors().isEmpty());
        }
    }

    @Test
    void createUser_blankId_successAndThrowsOnConnectorFailure() {
        final IndividualTMF632 req = spy(new IndividualTMF632());
        req.setPartyCharacteristic(new ArrayList<>());      // evito NPE en req

        SCIM2Request cr = new SCIM2Request();
        SCIM2User crUser = new SCIM2User();
        crUser.setPhone("P");                                // teléfono para que no sea null
        cr.setUser(crUser);

        SCIM2Response resp = new SCIM2Response();
        resp.setUser(new SCIM2User());
        resp.getUser().setUserId("U");

        when(customSCIM2.addUser(cr)).thenReturn(resp);

        try (var sci = mockStatic(SCIAM2Util.class);
             var cid = mockStatic(IndividualTMF632Util.class);
             var cru = mockConstruction(IndividualTMF632ResponseUtil.class) ) {

            sci.when(() -> SCIAM2Util.getCIAMRequestToCreate(req)).thenReturn(cr);
            cid.when(() -> IndividualTMF632Util.getContactsFromRequest(req))
                    .thenReturn(Collections.emptyList());

            when(userConnectorClient.createUser(any())).thenReturn(null);
            assertThrows(ValidationResponseException.class,
                    () -> useCase.createUser("role", req));

            when(userConnectorClient.createUser(any()))
                    .thenReturn(new UserConnectorResponse());

            final IndividualTMF632 req2 = spy(new IndividualTMF632());
            req2.setPartyCharacteristic(new ArrayList<>());  // ¡no te olvides de este set!
            sci.when(() -> SCIAM2Util.getCIAMRequestToCreate(req2)).thenReturn(cr);
            cid.when(() -> IndividualTMF632Util.getContactsFromRequest(req2))
                    .thenReturn(Collections.emptyList());

            IndividualTMF632 ok = useCase.createUser("role", req2);
            assertEquals("U", ok.getId());
        }
    }

}