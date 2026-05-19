package mx.att.digital.api.tmf632.application.usecase.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import mx.att.digital.api.tmf632.application.port.out.UserConnectorPort;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMediumProcessResult;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.*;

/**
 * Unit tests for {@link ExecuteUpdates} without depending on Spring HATEOAS.
 */
@ExtendWith(MockitoExtension.class)
class ExecuteUpdatesTest {

    @Mock UserConnectorPort client;
    @Mock ContactMediumProcessResult cmResult;
    @Mock PartyCharacteristicProcessResult partyResult;

    private ExecuteUpdates svc;
    private static final String ID = "user1";

    @BeforeEach
    void setUp() {
        svc = new ExecuteUpdates(client);
    }

    // --- executeAddressUpdates ---

    @Test
    void executeAddressUpdates_notOnlyAddresses_returnsEmpty() {
        when(cmResult.lstRequestUserAddress()).thenReturn(List.of());
        var emptyUC = new UserConnector();
        var result = svc.executeAddressUpdates(ID, cmResult, partyResult, emptyUC);
        assertTrue(result.isEmpty());
    }

    @Test
    void executeAddressUpdates_successAndError() {
        // mock a request with deep stubs for getUser().getAddresses().getFirst().getAddressId()
        var reqGood = mock(UserConnectorRequest.class, RETURNS_DEEP_STUBS);
        when(reqGood.getUser().getAddresses().getFirst().getAddressId()).thenReturn("A1");
        var reqBad  = mock(UserConnectorRequest.class, RETURNS_DEEP_STUBS);
        when(reqBad .getUser().getAddresses().getFirst().getAddressId()).thenReturn("B2");

        when(cmResult.lstRequestUserAddress()).thenReturn(List.of(reqGood, reqBad));
        when(partyResult.lstRequestConsent()).thenReturn(List.of());
        var emptyUC = new UserConnector();

        // A1 → success, B2 → error
        when(client.updateResource(ID, "A1", "addresses", reqGood))
                .thenReturn(new UserConnectorResponse("00", "ok", ID));
        when(client.updateResource(ID, "B2", "addresses", reqBad))
                .thenReturn(new UserConnectorResponse("99", "fail", ID));

        var errs = svc.executeAddressUpdates(ID, cmResult, partyResult, emptyUC);
        assertEquals(1, errs.size());
        assertEquals("99", errs.getFirst().getCode());
        assertTrue(errs.getFirst().getPath().contains("id: B2"));
    }

    // --- executeConsentUpdates ---

    @Test
    void executeConsentUpdates_notOnlyConsents_returnsEmpty() {
        when(cmResult.lstRequestUserAddress()).thenReturn(List.of());
        when(partyResult.lstRequestConsent()).thenReturn(List.of());
        var emptyUC = new UserConnector();
        assertTrue(svc.executeConsentUpdates(ID, cmResult, partyResult, emptyUC).isEmpty());
    }

    @Test
    void executeConsentUpdates_errorLoggedAndReturned() {
        var req = mock(UserConnectorRequest.class, RETURNS_DEEP_STUBS);
        when(req.getUser().getConsents().getFirst().getConsentId()).thenReturn("C1");

        when(cmResult.lstRequestUserAddress()).thenReturn(List.of());
        when(partyResult.lstRequestConsent()).thenReturn(List.of(req));
        when(partyResult.lstRequestContact()).thenReturn(List.of());
        var emptyUC = new UserConnector();

        when(client.updateResource(ID, "C1", "consents", req))
                .thenReturn(new UserConnectorResponse("42", "ko", ID));

        var errs = svc.executeConsentUpdates(ID, cmResult, partyResult, emptyUC);
        assertEquals(1, errs.size());
        assertEquals("42", errs.getFirst().getCode());
        assertTrue(errs.getFirst().getPath().contains("consentId: C1"));
    }

    // --- executeContactUpdates ---

    @Test
    void executeContactUpdates_notOnlyContacts_returnsEmpty() {
        when(cmResult.lstRequestUserAddress()).thenReturn(List.of());
        when(partyResult.lstRequestConsent()).thenReturn(List.of());
        when(partyResult.lstRequestContact()).thenReturn(List.of());
        assertTrue(svc.executeContactUpdates(ID, cmResult, partyResult, new UserConnector()).isEmpty());
    }

    @Test
    void executeContactUpdates_errorReturned() {
        var req = mock(UserConnectorRequest.class, RETURNS_DEEP_STUBS);
        when(req.getUser().getContacts().getFirst().getContactId()).thenReturn("X7");

        when(cmResult.lstRequestUserAddress()).thenReturn(List.of());
        when(partyResult.lstRequestConsent()).thenReturn(List.of());
        when(partyResult.lstRequestContact()).thenReturn(List.of(req));
        var emptyUC = new UserConnector();

        when(client.updateResource(ID, "X7", "contacts", req))
                .thenReturn(new UserConnectorResponse("E1", "error", ID));

        var errs = svc.executeContactUpdates(ID, cmResult, partyResult, emptyUC);
        assertEquals(1, errs.size());
        assertEquals("E1", errs.getFirst().getCode());
        assertTrue(errs.getFirst().getPath().contains("contactId: X7"));
    }

    // --- executeFullUserUpdate ---

    @Test
    void executeFullUserUpdate_userEmpty_returnsEmpty() {
        var emptyUC = new UserConnector();
        var result = svc.executeFullUserUpdate(ID, new IndividualTMF632(), cmResult, partyResult, emptyUC);
        assertTrue(result.isEmpty());
    }

    @Test
    void executeFullUserUpdate_successAndError() {
        var domain = new IndividualTMF632();
        domain.setBirthDate("BD");
        domain.setPreferredGivenName("PN");
        var uc = new UserConnector();
        uc.setMsisdn("m");  // non-empty to avoid empty-check

        when(cmResult.lstAllAddress()).thenReturn(List.of(new UserConnectorAddress()));
        when(partyResult.lstAllConsents()).thenReturn(List.of(new UserConnectorConsent()));
        when(partyResult.lstAllContacts()).thenReturn(List.of(new UserConnectorContact()));

        // success
        when(client.updateUser(eq(ID), any()))
                .thenReturn(new UserConnectorResponse("00","ok", ID));
        var ok = svc.executeFullUserUpdate(ID, domain, cmResult, partyResult, uc);
        assertTrue(ok.isEmpty());
        assertEquals("BD", uc.getProfile().getBirthDate());
        assertEquals("PN", uc.getProfile().getAlias());

        // failure
        when(client.updateUser(eq(ID), any()))
                .thenReturn(new UserConnectorResponse("X9","fail", ID));
        var failed = svc.executeFullUserUpdate(ID, domain, cmResult, partyResult, uc);
        assertEquals(1, failed.size());
        assertEquals("X9", failed.getFirst().getCode());
    }

    // --- executeDeletions ---

    @Test
    void executeDeletions_emptyLists_returnsEmpty() {
        assertTrue(svc.executeDeletions(ID, List.of(), List.of()).isEmpty());
    }

    @Test
    void executeDeletions_mixedMaps_successAndError() {
        var okMap  = Map.<String,String>of("contacts","A1");
        var badMap = Map.<String,String>of("consents","B2");

        when(client.deleteResource(ID, "A1", "contacts"))
                .thenReturn(new UserConnectorResponse("00","ok", ID));
        when(client.deleteResource(ID, "B2", "consents"))
                .thenReturn(new UserConnectorResponse("Z9","err", ID));

        var errs = svc.executeDeletions(ID, List.of(okMap), List.of(badMap));
        assertEquals(1, errs.size());
        assertEquals("Z9", errs.getFirst().getCode());
        assertTrue(errs.getFirst().getPath().contains("consentsId"));
    }
}
