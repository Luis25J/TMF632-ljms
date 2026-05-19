package mx.att.digital.api.tmf632.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.application.port.in.PartyCreatePort;
import mx.att.digital.api.tmf632.application.port.in.PartyDeletePort;
import mx.att.digital.api.tmf632.application.port.in.PartyQueryPort;
import mx.att.digital.api.tmf632.application.port.in.PartyUpdatePort;
import mx.att.digital.api.tmf632.application.usecase.util.PartyResponseAdapter;
import mx.att.digital.api.tmf632.infrastructure.exception.ResourceNotFoundException;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import mx.att.digital.api.tmf632.infrastructure.out.response.OrganizationResponse;

import java.util.List;

import org.owasp.encoder.Encode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * The type Party controller.
 */
@Slf4j
@RestController
@RequestMapping("/ms-api-tmf632-party-mgt/v5")
@RequiredArgsConstructor
public class PartyController {

  private final PartyQueryPort queryPort;
  private final PartyCreatePort createPort;
  private final PartyUpdatePort updatePort;
  private final PartyDeletePort deletePort;
  private final PartyResponseAdapter responseAdapter;


  /**
   * Gets organization.
   *
   * @param id the id
   * @return the organization
   */
  @GetMapping("/organization/{id}")
  public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable String id) {
    log.info("[CONTROLLER] 🔍 GET /organization/{}", Encode.forJava(id));
    Organization out = queryPort.retrieveOrganization(id, null);
    if (out == null) {
      log.info("[CONTROLLER] ❌ Organization not found: {}", Encode.forJava(id));
      throw new ResourceNotFoundException("Organization not found: " + id);
    }
    log.info("[CONTROLLER] ✅ Organization found: {}", Encode.forJava(id));
    OrganizationResponse dto = responseAdapter.toOrganizationDto(out, null);
    return ResponseEntity.ok(dto);
  }

  /**
   * Gets individual new.
   *
   * @param id   the id
   * @param role the role
   * @return the individual new
   */
  @GetMapping("/individual/{id}")
  public ResponseEntity<IndividualTMF632> getIndividualNew(@PathVariable String id, 
                                                           @RequestParam String role) {
    log.info("[CONTROLLER] 🔍 GET /individual/{}", Encode.forJava(id));
    IndividualTMF632 individual = queryPort.retrieveUser(id);

    if (individual == null) {
      log.info("[CONTROLLER] ❌ Individual not found: {}", Encode.forJava(id));
    }

    log.info("[CONTROLLER] ✅ IndividualCIM2 found: {}", Encode.forJava(id));

    return ResponseEntity.ok(individual);
  }

  /**
   * Delete individual response entity.
   *
   * @param id   the id
   * @param role the role
   * @return the response entity
   */
  @DeleteMapping("/individual/{id}")
  public ResponseEntity<IndividualTMF632> deleteIndividual(@PathVariable String id, 
                                                       @RequestParam String role) {
    log.info("[CONTROLLER] 🔍 DELETE /individual/{}", Encode.forJava(id));

    IndividualTMF632 individual = deletePort.deleteUser(id);

    if (individual == null) {
      log.info("[CONTROLLER] ❌ Individual not deleted: {}", Encode.forJava(id));
    }

    log.info("[CONTROLLER] ✅ IndividualCIM2 deleted: {}", individual);

    return ResponseEntity.ok(individual);
  }

  /**
   * Create individual response entity.
   *
   * @param individual the individual
   * @param role       the role
   * @return the response entity
   */
  @PostMapping("/individual")
  public ResponseEntity<IndividualTMF632> createIndividual(@RequestBody IndividualTMF632 individual,
                                                           @RequestParam String role) {
    log.info("[CONTROLLER] 🔍 CREATE /individual/{}", individual);

    IndividualTMF632 individualDomain = createPort.createUser(role, individual);

    if (individualDomain == null) {
      log.info("[CONTROLLER] ❌ Individual not created: {}", individual);
    }

    log.info("[CONTROLLER] ✅ IndividualCIM2 created: {}", individualDomain);

    return ResponseEntity.ok(individualDomain);
  }

  /**
   * Update individual response entity.
   *
   * @param id         the id
   * @param role       the role
   * @param individual the individual
   * @return the response entity
   */
  @PatchMapping("/individual/{id}")
  public ResponseEntity<IndividualTMF632> updateIndividual(@PathVariable String id,
                                           @RequestParam String role,
                                           @RequestBody IndividualTMF632 individual) {
    log.info("[CONTROLLER] 🔍 UPDATE /individual/{}", Encode.forJava(id));

    IndividualTMF632 individualDomain = updatePort.updateUser(id, individual);

    if (individualDomain == null) {
      log.info("[CONTROLLER] ❌ Individual not updated: {}", Encode.forJava(id));
    }

    log.info("[CONTROLLER] ✅ IndividualCIM2 updated: {}", individualDomain);
    return ResponseEntity.ok(individualDomain);
  }

    /**
   * Gets individual by userName.
   *
   * @param userName   the userName
   * @param role the role
   * @return the individual 
   */
  @GetMapping("/individual")
  public ResponseEntity<List<IndividualTMF632>> getIndividualByUserName(@RequestParam String userName, 
                                                                         @RequestParam String role) {
    log.info("[CONTROLLER] 🔍 GET /individual?userName={}", Encode.forJava(userName));
    IndividualTMF632 individual = queryPort.retrieveUserByName(userName);

    if (individual == null) {
      log.info("[CONTROLLER] ❌ Individual (userName) not found: {}", Encode.forJava(userName));
      return ResponseEntity.ok(null);
    }

    log.info("[CONTROLLER] ✅ IndividualCIM2 found: {}", Encode.forJava(userName));

    return ResponseEntity.ok(List.of(individual));
  }

}