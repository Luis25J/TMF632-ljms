package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Consent;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.MediumCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorConsent;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorContact;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConnectorUtilTest {

    @Test
    void getAddressUser_nullContactMedium_returnsEmptyList() {
        IndividualTMF632 req = mock(IndividualTMF632.class);
        when(req.getContactMedium()).thenReturn(null);

        List<UserConnectorAddress> result = UserConnectorUtil.getAddressUser(req);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAddressUser_filtersAndMapsWithoutMediumCharacteristic() {
        IndividualTMF632 req = mock(IndividualTMF632.class);
        ContactMedium cm = new ContactMedium();
        cm.setType("GeographicAddressContactMedium");
        cm.setId("A1");
        cm.setPostCode("PC");
        cm.setCity("CityX");
        cm.setStreet1("St1");
        // mediumCharacteristic is null
        when(req.getContactMedium()).thenReturn(singletonList(cm));

        List<UserConnectorAddress> list = UserConnectorUtil.getAddressUser(req);
        assertEquals(1, list.size());
        UserConnectorAddress addr = list.get(0);
        assertEquals("A1", addr.getAddressId());
        assertEquals("PC", addr.getPostalCode());
        assertNull(addr.getNeighborhood());
        assertEquals("CityX", addr.getState());
        assertNull(addr.getMunicipality());
        assertEquals("St1", addr.getStreetAndNumber());
        assertNull(addr.getReference());
        assertTrue(addr.getIsActive());

        // createdAt and lastUpdate are equal ISO strings
        assertEquals(addr.getCreatedAt(), addr.getUpdatedAt());
        assertDoesNotThrow(() -> OffsetDateTime.parse(addr.getCreatedAt()));
    }

    @Test
    void getAddressUser_mapsWithMediumCharacteristic() {
        IndividualTMF632 req = mock(IndividualTMF632.class);
        ContactMedium cm = new ContactMedium();
        cm.setType("GeographicAddressContactMedium");
        cm.setId("A2");
        cm.setPostCode("P2");
        cm.setCity("C2");
        cm.setStreet1("S2");
        MediumCharacteristic mc = new MediumCharacteristic();
        mc.setNeighborhood("N2");
        mc.setMunicipality("M2");
        mc.setReference("R2");
        cm.setMediumCharacteristic(mc);
        when(req.getContactMedium()).thenReturn(singletonList(cm));

        UserConnectorAddress addr = UserConnectorUtil.getAddressUser(req).get(0);
        assertEquals("N2", addr.getNeighborhood());
        assertEquals("M2", addr.getMunicipality());
        assertEquals("R2", addr.getReference());
    }

    @Test
    void getConsentsUser_mapsAllFieldsAndTimestamps() {
        // arrange
        IndividualTMF632 req = mock(IndividualTMF632.class);
        Consent consent = mock(Consent.class);
        when(consent.getConsentId()).thenReturn("CID");
        when(consent.getConsentType()).thenReturn("CTYPE");
        when(consent.getStatus()).thenReturn("STAT");

        try (MockedStatic<IndividualTMF632Util> ms =
                     mockStatic(IndividualTMF632Util.class)) {
            ms.when(() -> IndividualTMF632Util.getConsentsFromRequest(req))
                    .thenReturn(List.of(consent));

            List<UserConnectorConsent> result = UserConnectorUtil.getConsentsUser(req);

            assertEquals(1, result.size());
            UserConnectorConsent uc = result.get(0);
            assertEquals("CID", uc.getConsentId());
            assertEquals("CTYPE", uc.getConsentType());
            assertEquals("STAT", uc.getStatus());

            assertEquals(uc.getCreatedAt(), uc.getUpdatedAt());
            assertDoesNotThrow(() -> OffsetDateTime.parse(uc.getCreatedAt()));
        }
    }

    @Test
    void getUserConnectorAddressRequest_buildsCorrectRequest() {
        List<UserConnectorAddress> addrs = singletonList(
                new UserConnectorAddress("X","Y","N","S","M","St","R", true,"c","u"));
        UserConnectorRequest req = UserConnectorUtil.getUserConnectorAddressRequest("UID", addrs);
        assertEquals("UID", req.getUser().getUserId());
        assertSame(addrs, req.getUser().getAddresses());
    }

    @Test
    void getUserConnectorConsentsRequest_buildsCorrectRequest() {
        List<UserConnectorConsent> cons = singletonList(
                new UserConnectorConsent("C","T","S","c","u"));
        UserConnectorRequest req = UserConnectorUtil.getUserConnectorConsentsRequest("ID2", cons);
        assertEquals("ID2", req.getUser().getUserId());
        assertSame(cons, req.getUser().getConsents());
    }

    @Test
    void getUserConnectorContactRequest_buildsCorrectRequest() {
        List<UserConnectorContact> contacts = singletonList(
                new UserConnectorContact("CT","A","P",true,null,null));
        UserConnectorRequest req = UserConnectorUtil.getUserConnectorContactRequest("ID3", contacts);
        assertEquals("ID3", req.getUser().getUserId());
        assertSame(contacts, req.getUser().getContacts());
    }
}
