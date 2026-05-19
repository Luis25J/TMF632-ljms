package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMediumProcessResult;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyQueryUseCaseUtilsTest {

    @Mock
    private ProcessContactMediumUtils processContactMediumUtils;

    @Mock
    private ProcessPartyCharacteristicsUtils processPartyCharacteristicsUtils;

    @Mock
    private ExecuteUpdates executeUpdates;

    @InjectMocks
    private PartyQueryUseCaseUtils utils;

    @Mock
    private IndividualTMF632 individualRequest;

    @Mock
    private ContactMediumProcessResult contactResult;

    @Mock
    private PartyCharacteristicProcessResult partyResult;

     @Mock
    private List<Map<String, String>> removeList;

    @BeforeEach
    void setUp() {
        // stub processing steps
        when(processContactMediumUtils
             .processContactMedium(eq(individualRequest), any(UserConnector.class)))
            .thenReturn(contactResult);
        when(processPartyCharacteristicsUtils
             .processPartyCharacteristics(eq(individualRequest), any(UserConnector.class)))
            .thenReturn(partyResult);
    }

    @Test
    void updateExtendedDatabase_combinesAllErrors() {
        String id = "id123";
        PartialError e1 = mock(PartialError.class);
        PartialError e2 = mock(PartialError.class);
        PartialError e3 = mock(PartialError.class);
        PartialError e4 = mock(PartialError.class);
        PartialError e5 = mock(PartialError.class);

        when(contactResult.lstIdsForRemove()).thenReturn(removeList);
        when(partyResult.lstIdsForRemove()).thenReturn(removeList);

        when(executeUpdates.executeAddressUpdates(
                eq(id), eq(contactResult), eq(partyResult), any(UserConnector.class)))
            .thenReturn(List.of(e1));
        when(executeUpdates.executeConsentUpdates(
                eq(id), eq(contactResult), eq(partyResult), any(UserConnector.class)))
            .thenReturn(List.of(e2));
        when(executeUpdates.executeContactUpdates(
                eq(id), eq(contactResult), eq(partyResult), any(UserConnector.class)))
            .thenReturn(List.of(e3));
        when(executeUpdates.executeFullUserUpdate(
                eq(id), eq(individualRequest), eq(contactResult), eq(partyResult), any(UserConnector.class)))
            .thenReturn(List.of(e4));
        when(executeUpdates.executeDeletions(
                eq(id), eq(removeList), eq(removeList)))
            .thenReturn(List.of(e5));

        List<PartialError> result = utils.updateExtendedDatabase(id, individualRequest);

        assertThat(result).containsExactly(e1, e2, e3, e4, e5);
    }
}
