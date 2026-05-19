package mx.att.digital.api.tmf632.infrastructure.out.response;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrganizationResponseTest {

    @Test
    void gettersAndSetters_workForAllFields() {
        // Prepare values
        String id            = "org-1";
        String type          = "Organization";
        String baseType      = "Party";
        String schemaLoc     = "http://schema";
        String href          = "/org/1";
        String name          = "Acme";
        String status        = "ACTIVE";
        boolean legalEntity  = true;
        String tradingName   = "Acme Trading";
        Map<String, Object> cmEntry = Map.of("k", "v");
        List<Map<String,Object>> contactMedium   = List.of(cmEntry);
        Map<String, Object> charEntry = Map.of("c", 123);
        List<Map<String,Object>> characteristic  = List.of(charEntry);
        OrganizationResponse.TimePeriod period   = new OrganizationResponse.TimePeriod();
        period.setStartDateTime("2020-01-01T00:00:00Z");
        period.setEndDateTime("2030-01-01T00:00:00Z");
        ExternalReference ext = new ExternalReference();
        ext.setExternalIdentifierType("idType");
        ext.setOwner("ownerX");
        ext.setId("ext-1");
        List<ExternalReference> externalReference = List.of(ext);

        // Exercise setters
        OrganizationResponse resp = new OrganizationResponse();
        resp.setId(id);
        resp.setType(type);
        resp.setBaseType(baseType);
        resp.setSchemaLocation(schemaLoc);
        resp.setHref(href);
        resp.setName(name);
        resp.setStatus(status);
        resp.setIsLegalEntity(legalEntity);
        resp.setTradingName(tradingName);
        resp.setContactMedium(contactMedium);
        resp.setCharacteristic(characteristic);
        resp.setExistsDuring(period);
        resp.setExternalReference(externalReference);

        // Verify getters
        assertEquals(id,           resp.getId());
        assertEquals(type,         resp.getType());
        assertEquals(baseType,     resp.getBaseType());
        assertEquals(schemaLoc,    resp.getSchemaLocation());
        assertEquals(href,         resp.getHref());
        assertEquals(name,         resp.getName());
        assertEquals(status,       resp.getStatus());
        assertEquals(legalEntity,  resp.getIsLegalEntity());
        assertEquals(tradingName,  resp.getTradingName());
        assertSame(contactMedium,  resp.getContactMedium());
        assertSame(characteristic, resp.getCharacteristic());
        assertSame(period,         resp.getExistsDuring());
        assertSame(externalReference, resp.getExternalReference());

        // Verify TimePeriod getters
        assertEquals("2020-01-01T00:00:00Z", resp.getExistsDuring().getStartDateTime());
        assertEquals("2030-01-01T00:00:00Z", resp.getExistsDuring().getEndDateTime());
    }

    @Test
    void builder_createsExpectedObject() {
        // Prepare values
        OrganizationResponse.TimePeriod tp = new OrganizationResponse.TimePeriod();
        tp.setStartDateTime("S");
        tp.setEndDateTime("E");
        ExternalReference e1 = new ExternalReference();
        e1.setExternalIdentifierType("T");
        List<ExternalReference> refs = List.of(e1);

        Map<String,Object> m1 = Map.of("x","y");
        List<Map<String,Object>> cm = List.of(m1);
        List<Map<String,Object>> ch = List.of(Map.of("k",1));

        // Use Lombok builder
        OrganizationResponse resp = OrganizationResponse.builder()
                .id("ID")
                .type("T")
                .baseType("B")
                .schemaLocation("L")
                .href("H")
                .name("N")
                .status("S")
                .isLegalEntity(false)
                .tradingName("TN")
                .contactMedium(cm)
                .characteristic(ch)
                .existsDuring(tp)
                .externalReference(refs)
                .build();

        // Assert all fields
        assertEquals("ID", resp.getId());
        assertEquals("T",  resp.getType());
        assertEquals("B",  resp.getBaseType());
        assertEquals("L",  resp.getSchemaLocation());
        assertEquals("H",  resp.getHref());
        assertEquals("N",  resp.getName());
        assertEquals("S",  resp.getStatus());
        assertFalse(resp.getIsLegalEntity());
        assertEquals("TN", resp.getTradingName());
        assertSame(cm,     resp.getContactMedium());
        assertSame(ch,     resp.getCharacteristic());
        assertSame(tp,     resp.getExistsDuring());
        assertSame(refs,   resp.getExternalReference());
    }
}
