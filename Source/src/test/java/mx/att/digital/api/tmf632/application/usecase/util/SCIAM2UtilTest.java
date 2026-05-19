package mx.att.digital.api.tmf632.application.usecase.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Reference;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Request;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;

@ExtendWith(MockitoExtension.class)
class SCIAM2UtilTest {

    @Test
    void buildUserToUpdate_withAllFieldsPresent_setsAll() {
        var req = mock(IndividualTMF632.class);
        var user = new SCIM2User();

        when(req.getGivenName()).thenReturn("John");
        when(req.getFamilyName()).thenReturn("Doe");
        try (MockedStatic<IndividualTMF632Util> util = mockStatic(IndividualTMF632Util.class)) {
            util.when(() -> IndividualTMF632Util.getPhoneFromRequest(req)).thenReturn("555-1234");
            util.when(() -> IndividualTMF632Util.getEmailFromRequest(req)).thenReturn("john.doe@example.com");
            util.when(() -> IndividualTMF632Util.getUrlPhotoFromRequest(req)).thenReturn("http://img");

            var updated = SCIAM2Util.buildUserToUpdate(req, user);
            assertSame(user, updated);
            assertEquals("John",    user.getFirstName());
            assertEquals("Doe",     user.getLastName());
            assertEquals("555-1234",user.getPhone());
            assertEquals("john.doe@example.com", user.getEmail());
            assertEquals("http://img", user.getUrlPhoto());
            assertEquals("true",    user.getIsAdmin());
        }
    }

    @Test
    void buildUserToUpdate_withNulls_onlySetsIsAdmin() {
        var req = mock(IndividualTMF632.class);
        var user = new SCIM2User();

        // all getters return null by default
        try (MockedStatic<IndividualTMF632Util> util = mockStatic(IndividualTMF632Util.class)) {
            // static methods return null by default
            var result = SCIAM2Util.buildUserToUpdate(req, user);
            assertSame(user, result);
            assertNull(user.getFirstName());
            assertNull(user.getLastName());
            assertNull(user.getPhone());
            assertNull(user.getEmail());
            assertNull(user.getUrlPhoto());
            assertEquals("true", user.getIsAdmin());
        }
    }

    @Test
    void getCIAMRequestToCreate_withEmailNonBlank_setsEmailAndNameAndAllOptional() {
        var req = mock(IndividualTMF632.class);
        try (MockedStatic<IndividualTMF632Util> util = mockStatic(IndividualTMF632Util.class)) {
            util.when(() -> IndividualTMF632Util.getEmailFromRequest(req)).thenReturn("foo@bar");
            util.when(() -> IndividualTMF632Util.getPhoneFromRequest(req)).thenReturn("321");
            util.when(() -> IndividualTMF632Util.getUrlPhotoFromRequest(req)).thenReturn("picUrl");
            util.when(() -> IndividualTMF632Util.getPasswordFromRequest(req)).thenReturn("pwd");
            util.when(() -> IndividualTMF632Util.countConsentsFromRequest(req)).thenReturn(5);

            when(req.getGivenName()).thenReturn("GName");
            when(req.getFamilyName()).thenReturn("FName");

            SCIM2Request scimReq = SCIAM2Util.getCIAMRequestToCreate(req);
            SCIM2User u = scimReq.getUser();

            assertEquals(new SCIM2Reference("ADBILBAO"), scimReq.getReference());
            assertEquals("foo@bar",         u.getUserName());
            assertEquals("pwd",             u.getPassword());
            assertEquals("S",               u.getConsent());
            assertEquals("5",               u.getNumberConsents());
            assertEquals("true",            u.getIsAdmin());
            assertEquals("foo@bar",         u.getEmail());
            assertEquals("321",             u.getPhone());
            assertEquals("GName",           u.getFirstName());
            assertEquals("FName",           u.getLastName());
            assertEquals("picUrl",          u.getUrlPhoto());
        }
    }

    @Test
    void getCIAMRequestToCreate_withEmailBlank_usesPhoneAndSkipsOptionals() {
        var req = mock(IndividualTMF632.class);
        try (MockedStatic<IndividualTMF632Util> util = mockStatic(IndividualTMF632Util.class)) {
            util.when(() -> IndividualTMF632Util.getEmailFromRequest(req)).thenReturn("");
            util.when(() -> IndividualTMF632Util.getPhoneFromRequest(req)).thenReturn("555");
            util.when(() -> IndividualTMF632Util.getUrlPhotoFromRequest(req)).thenReturn("");
            util.when(() -> IndividualTMF632Util.getPasswordFromRequest(req)).thenReturn("pwd2");
            util.when(() -> IndividualTMF632Util.countConsentsFromRequest(req)).thenReturn(0);

            // blank or null names
            when(req.getGivenName()).thenReturn("");
            when(req.getFamilyName()).thenReturn(null);

            SCIM2Request scimReq = SCIAM2Util.getCIAMRequestToCreate(req);
            SCIM2User u = scimReq.getUser();

            // userName falls back to phone
            assertEquals("555", u.getUserName());
            assertEquals("pwd2", u.getPassword());
            assertEquals("S",    u.getConsent());
            assertEquals("0",    u.getNumberConsents());
            assertEquals("true", u.getIsAdmin());
            // email blank → not set
            assertNull(u.getEmail());
            // phone non-blank → set
            assertEquals("555", u.getPhone());
            // givenName blank & family null → not set
            assertNull(u.getFirstName());
            assertNull(u.getLastName());
            // urlPhoto blank → not set
            assertNull(u.getUrlPhoto());
            // reference correct
            assertEquals(new SCIM2Reference("ADBILBAO"), scimReq.getReference());
        }
    }
}
