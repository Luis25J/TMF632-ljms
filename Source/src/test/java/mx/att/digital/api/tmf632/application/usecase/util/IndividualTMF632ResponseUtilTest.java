package mx.att.digital.api.tmf632.application.usecase.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.AbstractPartyCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorProfile;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorConsent;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorContact;

class IndividualTMF632ResponseUtilTest {

    private final IndividualTMF632ResponseUtil util = new IndividualTMF632ResponseUtil();

    @Test
    void buildIndividualResponse_nullConnector_addsError() {
        var scim = new SCIM2User();
        scim.setUserId("U1");
        scim.setFirstName("FN");
        scim.setLastName("LN");

        // no connector, no errors list passed
        IndividualTMF632 result = util.buildIndividualResponse(scim, null, null);

        // basic SCIM2User mappings
        assertEquals("U1", result.getId());
        assertEquals("/partyManagement/v5/individual/U1", result.getHref());
        assertEquals("FN", result.getGivenName());
        assertEquals("LN", result.getFamilyName());
        assertEquals("FN LN", result.getFullName());

        // error was added
        var errs = result.getPartialErrors();
        assertNotNull(errs);
        assertEquals(1, errs.size());
        assertEquals("02", errs.getFirst().getCode());
        assertTrue(errs.getFirst().getMessage().contains("does not exist"));
        assertNull(result.getContactMedium());
        assertNull(result.getPartyCharacteristic());
    }

    @Test
    void buildIndividualResponse_fullConnector_mapsAll() throws Exception {
        var scim = new SCIM2User();
        scim.setUserId("ID2");
        scim.setFirstName("A");
        scim.setLastName("B");
        scim.setUrlPhoto("urlX");
        scim.setPassword("pwdX");
        scim.setUserName("userX");

        UserConnectorProfile profile = new UserConnectorProfile();
        profile.setBirthDate("1980-01-01");
        profile.setAlias("Nick");
        // connector with all data
        UserConnector conn = new UserConnector();
        conn.setProfile(profile);
        conn.setMsisdn("999");
        conn.setEmail("e@e");
        conn.setInterestedTAGs(List.of("t1"));
        conn.setAddresses(List.of(
                new UserConnectorAddress("AID","PC","NB","ST","MN","SN","RF",true,
                        "2023-01-01T00:00:00Z","2023-01-02T00:00:00Z")
        ));
        conn.setConsents(List.of(
                new UserConnectorConsent("CID","CT","OK",
                        "2023-01-01T00:00:00Z","2023-01-02T00:00:00Z")
        ));
        conn.setContacts(List.of(
                new UserConnectorContact("AL","CID2","PH2", false,
                        "2023-01-01T00:00:00Z","2023-01-02T00:00:00Z")
        ));

        IndividualTMF632 result = util.buildIndividualResponse(scim, conn, new ArrayList<>());

        assertEquals("1980-01-01", result.getBirthDate());
        assertEquals("Nick", result.getPreferredGivenName());

        assertEquals(3, result.getContactMedium().size());
        assertTrue(result.getContactMedium().stream()
                .anyMatch(cm -> "PhoneContactMedium".equals(cm.getType()) && "999".equals(cm.getPhoneNumber())));
        assertTrue(result.getContactMedium().stream()
                .anyMatch(cm -> "EmailContactMedium".equals(cm.getType()) && "e@e".equals(cm.getEmailAddress())));
        assertTrue(result.getContactMedium().stream()
                .anyMatch(cm -> "GeographicAddressContactMedium".equals(cm.getType()) && "PC".equals(cm.getPostCode())));

        List<AbstractPartyCharacteristic> chars = result.getPartyCharacteristic();
        assertEquals(6, chars.size());
        assertTrue(chars.stream().anyMatch(ch -> "avatarPicture".equals(ch.getName())));
        assertTrue(chars.stream().anyMatch(ch -> "password".equals(ch.getName())));
        assertTrue(chars.stream().anyMatch(ch -> "userName".equals(ch.getName())));
        assertTrue(chars.stream().anyMatch(ch -> "interestingItem".equals(ch.getName())));
        assertTrue(chars.stream().anyMatch(ch -> "consents".equals(ch.getName())));
        assertTrue(chars.stream().anyMatch(ch -> "contacts".equals(ch.getName())));

        assertNull(result.getPartialErrors());
    }

    @Test
    void toMap_nullAndNonNull_returnsExpected() throws Exception {
        Method toMap = IndividualTMF632ResponseUtil.class
                .getDeclaredMethod("toMap", Object.class, Class.class);
        toMap.setAccessible(true);

        @SuppressWarnings("unchecked")
        var empty = (Map<String,Object>) toMap.invoke(util, null, PartialError.class);
        assertTrue(empty.isEmpty());

        PartialError pe = new PartialError();
        pe.setCode("X");
        pe.setMessage("M");
        @SuppressWarnings("unchecked")
        var map = (Map<String,Object>) toMap.invoke(util, pe, PartialError.class);

        assertEquals("X", map.get("code"));
        assertEquals("M", map.get("message"));
    }
}
