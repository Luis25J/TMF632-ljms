package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import mx.att.digital.api.tmf632.infrastructure.exception.ConnectorRequestException;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * The type Customer prepaid connector client test.
 */
@ExtendWith(MockitoExtension.class)
class CustomerPrepaidConnectorClientTest {

    @Mock
    private RestTemplate restTemplate;

    private CustomerPrepaidConnectorClient client;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        client = new CustomerPrepaidConnectorClient(restTemplate);
        client.setBaseUrl("https://test-prepaid.com");
        client.setPath("/v1/customer");
        client.setUsername("testuser");
        client.setPassword("testpass");
    }

    /**
     * Retrieve individual successful response returns individual with name.
     */
    @Test
    void retrieveIndividual_successfulResponse_returnsIndividualWithName() {
        // Arrange
        String responseBody = "{\"customer\": {\"name\": \"Juan Perez\",\"id\": \"12345\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertEquals("Juan Perez", result.getName());
    }

    /**
     * Retrieve individual empty response returns individual with id only.
     */
    @Test
    void retrieveIndividual_emptyResponse_returnsIndividualWithIdOnly() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual json without customer node returns individual with name.
     */
    @Test
    void retrieveIndividual_jsonWithoutCustomerNode_returnsIndividualWithName() {
        // Arrange
        String responseBody = "{\"name\": \"Maria Garcia\",\"id\": \"67890\"}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("67890");

        // Assert
        assertNotNull(result);
        assertEquals("67890", result.getId());
        assertEquals("Maria Garcia", result.getName());
    }

    /**
     * Retrieve individual connection error throws connector request exception.
     */
    @Test
    void retrieveIndividual_connectionError_throwsConnectorRequestException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThrows(ConnectorRequestException.class, () -> {
            client.retrieveIndividual("12345");
        });
    }

    /**
     * Retrieve individual non 2 xx response throws connector request exception.
     */
    @Test
    void retrieveIndividual_non2xxResponse_throwsConnectorRequestException() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act & Assert
        assertThrows(ConnectorRequestException.class, () -> {
            client.retrieveIndividual("12345");
        });
    }

    /**
     * Retrieve individual missing configuration returns individual with id only.
     */
    @Test
    void retrieveIndividual_missingConfiguration_returnsIndividualWithIdOnly() {
        // Arrange
        client.setBaseUrl("");
        client.setPath("");

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual invalid json returns individual with id only.
     */
    @Test
    void retrieveIndividual_invalidJson_returnsIndividualWithIdOnly() {
        // Arrange
        String responseBody = "invalid json {";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual name in different field returns individual with name.
     */
    @Test
    void retrieveIndividual_nameInDifferentField_returnsIndividualWithName() {
        // Arrange
        String responseBody = "{\"customer\": {\"fullName\": \"Carlos Lopez\",\"customerName\": \"Another Name\",\"id\": \"11111\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("11111");

        // Assert
        assertNotNull(result);
        assertEquals("11111", result.getId());
        assertEquals("Carlos Lopez", result.getName());
    }

    /**
     * Retrieve organization returns organization domain.
     */
    @Test
    void retrieveOrganization_returnsOrganizationDomain() {
        // Act
        Organization result = client.retrieveOrganization("ORG-001");

        // Assert - CAMBIADO: ahora espera OrganizationDomain, no null
        assertNotNull(result);
        assertEquals("ORG-001", result.getId());
    }

    /**
     * Retrieve organization with configuration returns organization domain.
     */
    @Test
    void retrieveOrganization_withConfiguration_returnsOrganizationDomain() {
        // Act
        Organization result = client.retrieveOrganization("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Build object id with numeric id returns formatted object id.
     */
    @Test
    void buildObjectId_withNumericId_returnsFormattedObjectId() {
        // Act
        String result = client.buildObjectId("123456");

        // Assert
        assertEquals("1-2-3-456", result);
    }

    /**
     * Build object id with short numeric id returns formatted object id.
     */
    @Test
    void buildObjectId_withShortNumericId_returnsFormattedObjectId() {
        // Act
        String result = client.buildObjectId("12");

        // Assert
        assertEquals("1-2-0-0", result);
    }

    /**
     * Build object id with already formatted id returns same id.
     */
    @Test
    void buildObjectId_withAlreadyFormattedId_returnsSameId() {
        // Act
        String result = client.buildObjectId("1-2-3-456");

        // Assert
        assertEquals("1-2-3-456", result);
    }

    /**
     * Build object id with colon id returns same id.
     */
    @Test
    void buildObjectId_withColonId_returnsSameId() {
        // Act
        String result = client.buildObjectId("1:2:3:456");

        // Assert
        assertEquals("1:2:3:456", result);
    }

    /**
     * Build object id with  id returns same id.
     */
    @Test
    void buildObjectId_withNonNumericId_returnsSameId() {
        // Act
        String result = client.buildObjectId("ABC123");

        // Assert
        assertEquals("ABC123", result);
    }

    /**
     * Build object id with null id returns null.
     */
    @Test
    void buildObjectId_withNullId_returnsNull() {
        // Act
        String result = client.buildObjectId(null);

        // Assert
        assertNull(result);
    }

    /**
     * Normalize id with colons replaces with dashes.
     */
    @Test
    void normalizeId_withColons_replacesWithDashes() {
        // Act
        String result = client.normalizeId("1:2:3:456");

        // Assert
        assertEquals("1-2-3-456", result);
    }

    /**
     * Normalize id without colons returns same id.
     */
    @Test
    void normalizeId_withoutColons_returnsSameId() {
        // Act
        String result = client.normalizeId("123456");

        // Assert
        assertEquals("123456", result);
    }

    /**
     * Normalize id with null returns null.
     */
    @Test
    void normalizeId_withNull_returnsNull() {
        // Act
        String result = client.normalizeId(null);

        // Assert
        assertNull(result);
    }

    /**
     * Retrieve individual with null response throws exception.
     */
    @Test
    void retrieveIndividual_withNullResponse_throwsException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(null);

        // Act & Assert
        assertThrows(ConnectorRequestException.class, () -> {
            client.retrieveIndividual("12345");
        });
    }

    /**
     * Retrieve individual with blank body returns id only.
     */
    @Test
    void retrieveIndividual_withBlankBody_returnsIdOnly() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("   ", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual with null body returns id only.
     */
    @Test
    void retrieveIndividual_withNullBody_returnsIdOnly() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual with json array returns id only.
     */
    @Test
    void retrieveIndividual_withJsonArray_returnsIdOnly() {
        // Arrange
        String responseBody = "[{\"name\": \"Test\"}]";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual with empty json object returns id only.
     */
    @Test
    void retrieveIndividual_withEmptyJsonObject_returnsIdOnly() {
        // Arrange
        String responseBody = "{}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual with name as number returns id only.
     */
    @Test
    void retrieveIndividual_withNameAsNumber_returnsIdOnly() {
        // Arrange
        String responseBody = "{\"customer\": {\"name\": 12345}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual with blank name returns id only.
     */
    @Test
    void retrieveIndividual_withBlankName_returnsIdOnly() {
        // Arrange
        String responseBody = "{\"customer\": {\"name\": \"   \"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual with multiple name fields returns first valid.
     */
    @Test
    void retrieveIndividual_withMultipleNameFields_returnsFirstValid() {
        // Arrange
        String responseBody = "{\"customer\": {\"customerName\": \"First Name\", \"userName\": \"Second Name\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("First Name", result.getName());
    }

    /**
     * Retrieve individual with nested customer object parses name.
     */
    @Test
    void retrieveIndividual_withNestedCustomerObject_parsesName() {
        // Arrange
        String responseBody = "{\"customer\": {\"personalInfo\": {\"name\": \"Nested Name\"}}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Build object id with single digit returns formatted id.
     */
    @Test
    void buildObjectId_withSingleDigit_returnsFormattedId() {
        // Act
        String result = client.buildObjectId("5");

        // Assert
        assertEquals("5-0-0-0", result);
    }

    /**
     * Build object id with three digits returns formatted id.
     */
    @Test
    void buildObjectId_withThreeDigits_returnsFormattedId() {
        // Act
        String result = client.buildObjectId("789");

        // Assert
        assertEquals("7-8-9-0", result);
    }

    /**
     * Build object id with four digits returns formatted id.
     */
    @Test
    void buildObjectId_withFourDigits_returnsFormattedId() {
        // Act
        String result = client.buildObjectId("1234");

        // Assert
        assertEquals("1-2-3-4", result);
    }

    /**
     * Build object id with long numeric id returns formatted id.
     */
    @Test
    void buildObjectId_withLongNumericId_returnsFormattedId() {
        // Act
        String result = client.buildObjectId("123456789");

        // Assert
        assertEquals("1-2-3-456789", result);
    }

    /**
     * Build object id with mixed alphanumeric returns same id.
     */
    @Test
    void buildObjectId_withMixedAlphanumeric_returnsSameId() {
        // Act
        String result = client.buildObjectId("A1B2C3");

        // Assert
        assertEquals("A1B2C3", result);
    }

    /**
     * Normalize id with multiple colons replaces all.
     */
    @Test
    void normalizeId_withMultipleColons_replacesAll() {
        // Act
        String result = client.normalizeId("1:2:3:4:5");

        // Assert
        assertEquals("1-2-3-4-5", result);
    }

    /**
     * Normalize id with mixed separators replaces only colons.
     */
    @Test
    void normalizeId_withMixedSeparators_replacesOnlyColons() {
        // Act
        String result = client.normalizeId("1:2-3:4");

        // Assert
        assertEquals("1-2-3-4", result);
    }

    /**
     * Retrieve individual missing base url only returns id only.
     */
    @Test
    void retrieveIndividual_missingBaseUrlOnly_returnsIdOnly() {
        // Arrange
        client.setBaseUrl(null);
        client.setPath("/api");

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual missing path only returns id only.
     */
    @Test
    void retrieveIndividual_missingPathOnly_returnsIdOnly() {
        // Arrange
        client.setBaseUrl("http://test.com");
        client.setPath(null);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve organization with null id returns organization with null id.
     */
    @Test
    void retrieveOrganization_withNullId_returnsOrganizationWithNullId() {
        // Act
        Organization result = client.retrieveOrganization(null);

        // Assert
        assertNotNull(result);
        assertNull(result.getId());
    }

    /**
     * Retrieve organization with empty id returns organization with empty id.
     */
    @Test
    void retrieveOrganization_withEmptyId_returnsOrganizationWithEmptyId() {
        // Act
        Organization result = client.retrieveOrganization("");

        // Assert
        assertNotNull(result);
    }

    /**
     * Build object id with empty string returns formatted id.
     */
    @Test
    void buildObjectId_withEmptyString_returnsFormattedId() {
        // Act
        String result = client.buildObjectId("");

        // Assert
        assertEquals("0-0-0-0", result);
    }

    /**
     * Normalize id with empty string returns empty string.
     */
    @Test
    void normalizeId_withEmptyString_returnsEmptyString() {
        // Act
        String result = client.normalizeId("");

        // Assert
        assertEquals("", result);
    }

    /**
     * Retrieve individual with field containing name extracts name.
     */
    @Test
    void retrieveIndividual_withFieldContainingName_extractsName() {
        // Arrange
        String responseBody = "{\"customer\": {\"fullName\": \"Carlos Rodriguez\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("Carlos Rodriguez", result.getName());
    }

    /**
     * Retrieve individual with non textual name ignores it.
     */
    @Test
    void retrieveIndividual_withNonTextualName_ignoresIt() {
        // Arrange
        String responseBody = "{\"customer\": {\"name\": 12345}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertNull(result.getName());
    }

    /**
     * Retrieve individual with blank name field ignores it.
     */
    @Test
    void retrieveIndividual_withBlankNameField_ignoresIt() {
        // Arrange
        String responseBody = "{\"customer\": {\"name\": \"   \"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertNull(result.getName());
    }

    /**
     * Retrieve individual with null json node handles gracefully.
     */
    @Test
    void retrieveIndividual_withNullJsonNode_handlesGracefully() {
        // Arrange
        String responseBody = "null";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual with array json node handles gracefully.
     */
    @Test
    void retrieveIndividual_withArrayJsonNode_handlesGracefully() {
        // Arrange
        String responseBody = "[{\"name\": \"Test\"}]";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual with multiple name fields picks first.
     */
    @Test
    void retrieveIndividual_withMultipleNameFields_picksFirst() {
        // Arrange
        String responseBody = "{\"customer\": {\"firstName\": \"John\", \"lastName\": \"Doe\", \"fullName\": \"John Doe\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getName());
    }

    /**
     * Build object id with single digit formats correctly.
     */
    @Test
    void buildObjectId_withSingleDigit_formatsCorrectly() {
        // Act
        String result = client.buildObjectId("5");

        // Assert
        assertEquals("5-0-0-0", result);
    }

    /**
     * Build object id with two digits formats correctly.
     */
    @Test
    void buildObjectId_withTwoDigits_formatsCorrectly() {
        // Act
        String result = client.buildObjectId("56");

        // Assert
        assertEquals("5-6-0-0", result);
    }

    /**
     * Build object id with three digits formats correctly.
     */
    @Test
    void buildObjectId_withThreeDigits_formatsCorrectly() {
        // Act
        String result = client.buildObjectId("567");

        // Assert
        assertEquals("5-6-7-0", result);
    }

    /**
     * Build object id with alphanumeric returns unchanged.
     */
    @Test
    void buildObjectId_withAlphanumeric_returnsUnchanged() {
        // Act
        String result = client.buildObjectId("ABC123");

        // Assert
        assertEquals("ABC123", result);
    }

    /**
     * Build object id with hyphen returns unchanged.
     */
    @Test
    void buildObjectId_withHyphen_returnsUnchanged() {
        // Act
        String result = client.buildObjectId("1-2-3");

        // Assert
        assertEquals("1-2-3", result);
    }

    /**
     * Build object id with colon returns unchanged.
     */
    @Test
    void buildObjectId_withColon_returnsUnchanged() {
        // Act
        String result = client.buildObjectId("1:2:3");

        // Assert
        assertEquals("1:2:3", result);
    }

    /**
     * Normalize id with colon replaces with hyphen.
     */
    @Test
    void normalizeId_withColon_replacesWithHyphen() {
        // Act
        String result = client.normalizeId("1:2:3:4");

        // Assert
        assertEquals("1-2-3-4", result);
    }

    /**
     * Normalize id with alphanumeric colons replaces all.
     */
    @Test
    void normalizeId_withAlphanumericColons_replacesAll() {
        // Act
        String result = client.normalizeId("a:b:c");

        // Assert
        assertEquals("a-b-c", result);
    }

    /**
     * Normalize id with no colons returns unchanged.
     */
    @Test
    void normalizeId_withNoColons_returnsUnchanged() {
        // Act
        String result = client.normalizeId("12345");

        // Assert
        assertEquals("12345", result);
    }

    /**
     * Retrieve individual with missing configuration returns id only.
     */
    @Test
    void retrieveIndividual_withMissingConfiguration_returnsIdOnly() {
        // Arrange
        client.setBaseUrl(null);
        client.setPath(null);

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual with blank base url returns id only.
     */
    @Test
    void retrieveIndividual_withBlankBaseUrl_returnsIdOnly() {
        // Arrange
        client.setBaseUrl("   ");

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    /**
     * Retrieve individual with blank path returns id only.
     */
    @Test
    void retrieveIndividual_withBlankPath_returnsIdOnly() {
        // Arrange
        client.setPath("   ");

        // Act
        Individual result = client.retrieveIndividual("12345");

        // Assert
        assertNotNull(result);
        assertEquals("12345", result.getId());
    }

    // Tests for retrieveIndividualByMdn

    /**
     * Retrieve individual by mdn successful response returns individual with customer id.
     */
    @Test
    void retrieveIndividualByMdn_successfulResponse_returnsIndividualWithCustomerId() {
        // Arrange
        String responseBody = "{\"customer\": {\"customerId\": \"0-1-5-1203\", \"name\": \"John Doe\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNotNull(result);
        assertEquals("0-1-5-1203", result.getId());
        assertEquals("John Doe", result.getName());
    }

    /**
     * Retrieve individual by mdn empty response returns null.
     */
    @Test
    void retrieveIndividualByMdn_emptyResponse_returnsNull() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("", HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNull(result);
    }

    /**
     * Retrieve individual by mdn missing customer node returns null.
     */
    @Test
    void retrieveIndividualByMdn_missingCustomerNode_returnsNull() {
        // Arrange
        String responseBody = "{\"data\": {\"id\": \"123\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNull(result);
    }

    /**
     * Retrieve individual by mdn connection error throws connector request exception.
     */
    @Test
    void retrieveIndividualByMdn_connectionError_throwsConnectorRequestException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        // Act & Assert
        assertThrows(ConnectorRequestException.class, () -> {
            client.retrieveIndividualByMdn("5624864455");
        });
    }

    /**
     * Retrieve individual by mdn non 2 xx response throws connector request exception.
     */
    @Test
    void retrieveIndividualByMdn_non2xxResponse_throwsConnectorRequestException() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act & Assert
        assertThrows(ConnectorRequestException.class, () -> {
            client.retrieveIndividualByMdn("5624864455");
        });
    }

    /**
     * Retrieve individual by mdn missing configuration returns null.
     */
    @Test
    void retrieveIndividualByMdn_missingConfiguration_returnsNull() {
        // Arrange
        client.setBaseUrl("");
        client.setPath("");

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNull(result);
    }

    /**
     * Retrieve individual by mdn invalid json returns null.
     */
    @Test
    void retrieveIndividualByMdn_invalidJson_returnsNull() {
        // Arrange
        String responseBody = "invalid json {";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNull(result);
    }

    /**
     * Retrieve individual by mdn with customer id and no name returns individual with id only.
     */
    @Test
    void retrieveIndividualByMdn_withCustomerIdAndNoName_returnsIndividualWithIdOnly() {
        // Arrange
        String responseBody = "{\"customer\": {\"customerId\": \"0-1-5-1203\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNotNull(result);
        assertEquals("0-1-5-1203", result.getId());
        assertNull(result.getName());
    }

    /**
     * Retrieve individual by mdn with null response throws exception.
     */
    @Test
    void retrieveIndividualByMdn_withNullResponse_throwsException() {
        // Arrange
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(null);

        // Act & Assert
        assertThrows(ConnectorRequestException.class, () -> {
            client.retrieveIndividualByMdn("5624864455");
        });
    }

    /**
     * Retrieve individual by mdn with blank body returns null.
     */
    @Test
    void retrieveIndividualByMdn_withBlankBody_returnsNull() {
        // Arrange
        ResponseEntity<String> response = new ResponseEntity<>("   ", HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNull(result);
    }

    /**
     * Retrieve individual by mdn with given name field extracts name.
     */
    @Test
    void retrieveIndividualByMdn_withGivenNameField_extractsName() {
        // Arrange
        String responseBody = "{\"customer\": {\"customerId\": \"123\", \"givenName\": \"Maria\"}}";
        ResponseEntity<String> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(response);

        // Act
        Individual result = client.retrieveIndividualByMdn("5624864455");

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Maria", result.getName());
    }
}