package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.IndividualStateType;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import mx.att.digital.api.tmf632.infrastructure.out.response.ExternalReference;
import mx.att.digital.api.tmf632.infrastructure.out.response.IndividualResponse;
import mx.att.digital.api.tmf632.infrastructure.out.response.OrganizationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyResponseAdapterTest {

    private PartyResponseAdapter adapter;

    @Mock
    private Individual mockIndividual;

    @Mock
    private Organization mockOrganization;

    @BeforeEach
    void setUp() {
        adapter = new PartyResponseAdapter();
    }

    @Test
    void toIndividualDto_returnsNull_whenDomainIsNull() {
        assertNull(adapter.toIndividualDto(null, "anything"));
    }

    @Test
    void toIndividualDto_defaultUserFalse_withAllFields() {
        // Given
        when(mockIndividual.getId()).thenReturn("1234");            // even length -> prepaid
        when(mockIndividual.getName()).thenReturn("John Doe");
        
        IndividualStateType mockStatus = mock(IndividualStateType.class);
        when(mockStatus.getValue()).thenReturn("ACTIVE");

        when(mockIndividual.getStatus()).thenReturn(mockStatus);

        IndividualResponse resp = adapter.toIndividualDto(mockIndividual, "postpaid");
  
        assertNotNull(resp);
        assertEquals("1234", resp.getId());
        assertEquals("/partyManagement/v5/individual/1234", resp.getHref());
        assertEquals("Individual", resp.getType());
        assertEquals("Party", resp.getBaseType());
        assertEquals("https://schemas.company.com/tmf632/Individual", resp.getSchemaLocation());
        assertEquals("John Doe", resp.getName());
        assertEquals("ACTIVE", resp.getStatus());
        assertTrue(resp.getContactMedium().isEmpty());

        List<Map<String, Object>> chars = resp.getCharacteristic();
        assertEquals(1, chars.size());
        Map<String, Object> platformChar = chars.get(0);
        assertEquals("platform", platformChar.get("name"));
        assertEquals("prepaid", platformChar.get("value"));  

        List<ExternalReference> ext = resp.getExternalReference();
        assertEquals(1, ext.size());
        ExternalReference er = ext.get(0);
        assertEquals("partyId", er.getExternalIdentifierType());
        assertEquals("amdocs", er.getOwner());
        assertEquals("AMD-1234", er.getId());
    }

    @Test
    void toIndividualDto_includeDefaultUserTrue_andBlankNameAndNullStatus() {
        // Given odd-length ID -> postpaid, blank name, null status
        when(mockIndividual.getId()).thenReturn("12345");
        when(mockIndividual.getName()).thenReturn("   ");
        when(mockIndividual.getStatus()).thenReturn(null);

        IndividualResponse resp = adapter.toIndividualDto(mockIndividual, "prepaid", true);

        assertNotNull(resp);
        assertEquals("12345", resp.getId());
        assertNull(resp.getName());
        assertNull(resp.getStatus());

        List<Map<String, Object>> chars = resp.getCharacteristic();
        assertEquals(2, chars.size());
        // first is platform
        assertEquals("platform", chars.get(0).get("name"));
        assertEquals("postpaid", chars.get(0).get("value"));
        // second isDefaultUser
        assertEquals("isDefaultUser", chars.get(1).get("name"));
        Object val = chars.get(1).get("value");
        assertTrue("true".equals(val) || "false".equals(val));

        List<ExternalReference> ext = resp.getExternalReference();
        ExternalReference er = ext.get(0);
        assertEquals("matrixx", er.getOwner());
        assertEquals("MTX-12345", er.getId());
    }

    @Test
    void toOrganizationDto_returnsNull_whenDomainIsNull() {
        assertNull(adapter.toOrganizationDto(null, "any"));
    }

    @Test
    void toOrganizationDto_withBlankName() {
        when(mockOrganization.getId()).thenReturn("ORG1");
        when(mockOrganization.getName()).thenReturn("   ");

        OrganizationResponse resp = adapter.toOrganizationDto(mockOrganization, "postpaid");

        assertEquals("ORG1", resp.getId());
        assertNull(resp.getName());
        assertNull(resp.getTradingName());
        assertFalse(resp.getIsLegalEntity());
        assertTrue(resp.getContactMedium().isEmpty());
        assertTrue(resp.getCharacteristic().isEmpty());

        OrganizationResponse.TimePeriod tp = resp.getExistsDuring();
        assertEquals("2005-01-01T00:00:00Z", tp.getStartDateTime());
        assertEquals("2040-01-01T00:00:00Z", tp.getEndDateTime());

        ExternalReference er = resp.getExternalReference().get(0);
        assertEquals("amdocs", er.getOwner());
        assertEquals("AMD-ORG1", er.getId());
    }

    @Test
    void toOrganizationDto_withName() {
        when(mockOrganization.getId()).thenReturn("ORG2");
        when(mockOrganization.getName()).thenReturn("Acme Corp");

        OrganizationResponse resp = adapter.toOrganizationDto(mockOrganization, "prepaid");

        assertEquals("Acme Corp", resp.getName());
        assertEquals("Acme Corp", resp.getTradingName());
        ExternalReference er = resp.getExternalReference().get(0);
        assertEquals("matrixx", er.getOwner());
        assertEquals("MTX-ORG2", er.getId());
    }
}
