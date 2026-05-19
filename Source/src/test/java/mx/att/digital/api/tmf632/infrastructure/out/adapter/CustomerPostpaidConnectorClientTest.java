package mx.att.digital.api.tmf632.infrastructure.out.adapter;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * The type Customer postpaid connector client test.
 */
class CustomerPostpaidConnectorClientTest {

  private RestTemplate restTemplate632;
  private CustomerPostpaidConnectorClient client;

  /**
   * Sets up.
   */
  @BeforeEach
  void setUp() {
    restTemplate632 = mock(RestTemplate.class);
    client = new CustomerPostpaidConnectorClient(restTemplate632);

    client.setBaseUrl("https://api.test.com");
    client.setPath("/customer");
    client.setUsername("testuser");
    client.setPassword("testpass");
  }

  /**
   * Test retrieve individual success.
   */
  @Test
  void testRetrieveIndividual_Success() {
    // Given
    String customerId = "186191396";
    String mockResponse = "{\"customer\":{\"customerId\":\"186191396\",\"type\":\"Individual\",\"fullName\":\"Test User\"},\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNotNull(result);
    assertEquals(customerId, result.getId());
    assertEquals("Test User", result.getName());
  }

  /**
   * Test retrieve individual null response.
   */
  @Test
  void testRetrieveIndividual_NullResponse() {
    // Given
    String customerId = "186191396";

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(ResponseEntity.ok().build()); // Response sin body

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual non individual type.
   */
  @Test
  void testRetrieveIndividual_NonIndividualType() {
    // Given
    String customerId = "186191396";
    String mockResponse = "{\"customer\":{\"customerId\":\"186191396\",\"type\":\"Organization\",\"legalName\":\"Test Corp\"},\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual http error.
   */
  @Test
  void testRetrieveIndividual_HttpError() {
    // Given
    String customerId = "186191396";

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(ResponseEntity.status(500).build());

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve organization success.
   */
  @Test
  void testRetrieveOrganization_Success() {
    // Given
    String customerId = "186191396";
    String mockResponse = "{\"customer\":{\"customerId\":\"186191396\",\"type\":\"Organization\",\"legalName\":\"Test Corp\"},\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNotNull(result);
    assertEquals(customerId, result.getId());
    assertEquals("Test Corp", result.getName());
  }

  /**
   * Test retrieve organization non organization type.
   */
  @Test
  void testRetrieveOrganization_NonOrganizationType() {
    // Given
    String customerId = "186191396";
    String mockResponse = "{\"customer\":{\"customerId\":\"186191396\",\"type\":\"Individual\",\"fullName\":\"Test User\"},\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual invalid json.
   */
  @Test
  void testRetrieveIndividual_InvalidJson() {
    // Given
    String customerId = "12345";
    String mockResponse = "{invalid json";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When/Then
    assertThrows(Exception.class, () -> client.retrieveIndividual(customerId));
  }

  /**
   * Test retrieve individual empty body.
   */
  @Test
  void testRetrieveIndividual_EmptyBody() {
    // Given
    String customerId = "12345";

    ResponseEntity<String> responseEntity = ResponseEntity.ok("");

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual blank body.
   */
  @Test
  void testRetrieveIndividual_BlankBody() {
    // Given
    String customerId = "12345";

    ResponseEntity<String> responseEntity = ResponseEntity.ok("   ");

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual missing customer node.
   */
  @Test
  void testRetrieveIndividual_MissingCustomerNode() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve organization empty body.
   */
  @Test
  void testRetrieveOrganization_EmptyBody() {
    // Given
    String customerId = "12345";

    ResponseEntity<String> responseEntity = ResponseEntity.ok("");

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve organization blank body.
   */
  @Test
  void testRetrieveOrganization_BlankBody() {
    // Given
    String customerId = "12345";

    ResponseEntity<String> responseEntity = ResponseEntity.ok("   ");

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve organization missing customer node.
   */
  @Test
  void testRetrieveOrganization_MissingCustomerNode() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve organization http error.
   */
  @Test
  void testRetrieveOrganization_HttpError() {
    // Given
    String customerId = "12345";

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(ResponseEntity.status(404).build());

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve organization null response.
   */
  @Test
  void testRetrieveOrganization_NullResponse() {
    // Given
    String customerId = "12345";

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(null);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual with alternative name field.
   */
  @Test
  void testRetrieveIndividual_WithAlternativeNameField() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Individual\",\"name\":\"Alternative Name\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNotNull(result);
    assertEquals("12345", result.getId());
    assertEquals("Alternative Name", result.getName());
  }

  /**
   * Test retrieve organization with alternative name field.
   */
  @Test
  void testRetrieveOrganization_WithAlternativeNameField() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Organization\",\"name\":\"Alternative Corp\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNotNull(result);
    assertEquals("12345", result.getId());
    assertEquals("Alternative Corp", result.getName());
  }

  /**
   * Test retrieve individual with formatted name.
   */
  @Test
  void testRetrieveIndividual_WithFormattedName() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Individual\",\"formattedName\":\"Formatted Name\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNotNull(result);
    assertEquals("Formatted Name", result.getName());
  }

  /**
   * Test retrieve individual with legal name.
   */
  @Test
  void testRetrieveIndividual_WithLegalName() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Individual\",\"legalName\":\"Legal Name\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNotNull(result);
    assertEquals("Legal Name", result.getName());
  }

  /**
   * Test retrieve individual with no valid name.
   */
  @Test
  void testRetrieveIndividual_WithNoValidName() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Individual\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNotNull(result);
    assertEquals("12345", result.getId());
    assertNull(result.getName());
  }

  /**
   * Test retrieve organization with formatted name.
   */
  @Test
  void testRetrieveOrganization_WithFormattedName() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Organization\",\"formattedName\":\"Formatted Org\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNotNull(result);
    assertEquals("Formatted Org", result.getName());
  }

  /**
   * Test retrieve organization with full name.
   */
  @Test
  void testRetrieveOrganization_WithFullName() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Organization\",\"fullName\":\"Full Org Name\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNotNull(result);
    assertEquals("Full Org Name", result.getName());
  }

  /**
   * Test retrieve organization with no valid name.
   */
  @Test
  void testRetrieveOrganization_WithNoValidName() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Organization\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Organization result = client.retrieveOrganization(customerId);

    // Then
    assertNotNull(result);
    assertEquals("12345", result.getId());
    assertNull(result.getName());
  }

  /**
   * Test retrieve individual with blank name fields.
   */
  @Test
  void testRetrieveIndividual_WithBlankNameFields() {
    // Given
    String customerId = "12345";
    String mockResponse = "{\"customer\":{\"customerId\":\"12345\",\"type\":\"Individual\",\"fullName\":\"   \",\"name\":\"\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividual(customerId);

    // Then
    assertNotNull(result);
    assertNull(result.getName());
  }

  // Tests for retrieveIndividualByCustomerdn

  /**
   * Test retrieve individual by customerdn success.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_Success() {
    // Given
    String customerdn = "5510500794";
    String mockResponse = "{\"customer\":{\"customerId\":\"186180009\",\"type\":\"Individual\",\"fullName\":\"Test User\"},\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNotNull(result);
    assertEquals("186180009", result.getId());
    assertEquals("Test User", result.getName());
  }

  /**
   * Test retrieve individual by customerdn empty response.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_EmptyResponse() {
    // Given
    String customerdn = "5510500794";

    ResponseEntity<String> responseEntity = ResponseEntity.ok("");

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual by customerdn missing customer node.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_MissingCustomerNode() {
    // Given
    String customerdn = "5510500794";
    String mockResponse = "{\"code\":\"0\",\"message\":\"SUCCESS\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual by customerdn http error.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_HttpError() {
    // Given
    String customerdn = "5510500794";

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(ResponseEntity.status(500).build());

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual by customerdn null response.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_NullResponse() {
    // Given
    String customerdn = "5510500794";

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(null);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual by customerdn blank body.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_BlankBody() {
    // Given
    String customerdn = "5510500794";

    ResponseEntity<String> responseEntity = ResponseEntity.ok("   ");

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNull(result);
  }

  /**
   * Test retrieve individual by customerdn with customer id and no name.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_WithCustomerIdAndNoName() {
    // Given
    String customerdn = "5510500794";
    String mockResponse = "{\"customer\":{\"customerId\":\"186180009\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNotNull(result);
    assertEquals("186180009", result.getId());
    assertNull(result.getName());
  }

  /**
   * Test retrieve individual by customerdn invalid json.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_InvalidJson() {
    // Given
    String customerdn = "5510500794";
    String mockResponse = "{invalid json";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When/Then
    assertThrows(Exception.class, () -> client.retrieveIndividualByCustomerdn(customerdn));
  }

  /**
   * Test retrieve individual by customerdn with alternative name fields.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_WithAlternativeNameFields() {
    // Given
    String customerdn = "5510500794";
    String mockResponse = "{\"customer\":{\"customerId\":\"186180009\",\"name\":\"Alternative Name\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNotNull(result);
    assertEquals("186180009", result.getId());
    assertEquals("Alternative Name", result.getName());
  }

  /**
   * Test retrieve individual by customerdn with formatted name.
   */
  @Test
  void testRetrieveIndividualByCustomerdn_WithFormattedName() {
    // Given
    String customerdn = "5510500794";
    String mockResponse = "{\"customer\":{\"customerId\":\"186180009\",\"formattedName\":\"Formatted Name\"},\"code\":\"0\"}";

    ResponseEntity<String> responseEntity = ResponseEntity.ok(mockResponse);

    when(restTemplate632.exchange(
        anyString(),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))
    ).thenReturn(responseEntity);

    // When
    Individual result = client.retrieveIndividualByCustomerdn(customerdn);

    // Then
    assertNotNull(result);
    assertEquals("Formatted Name", result.getName());
  }
}
