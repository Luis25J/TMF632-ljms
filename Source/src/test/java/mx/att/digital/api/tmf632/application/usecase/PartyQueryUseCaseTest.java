package mx.att.digital.api.tmf632.application.usecase;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.PostpaidConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.PrepaidConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.TMF629CustomerConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632ResponseUtil;
import mx.att.digital.api.tmf632.application.usecase.util.SCIAM2Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyQueryUseCaseTest {

    @Mock PrepaidConnectorPort prepaid;
    @Mock PostpaidConnectorPort postpaid;
    @Mock CustomSCIM2ConnectorPort customSCIM2;
    @Mock UserConnectorPort userConnectorClient;
    @Mock TMF629CustomerConnectorPort tmf629Port;

    private PartyQueryUseCase useCase;

    @BeforeEach
    void init() {
        useCase = new PartyQueryUseCase(
                prepaid, postpaid, customSCIM2, userConnectorClient, tmf629Port
        );
    }

    // ==== retrieveIndividual ====

    @Test
    void retrieveIndividual_emptyId_returnsNull() {
        assertNull(useCase.retrieveIndividual("", "any"));
    }
    @Test
    void retrieveIndividual_prepaidSegment_usesPrepaid() {
        Individual pre = new Individual();
        when(prepaid.retrieveIndividual("ID")).thenReturn(pre);
        assertSame(pre, useCase.retrieveIndividual("ID", "PREPAID"));
    }
    @Test
    void retrieveIndividual_postpaidSegment_usesPostpaid() {
        Individual pos = new Individual();
        when(postpaid.retrieveIndividual("ID")).thenReturn(pos);
        assertSame(pos, useCase.retrieveIndividual("ID", "PoStPaId"));
    }
    @Test
    void retrieveIndividual_bothConnectorsLogic() {
        Individual a = new Individual(), b = new Individual();
        when(prepaid.retrieveIndividual("ID")).thenReturn(a);
        when(postpaid.retrieveIndividual("ID")).thenReturn(null);
        assertSame(a, useCase.retrieveIndividual("ID", "other"));

        when(prepaid.retrieveIndividual("ID")).thenReturn(null);
        when(postpaid.retrieveIndividual("ID")).thenReturn(b);
        assertSame(b, useCase.retrieveIndividual("ID", "other"));

        when(prepaid.retrieveIndividual("ID")).thenReturn(a);
        when(postpaid.retrieveIndividual("ID")).thenReturn(b);
        assertSame(a, useCase.retrieveIndividual("ID", "other"));

        when(prepaid.retrieveIndividual("ID")).thenReturn(null);
        when(postpaid.retrieveIndividual("ID")).thenReturn(null);
        assertNull(useCase.retrieveIndividual("ID", "other"));
    }

    // ==== retrieveOrganization ====

    @Test
    void retrieveOrganization_emptyId_returnsNull() {
        assertNull(useCase.retrieveOrganization("", "x"));
    }
    @Test
    void retrieveOrganization_prepaid() {
        Organization o = new Organization();
        when(prepaid.retrieveOrganization("ID")).thenReturn(o);
        assertSame(o, useCase.retrieveOrganization("ID", "prepaid"));
    }
    @Test
    void retrieveOrganization_postpaid() {
        Organization o2 = new Organization();
        when(postpaid.retrieveOrganization("ID")).thenReturn(o2);
        assertSame(o2, useCase.retrieveOrganization("ID", "POSTPAID"));
    }
    @Test
    void retrieveOrganization_bothLogic() {
        Organization x = new Organization(), y = new Organization();
        when(prepaid.retrieveOrganization("ID")).thenReturn(x);
        when(postpaid.retrieveOrganization("ID")).thenReturn(null);
        assertSame(x, useCase.retrieveOrganization("ID","other"));
        when(prepaid.retrieveOrganization("ID")).thenReturn(null);
        when(postpaid.retrieveOrganization("ID")).thenReturn(y);
        assertSame(y, useCase.retrieveOrganization("ID","other"));
        when(prepaid.retrieveOrganization("ID")).thenReturn(x);
        when(postpaid.retrieveOrganization("ID")).thenReturn(y);
        assertSame(x, useCase.retrieveOrganization("ID","other"));
        when(prepaid.retrieveOrganization("ID")).thenReturn(null);
        when(postpaid.retrieveOrganization("ID")).thenReturn(null);
        assertNull(useCase.retrieveOrganization("ID","other"));
    }

    // ==== retrieveUser ====

    @Test
    void retrieveUser_emptyId_returnsNull() {
        assertNull(useCase.retrieveUser(""));
    }

    @Test
    void retrieveUser_scimNull_returnsNull() {
        when(customSCIM2.retrieveUserInfoByName("abc")).thenReturn(null);
        assertNull(useCase.retrieveUser("abc"));
    }

    @Test
    void retrieveUser_success_buildsViaResponseUtil() {
        String longId = "A".repeat(31);
        SCIM2User scim = new SCIM2User(); scim.setUserId("UID");
        UserConnector uc = new UserConnector();
        try (MockedStatic<SCIAM2Util> sci = mockStatic(SCIAM2Util.class);
             MockedStatic<IndividualTMF632ResponseUtil> respUtil =
                     mockStatic(IndividualTMF632ResponseUtil.class);
             MockedConstruction<IndividualTMF632ResponseUtil> ctor =
                     mockConstruction(IndividualTMF632ResponseUtil.class,
                             (mock, ctx) -> {
                                 when(mock.buildIndividualResponse(any(), any(), isNull()))
                                         .thenReturn(new IndividualTMF632());
                             })) {
            when(customSCIM2.retrieveUserInfoById(longId)).thenReturn(scim);
            when(userConnectorClient.retrieveUserById("UID")).thenReturn(uc);

            IndividualTMF632 out = useCase.retrieveUser(longId);
            assertNotNull(out);
        }
    }

}
