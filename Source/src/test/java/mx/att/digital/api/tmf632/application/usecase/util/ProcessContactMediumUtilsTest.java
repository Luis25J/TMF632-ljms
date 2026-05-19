package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMediumProcessResult;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorAddress;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ValidFor;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.MediumCharacteristic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessContactMediumUtilsTest {

    private ProcessContactMediumUtils utils;
    private IndividualTMF632 request;
    private UserConnector userConnector;

    @BeforeEach
    void setUp() {
        utils = new ProcessContactMediumUtils();
        request = mock(IndividualTMF632.class);
        userConnector = new UserConnector();
    }

    @Test
    void whenContactMediumIsNull_returnsEmptyResultAndNoMutation() {
        when(request.getContactMedium()).thenReturn(null);

        ContactMediumProcessResult result =
                utils.processContactMedium(request, userConnector);

        assertTrue(result.lstRequestUserAddress().isEmpty());
        assertTrue(result.lstAllAddress().isEmpty());
        assertTrue(result.lstIdsForRemove().isEmpty());
        // userConnector untouched
        assertNull(userConnector.getMsisdn());
        assertNull(userConnector.getEmail());
    }

    @Test
    void mapsPhoneAndEmail_andCollectsRemovalEntries() {
        ContactMedium cmPhone = new ContactMedium();
        cmPhone.setType("PhoneContactMedium");
        cmPhone.setPhoneNumber("12345");

        ContactMedium cmEmail = new ContactMedium();
        cmEmail.setType("EmailContactMedium");
        cmEmail.setEmailAddress("e@mail");

        ContactMedium cmRemove = new ContactMedium();
        cmRemove.setType("Other");
        cmRemove.setId("ID_REM");
        ValidFor vf = new ValidFor();
        vf.setEndDateTime(Instant.now().minusSeconds(10).toString());
        cmRemove.setValidFor(vf);

        when(request.getContactMedium())
                .thenReturn(List.of(cmPhone, cmEmail, cmRemove));

        ContactMediumProcessResult result =
                utils.processContactMedium(request, userConnector);

        // mapPhoneOrEmail
        assertEquals("12345", userConnector.getMsisdn());
        assertEquals("e@mail", userConnector.getEmail());

        // removal
        assertEquals(1, result.lstIdsForRemove().size());
        Map<String, String> removal = result.lstIdsForRemove().getFirst();
        assertEquals("ID_REM", removal.get("addresses"));

        // no geographic entries
        assertTrue(result.lstAllAddress().isEmpty());
        assertTrue(result.lstRequestUserAddress().isEmpty());
    }

    @Test
    void collectsGeographicAddress_withoutMediumCharacteristic() {
        ContactMedium geo = new ContactMedium();
        geo.setType("GeographicAddressContactMedium");
        geo.setId("G1");
        geo.setCity("CityX");
        geo.setPostCode("P123");
        geo.setStreet1("Main St");

        when(request.getContactMedium()).thenReturn(singletonList(geo));

        ContactMediumProcessResult result =
                utils.processContactMedium(request, userConnector);

        assertTrue(result.lstIdsForRemove().isEmpty());

        // one address built
        List<UserConnectorAddress> addrs = result.lstAllAddress();
        assertEquals(1, addrs.size());
        UserConnectorAddress addr = addrs.getFirst();
        assertEquals("G1", addr.getAddressId());
        assertEquals("CityX", addr.getState());
        assertEquals("P123", addr.getPostalCode());
        assertEquals("Main St", addr.getStreetAndNumber());
        assertNull(addr.getNeighborhood());
        assertNull(addr.getMunicipality());
        assertNull(addr.getReference());

        // one request wrapping that address
        List<UserConnectorRequest> reqs = result.lstRequestUserAddress();
        assertEquals(1, reqs.size());
        UserConnectorRequest r0 = reqs.getFirst();
        assertEquals(addr, r0.getUser().getAddresses().getFirst());
    }

    @Test
    void collectsGeographicAddress_withMediumCharacteristic() {
        ContactMedium geo = new ContactMedium();
        geo.setType("GeographicAddressContactMedium");
        geo.setId("G2");
        geo.setCity("CityY");
        geo.setPostCode("P456");
        geo.setStreet1("2nd St");
        MediumCharacteristic mc = new MediumCharacteristic();
        mc.setNeighborhood("Nbrhd");
        mc.setMunicipality("Mun");
        mc.setReference("Ref");
        geo.setMediumCharacteristic(mc);

        when(request.getContactMedium()).thenReturn(singletonList(geo));

        ContactMediumProcessResult result =
                utils.processContactMedium(request, userConnector);

        UserConnectorAddress addr = result.lstAllAddress().getFirst();
        assertEquals("Nbrhd", addr.getNeighborhood());
        assertEquals("Mun", addr.getMunicipality());
        assertEquals("Ref", addr.getReference());
    }
}
