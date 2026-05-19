package mx.att.digital.api.tmf632.application.usecase;

import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2UserDelete;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyDeleteUseCaseTest {

    @Mock
    CustomSCIM2ConnectorPort customSCIM2;
    @Mock
    UserConnectorPort userConnectorClient;

    private PartyDeleteUseCase useCase;

    @BeforeEach
    void init() {
        useCase = new PartyDeleteUseCase(customSCIM2, userConnectorClient);
    }

    @Test
    void deleteUser_empty_returnsNull() {
        assertNull(useCase.deleteUser(""));
    }

    @Test
    void deleteUser_shortId_andDeletedTrue_setsInactive() {
        SCIM2User scim = new SCIM2User(); scim.setUserId("U1");
        SCIM2UserDelete del = new SCIM2UserDelete();
        del.setUserId("U1"); del.setDeleted(true);

        when(customSCIM2.retrieveUserInfoById("id"))
                .thenReturn(scim);
        when(customSCIM2.deleteUserById("U1")).thenReturn(del);
        when(userConnectorClient.deleteUser("id"))
                .thenReturn(new UserConnectorResponse());

        IndividualTMF632 res = useCase.deleteUser("id");
        assertEquals("U1", res.getId());
        assertEquals("inactive", res.getStatus());
    }

    @Test
    void deleteUser_longId_andDeletedFalse_returnsEmptyIndividual() {
        String longId = "X".repeat(31);
        SCIM2UserDelete del = new SCIM2UserDelete();
        del.setUserId(longId);
        del.setDeleted(false);

        when(customSCIM2.deleteUserById(longId)).thenReturn(del);
        when(userConnectorClient.deleteUser(longId)).thenReturn(null);

        IndividualTMF632 res = useCase.deleteUser(longId);

        assertNotNull(res);
        assertNull(res.getStatus());
    }

}