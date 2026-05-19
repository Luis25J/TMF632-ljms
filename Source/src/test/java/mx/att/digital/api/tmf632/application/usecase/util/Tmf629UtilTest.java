package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse.TMF629Characteristic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Tmf629UtilTest {

    @Test
    void returnsFalseWhenResponseIsNull() {
        assertFalse(Tmf629Util.isValidPaymentCategory(null));
    }

    @Test
    void returnsFalseWhenResponseIsEmpty() {
        assertFalse(Tmf629Util.isValidPaymentCategory(emptyList()));
    }

    @Test
    void returnsFalseWhenNoCharacteristics() {
        TMF629CustomResponse resp = mock(TMF629CustomResponse.class);
        when(resp.getCharacteristic()).thenReturn(null);
        assertFalse(Tmf629Util.isValidPaymentCategory(List.of(resp)));

        // also empty list of characteristics
        when(resp.getCharacteristic()).thenReturn(emptyList());
        assertFalse(Tmf629Util.isValidPaymentCategory(List.of(resp)));
    }

    @Test
    void returnsFalseWhenTypeOrNameMismatch() {
        TMF629CustomResponse resp = mock(TMF629CustomResponse.class);
        TMF629Characteristic c = mock(TMF629Characteristic.class);
        when(c.type()).thenReturn("Other");
        when(c.name()).thenReturn("paymentCategory");
        when(resp.getCharacteristic()).thenReturn(List.of(c));
        assertFalse(Tmf629Util.isValidPaymentCategory(List.of(resp)));

        when(c.type()).thenReturn("Characteristic");
        when(c.name()).thenReturn("otherName");
        assertFalse(Tmf629Util.isValidPaymentCategory(List.of(resp)));
    }

    @Test
    void returnsFalseWhenValueTypeNullOrNotAllowed() {
        TMF629CustomResponse resp = mock(TMF629CustomResponse.class);
        TMF629Characteristic c = mock(TMF629Characteristic.class);
        when(c.type()).thenReturn("Characteristic");
        when(c.name()).thenReturn("paymentCategory");
        // null value
        when(c.valueType()).thenReturn(null);
        when(resp.getCharacteristic()).thenReturn(List.of(c));
        assertFalse(Tmf629Util.isValidPaymentCategory(List.of(resp)));

        // unsupported value
        when(c.valueType()).thenReturn("postpaid");
        assertFalse(Tmf629Util.isValidPaymentCategory(List.of(resp)));
    }

    @Test
    void returnsTrueForAllowedPaymentCategories_caseInsensitive() {
        TMF629CustomResponse resp1 = mock(TMF629CustomResponse.class);
        TMF629Characteristic c1 = mock(TMF629Characteristic.class);
        when(c1.type()).thenReturn("Characteristic");
        when(c1.name()).thenReturn("paymentCategory");
        when(c1.valueType()).thenReturn("PREPAGO");
        when(resp1.getCharacteristic()).thenReturn(List.of(c1));

        TMF629CustomResponse resp2 = mock(TMF629CustomResponse.class);
        TMF629Characteristic c2 = mock(TMF629Characteristic.class);
        // mixed list: only one needs to match
        boolean result =
                Tmf629Util.isValidPaymentCategory(List.of(resp1, resp2));
        assertTrue(result);
    }

    @Test
    void allowedPaymentCategoriesConstantContainsExpectedValues() {
        Set<String> allowed = Tmf629Util.ALLOWED_PAYMENT_CATEGORIES;
        assertEquals(Set.of("prepago", "prepaid", "hybrid"), allowed);
        // ensure case-insensitive matching works
        assertTrue(allowed.contains("prePaid".toLowerCase()));
        assertFalse(allowed.contains("postpaid"));
    }
}
