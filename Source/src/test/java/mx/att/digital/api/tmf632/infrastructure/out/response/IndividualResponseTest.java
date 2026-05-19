package mx.att.digital.api.tmf632.infrastructure.out.response;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class IndividualResponseTest {

    @Test
    void gettersAndSettersWorkForAllFields() {
        IndividualResponse resp = new IndividualResponse();

        resp.setId("ID123");
        resp.setType("Person");
        resp.setBaseType("Base");
        resp.setSchemaLocation("http://schema");
        resp.setHref("/persons/ID123");
        resp.setName("Alice");
        resp.setStatus("ACTIVE");
        resp.setContactMedium(singletonList(Map.of("phone", "555-1234")));
        resp.setCharacteristic(singletonList(Map.of("age", 30)));
        ExternalReference ext = new ExternalReference();
        ext.setExternalIdentifierType("EXT");
        ext.setId("E1");
        ext.setOwner("OwnerX");
        resp.setExternalReference(singletonList(ext));

        assertEquals("ID123", resp.getId());
        assertEquals("Person", resp.getType());
        assertEquals("Base", resp.getBaseType());
        assertEquals("http://schema", resp.getSchemaLocation());
        assertEquals("/persons/ID123", resp.getHref());
        assertEquals("Alice", resp.getName());
        assertEquals("ACTIVE", resp.getStatus());
        assertEquals(1, resp.getContactMedium().size());
        assertEquals("555-1234", resp.getContactMedium().get(0).get("phone"));
        assertEquals(1, resp.getCharacteristic().size());
        assertEquals(30, resp.getCharacteristic().get(0).get("age"));
        assertEquals(1, resp.getExternalReference().size());
        assertEquals("EXT", resp.getExternalReference().get(0).getExternalIdentifierType());
    }

    @Test
    void jsonSerializationOmitsNullsAndIncludesNonNulls() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        IndividualResponse partial = new IndividualResponse();
        partial.setId("ID_ONLY");
        String jsonPartial = mapper.writeValueAsString(partial);

        assertTrue(jsonPartial.contains("\"id\":\"ID_ONLY\""));
        assertFalse(jsonPartial.contains("name"));
        assertFalse(jsonPartial.contains("@type"));

        IndividualResponse full = new IndividualResponse();
        full.setId("ID");
        full.setType("T");
        full.setBaseType("BT");
        full.setSchemaLocation("SL");
        full.setHref("H");
        full.setName("N");
        full.setStatus("S");
        String jsonFull = mapper.writeValueAsString(full);

        assertTrue(jsonFull.contains("\"id\":\"ID\""));
        assertTrue(jsonFull.contains("\"@type\":\"T\""));
        assertTrue(jsonFull.contains("\"@baseType\":\"BT\""));
        assertTrue(jsonFull.contains("\"@schemaLocation\":\"SL\""));
        assertTrue(jsonFull.contains("\"href\":\"H\""));
        assertTrue(jsonFull.contains("\"name\":\"N\""));
        assertTrue(jsonFull.contains("\"status\":\"S\""));
    }

    @Test
    void jsonDeserializationRespectsPropertyMappings() throws Exception {
        String json = """
            {
              "id":"ID1",
              "@type":"Typ",
              "@baseType":"BTyp",
              "@schemaLocation":"Loc",
              "href":"link",
              "name":"Alice",
              "status":"OK"
            }
            """;

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        IndividualResponse resp = mapper.readValue(json, IndividualResponse.class);

        assertEquals("ID1", resp.getId());
        assertEquals("Typ", resp.getType());
        assertEquals("BTyp", resp.getBaseType());
        assertEquals("Loc", resp.getSchemaLocation());
        assertEquals("link", resp.getHref());
        assertEquals("Alice", resp.getName());
        assertEquals("OK", resp.getStatus());
    }
}
