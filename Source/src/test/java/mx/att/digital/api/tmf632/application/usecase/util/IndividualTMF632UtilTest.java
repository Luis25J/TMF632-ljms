package mx.att.digital.api.tmf632.application.usecase.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Consent;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Contact;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ObjectArrayCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Remove;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.StringCharacteristic;

class IndividualTMF632UtilTest {

    @Test
    void getEmailFromRequest_nullAndEmpty() {
        var req = new IndividualTMF632();
        // no contactMedium set → null → should return null
        assertNull(IndividualTMF632Util.getEmailFromRequest(req));

        // set a non-matching ContactMedium
        req.setContactMedium(List.of(new ContactMedium("X", null, null, null, null, null, null, null, null, null, null)));

        assertNull(IndividualTMF632Util.getEmailFromRequest(req));

        // set matching EmailContactMedium
        var emailCm = new ContactMedium();
        emailCm.setType("EmailContactMedium");
        emailCm.setEmailAddress("foo@bar.com");
        req.setContactMedium(List.of(emailCm));
        assertEquals("foo@bar.com", IndividualTMF632Util.getEmailFromRequest(req));
    }

    @Test
    void getPhoneFromRequest() {
        var req = new IndividualTMF632();
        assertNull(IndividualTMF632Util.getPhoneFromRequest(req));

        var phoneCm = new ContactMedium();
        phoneCm.setType("PhoneContactMedium");
        phoneCm.setPhoneNumber("12345");
        req.setContactMedium(List.of(phoneCm));
        assertEquals("12345", IndividualTMF632Util.getPhoneFromRequest(req));
    }

    @Test
    void getUrlPhotoAndPasswordFromRequest() {
        var req = new IndividualTMF632();

        // no partyCharacteristic → null
        assertNull(IndividualTMF632Util.getUrlPhotoFromRequest(req));
        assertNull(IndividualTMF632Util.getPasswordFromRequest(req));

        // add password characteristic
        var pwdCh = new StringCharacteristic();
        pwdCh.setName("password");
        pwdCh.setValue("secret");
        // add avatarPicture characteristic
        var avatarCh = new StringCharacteristic();
        avatarCh.setName("avatarPicture");
        avatarCh.setValue("http://img");
        req.setPartyCharacteristic(List.of(pwdCh, avatarCh));

        assertEquals("secret", IndividualTMF632Util.getPasswordFromRequest(req));
        assertEquals("http://img", IndividualTMF632Util.getUrlPhotoFromRequest(req));
    }

    @Test
    void countConsentsFromRequest() {
        var req = new IndividualTMF632();
        assertEquals(0, IndividualTMF632Util.countConsentsFromRequest(req));

        // add an OAC without matching name
        var oacBad = new ObjectArrayCharacteristic();
        oacBad.setName("other");
        oacBad.setValue(List.of(Map.of("k","v")));
        req.setPartyCharacteristic(List.of(oacBad));
        assertEquals(0, IndividualTMF632Util.countConsentsFromRequest(req));

        // add a matching consents OAC
        var oac = new ObjectArrayCharacteristic();
        oac.setName("consents");
        oac.setValue(List.of(Map.of("id",1), Map.of("id",2)));
        req.setPartyCharacteristic(List.of(oac));
        assertEquals(2, IndividualTMF632Util.countConsentsFromRequest(req));
    }

    @Test
    void getContactsAndConsentsFromRequest() {
        var req = new IndividualTMF632();

        // no party ⇒ empty
        assertTrue(IndividualTMF632Util.getContactsFromRequest(req).isEmpty());
        assertTrue(IndividualTMF632Util.getConsentsFromRequest(req).isEmpty());

        // prepare ObjectArrayCharacteristic for contacts
        var contactMap = Map.<String,Object>of(
                "alias","A", "contactId","C1", "contactPhone","P1"
        );
        var oacContacts = new ObjectArrayCharacteristic();
        oacContacts.setName("contacts");
        oacContacts.setValue(List.of(contactMap));

        // prepare ObjectArrayCharacteristic for consents
        var consentMap = Map.<String,Object>of(
                "consentId","CID", "consentType","T", "status","OK"
        );
        var oacConsents = new ObjectArrayCharacteristic();
        oacConsents.setName("consents");
        oacConsents.setValue(List.of(consentMap));

        req.setPartyCharacteristic(List.of(oacContacts, oacConsents));

        List<Contact> contacts = IndividualTMF632Util.getContactsFromRequest(req);
        assertEquals(1, contacts.size());
        assertEquals("C1", contacts.get(0).getContactId());

        List<Consent> consents = IndividualTMF632Util.getConsentsFromRequest(req);
        assertEquals(1, consents.size());
        assertEquals("CID", consents.get(0).getConsentId());
    }

    @Test
    void getCharsToRemovesFromRequest() {
        var req = new IndividualTMF632();
        assertTrue(IndividualTMF632Util.getCharsToRemovesFromRequest(req).isEmpty());

        // prepare two OACs with name “contacts” and “consents”
        var map1 = Map.<String,Object>of("@op","x");
        var map2 = Map.<String,Object>of("@op","y");
        var oac1 = new ObjectArrayCharacteristic();
        oac1.setName("contacts");
        oac1.setValue(List.of(map1));
        var oac2 = new ObjectArrayCharacteristic();
        oac2.setName("consents");
        oac2.setValue(List.of(map2));

        req.setPartyCharacteristic(List.of(oac1, oac2));

        var removes = IndividualTMF632Util.getCharsToRemovesFromRequest(req);
        assertEquals(2, removes.size());
        assertTrue(removes.stream().allMatch(r -> r instanceof Remove));
    }

    @Test
    void getAddressFromRequest() {
        var req = new IndividualTMF632();
        assertTrue(IndividualTMF632Util.getAddressFromRequest(req).isEmpty());

        var cm1 = new ContactMedium();
        cm1.setType("GeographicAddressContactMedium");
        var cm2 = new ContactMedium();
        cm2.setType("EmailContactMedium");
        req.setContactMedium(List.of(cm1, cm2));

        var list = IndividualTMF632Util.getAddressFromRequest(req);
        assertEquals(1, list.size());
        assertEquals("GeographicAddressContactMedium", list.get(0).getType());
    }
}
