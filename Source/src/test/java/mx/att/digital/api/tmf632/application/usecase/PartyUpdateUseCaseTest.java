package mx.att.digital.api.tmf632.application.usecase;

import mx.att.digital.api.tmf632.application.port.out.CustomSCIM2ConnectorPort;
import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.application.usecase.util.IndividualTMF632ResponseUtil;
import mx.att.digital.api.tmf632.application.usecase.util.PartyQueryUseCaseUtils;
import mx.att.digital.api.tmf632.application.usecase.util.SCIAM2Util;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyUpdateUseCaseTest {

    @Mock
    CustomSCIM2ConnectorPort customSCIM2;
    @Mock
    UserConnectorPort userConnectorClient;

    private PartyUpdateUseCase useCase;

    @BeforeEach
    void init() {
        useCase = new PartyUpdateUseCase(customSCIM2, userConnectorClient);
    }

    @Test
    void updateUser_empty_returnsNull() {
        assertEquals(null, useCase.updateUser("", new IndividualTMF632()));
    }

    @Test
    void updateUser_success_usesPartyQueryUtils_andResponseUtil() {
        String id = "ID1";
        IndividualTMF632 req      = new IndividualTMF632();
        SCIM2User existing  = new SCIM2User();    existing.setUserId(id);
        SCIM2User      updated   = new SCIM2User();    updated .setUserId(id);
        IndividualTMF632 built    = new IndividualTMF632();

        when(customSCIM2.retrieveUserInfoById(id)).thenReturn(existing);

        try (var sci = mockStatic(SCIAM2Util.class);
             var pcuc = mockConstruction(PartyQueryUseCaseUtils.class,
                     (mock, ctx) -> when(mock.updateExtendedDatabase(eq(id), eq(req)))
                             .thenReturn(List.of(new PartialError()))
             );
             var cru = mockConstruction(IndividualTMF632ResponseUtil.class,
                     (mock, ctx) -> when(mock.buildIndividualResponse(any(), any(), anyList()))
                             .thenReturn(built)
             ) ) {

            lenient()
                    .when(customSCIM2.updateUserById(eq(id), any(SCIM2User.class)))
                    .thenReturn(updated);

            when(userConnectorClient.retrieveUserById(id))
                    .thenReturn(new UserConnector());

            IndividualTMF632 out = useCase.updateUser(id, req);
            assertSame(built, out);
        }
    }

}