package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorContact;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorRequest;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorConsent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyCharacteristicProcessResultTest {

    private UserConnectorRequest req1;
    private UserConnectorRequest req2;
    private UserConnectorConsent consent1;
    private UserConnectorConsent consent2;
    private UserConnectorContact contact1;
    private UserConnectorContact contact2;
    private List<Map<String, String>> idsForRemove;

    @BeforeEach
    void setUp() {
        // create mocks for list elements
        req1 = mock(UserConnectorRequest.class);
        req2 = mock(UserConnectorRequest.class);
        consent1 = mock(UserConnectorConsent.class);
        consent2 = mock(UserConnectorConsent.class);
        contact1 = mock(UserConnectorContact.class);
        contact2 = mock(UserConnectorContact.class);

        idsForRemove = List.of(
            Map.of("tenantId", "t1"),
            Map.of("tenantId", "t2")
        );
    }

    @Test
    void getters_and_toString_includeAllFields() {
        var rec = new PartyCharacteristicProcessResult(
            List.of(req1, req2),
            List.of(req2),
            List.of(consent1, consent2),
            List.of(contact1),
            idsForRemove
        );

        // getters
        assertThat(rec.lstRequestConsent())
            .containsExactly(req1, req2);
        assertThat(rec.lstRequestContact())
            .containsExactly(req2);
        assertThat(rec.lstAllConsents())
            .containsExactly(consent1, consent2);
        assertThat(rec.lstAllContacts())
            .containsExactly(contact1);
        assertThat(rec.lstIdsForRemove())
            .isEqualTo(idsForRemove);

        // toString should mention all field names and contents
        String s = rec.toString();
        assertThat(s).contains(
            "lstRequestConsent=[", 
            "lstRequestContact=[", 
            "lstAllConsents=[", 
            "lstAllContacts=[", 
            "lstIdsForRemove=["
        );
        assertThat(s).contains("t1", "t2");
    }

    @Test
    void equals_and_hashCode_behaveAsRecord() {
        var a = new PartyCharacteristicProcessResult(
            List.of(req1), List.of(req2),
            List.of(consent1), List.of(contact1),
            idsForRemove
        );
        var b = new PartyCharacteristicProcessResult(
            List.of(req1), List.of(req2),
            List.of(consent1), List.of(contact1),
            idsForRemove
        );
        var c = new PartyCharacteristicProcessResult(
            List.of(req2), List.of(req1),
            List.of(consent2), List.of(contact2),
            idsForRemove
        );

        // reflexive, symmetric
        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(b).isEqualTo(a);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());

        // not equal to different
        assertThat(a).isNotEqualTo(c);
        assertThat(a.hashCode()).isNotEqualTo(c.hashCode());

        // against null and other types
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("some string");
    }
}
