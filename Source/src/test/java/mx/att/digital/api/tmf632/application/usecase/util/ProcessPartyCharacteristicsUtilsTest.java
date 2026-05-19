package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.*;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ProcessPartyCharacteristicsUtilsTest {

    private ProcessPartyCharacteristicsUtils utils;
    private IndividualTMF632 req;
    private UserConnector user;

    @BeforeEach
    void setUp() {
        utils = new ProcessPartyCharacteristicsUtils();
        req = new IndividualTMF632();
        user = new UserConnector();
    }

    @Test
    void returnsEmptyResult_whenPartyCharacteristicIsNull() {
        req.setPartyCharacteristic(null);

        var result = utils.processPartyCharacteristics(req, user);

        assertTrue(result. lstRequestConsent().isEmpty());
        assertTrue(result.lstRequestContact().isEmpty());
        assertTrue(result.lstAllConsents().isEmpty());
        assertTrue(result.lstAllContacts().isEmpty());
        assertTrue(result.lstIdsForRemove().isEmpty());
    }

    @Test
    void defaultBranch_logsUnknownNameAndNoSideEffect() {
        // a name that doesn't match any case
        StringArrayCharacteristic ch = new StringArrayCharacteristic();
        ch.setName("foo");
        ch.setValue(List.of("X"));
        req.setPartyCharacteristic(List.of(ch));

        var result = utils.processPartyCharacteristics(req, user);

        assertNull(user.getInterestedTAGs());
        assertTrue(result.lstIdsForRemove().isEmpty());
    }

    @Test
    void interestingItem_setsInterestedTAGsOnUserConnector() {
        StringArrayCharacteristic ch = new StringArrayCharacteristic();
        ch.setName("interestingItem");
        ch.setValue(List.of("A", "B", "C"));
        req.setPartyCharacteristic(List.of(ch));

        utils.processPartyCharacteristics(req, user);

        assertEquals(List.of("A", "B", "C"), user.getInterestedTAGs());
    }

    @Test
    void consentsAddition_buildsConsentAndRequest() {
        // crear un map con los campos de Consent
        Map<String,Object> m = new HashMap<>();
        m.put("consentId",   "CID");
        m.put("consentType", "TYPE");
        m.put("status",      "OK");
        m.put("createdAt",   "2021-01-01");
        m.put("updatedAt",   "2022-02-02");
        ObjectArrayCharacteristic ch = new ObjectArrayCharacteristic();
        ch.setName("consents");
        ch.setValue(List.of(m));
        req.setPartyCharacteristic(List.of(ch));

        var result = utils.processPartyCharacteristics(req, user);

        // uno en cada lista
        assertEquals(1, result.lstAllConsents().size());
        assertEquals(1, result.lstRequestConsent().size());
        UserConnectorConsent uc = result.lstAllConsents().get(0);
        assertEquals("CID",   uc.getConsentId());
        assertEquals("TYPE",  uc.getConsentType());
        assertEquals("OK",    uc.getStatus());
        assertEquals("2021-01-01", uc.getCreatedAt());
        assertEquals("2022-02-02", uc.getUpdatedAt());
        // request contiene ese mismo consent
        UserConnectorRequest reqC = result.lstRequestConsent().get(0);
        assertEquals(uc, reqC.getUser().getConsents().get(0));
    }

    @Test
    void contactsAddition_buildsContactAndRequest() {
        Map<String,Object> m = new HashMap<>();
        m.put("contactId",   "CID");
        m.put("alias",       "Ali");
        m.put("contactPhone","P123");
        m.put("isActive",    true);
        m.put("createdAt",   "2020-03-03");
        m.put("updatedAt",   "2020-04-04");
        ObjectArrayCharacteristic ch = new ObjectArrayCharacteristic();
        ch.setName("contacts");
        ch.setValue(List.of(m));
        req.setPartyCharacteristic(List.of(ch));

        var result = utils.processPartyCharacteristics(req, user);

        assertEquals(1, result.lstAllContacts().size());
        assertEquals(1, result.lstRequestContact().size());
        UserConnectorContact cc = result.lstAllContacts().get(0);
        assertEquals("CID",   cc.getContactId());
        assertEquals("Ali",   cc.getAlias());
        assertEquals("P123",  cc.getContactPhone());
        assertTrue(cc.getIsActive());
        assertEquals("2020-03-03", cc.getCreatedAt());
        assertEquals("2020-04-04", cc.getUpdatedAt());
        // request contiene ese mismo contact
        UserConnectorRequest reqP = result.lstRequestContact().get(0);
        assertEquals(cc, reqP.getUser().getContacts().get(0));
    }
}
