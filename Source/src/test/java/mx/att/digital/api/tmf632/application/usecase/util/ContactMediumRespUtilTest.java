package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.application.usecase.util.ContactMediumRespUtil;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.MediumCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ValidFor;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactMediumRespUtilTest {

    @Test
    void returnsEmptyList_whenUserConnectorIsNull() {
        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyList_whenAllFieldsBlankOrNull() {
        UserConnector user = new UserConnector();
        user.setMsisdn("   ");
        user.setEmail("");
        user.setAddresses(null);

        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(user);
        assertTrue(result.isEmpty());
    }

    @Test
    void addsPhoneEntry_whenMsisdnPresent() {
        UserConnector user = new UserConnector();
        user.setMsisdn("5551234");
        user.setEmail(null);
        user.setAddresses(null);

        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(user);
        assertEquals(1, result.size());
        ContactMedium cm = result.get(0);
        assertEquals("PhoneContactMedium", cm.getType());
        assertTrue(cm.getPreferred());
        assertEquals("registerPhone", cm.getMediumType());
        assertEquals("5551234", cm.getPhoneNumber());
    }

    @Test
    void addsEmailEntry_whenEmailPresent() {
        UserConnector user = new UserConnector();
        user.setMsisdn(null);
        user.setEmail("a@b.com");
        user.setAddresses(null);

        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(user);
        assertEquals(1, result.size());
        ContactMedium cm = result.get(0);
        assertEquals("EmailContactMedium", cm.getType());
        assertTrue(cm.getPreferred());
        assertEquals("registerEmail", cm.getMediumType());
        assertEquals("a@b.com", cm.getEmailAddress());
    }

    @Test
    void addsPhoneAndEmail_whenBothPresent() {
        UserConnector user = new UserConnector();
        user.setMsisdn("123");
        user.setEmail("e@f.com");
        user.setAddresses(null);

        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(user);
        assertEquals(2, result.size());

        ContactMedium phone = result.get(0);
        assertEquals("PhoneContactMedium", phone.getType());
        assertEquals("123", phone.getPhoneNumber());

        ContactMedium email = result.get(1);
        assertEquals("EmailContactMedium", email.getType());
        assertEquals("e@f.com", email.getEmailAddress());
    }

    @Test
    void addsGeographicEntry_whenAddressesPresent() {
        UserConnectorAddress addr = new UserConnectorAddress();
        addr.setAddressId("A1");
        addr.setState("City");
        addr.setPostalCode("00000");
        addr.setStreetAndNumber("Main St");
        addr.setNeighborhood("NB");
        addr.setMunicipality("MN");
        addr.setReference("REF");
        // createdAt must be at least 19 chars
        addr.setCreatedAt("2020-01-02T03:04:05XYZ");

        UserConnector user = new UserConnector();
        user.setMsisdn(null);
        user.setEmail(null);
        user.setAddresses(List.of(addr));

        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(user);
        assertEquals(1, result.size());

        ContactMedium geo = result.get(0);
        assertEquals("GeographicAddressContactMedium", geo.getType());
        assertTrue(geo.getPreferred());
        assertEquals("addressList", geo.getMediumType());
        // validFor endDateTime = substring(0,19)+"Z"
        ValidFor vf = geo.getValidFor();
        assertEquals("2020-01-02T03:04:05Z", vf.getEndDateTime());
        assertEquals("City", geo.getCity());
        assertEquals("00000", geo.getPostCode());
        assertEquals("Main St", geo.getStreet1());

        MediumCharacteristic mc = geo.getMediumCharacteristic();
        assertNotNull(mc);
        assertEquals("GeographicAddressMX", mc.getType());
        assertEquals("NB", mc.getNeighborhood());
        assertEquals("MN", mc.getMunicipality());
        assertEquals("REF", mc.getReference());
    }

    @Test
    void combinesAllEntries_inCorrectOrder() {
        UserConnectorAddress addr = new UserConnectorAddress();
        addr.setAddressId("X");
        addr.setState("S");
        addr.setPostalCode("P");
        addr.setStreetAndNumber("St");
        addr.setCreatedAt("2021-11-11T11:11:11AAA");
        addr.setNeighborhood("N");
        addr.setMunicipality("M");
        addr.setReference("R");

        UserConnector user = new UserConnector();
        user.setMsisdn("MNO");
        user.setEmail("z@y.com");
        user.setAddresses(List.of(addr));

        List<ContactMedium> result = ContactMediumRespUtil.buildListContactMedium(user);
        assertEquals(3, result.size());
        assertEquals("PhoneContactMedium", result.get(0).getType());
        assertEquals("EmailContactMedium", result.get(1).getType());
        assertEquals("GeographicAddressContactMedium", result.get(2).getType());
    }
}
