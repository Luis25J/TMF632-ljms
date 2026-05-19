package mx.att.digital.api.tmf632.infrastructure.out.response;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExternalReferenceTest {

    @Test
    void gettersAndSettersWorkAsExpected() {
        ExternalReference ref = new ExternalReference();

        ref.setExternalIdentifierType("typeA");
        ref.setId("id123");
        ref.setOwner("ownerX");

        assertEquals("typeA", ref.getExternalIdentifierType());
        assertEquals("id123", ref.getId());
        assertEquals("ownerX", ref.getOwner());
    }

    @Test
    void serializationOmitsNullFields() throws Exception {
        ExternalReference ref = new ExternalReference();
        ref.setId("idOnly");
        // externalIdentifierType and owner remain null

        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        String json = mapper.writeValueAsString(ref);

        assertTrue(json.contains("\"id\":\"idOnly\""));
        assertFalse(json.contains("externalIdentifierType"));
        assertFalse(json.contains("owner"));
    }

    @Test
    void serializationIncludesAllNonNullFields() throws Exception {
        ExternalReference ref = new ExternalReference();
        ref.setExternalIdentifierType("typeB");
        ref.setId("id456");
        ref.setOwner("ownerY");

        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(Include.NON_NULL);

        String json = mapper.writeValueAsString(ref);

        assertTrue(json.contains("\"externalIdentifierType\":\"typeB\""));
        assertTrue(json.contains("\"id\":\"id456\""));
        assertTrue(json.contains("\"owner\":\"ownerY\""));
    }
}
