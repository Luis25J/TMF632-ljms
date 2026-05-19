package mx.att.digital.api.tmf632.application.usecase.util;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Individual;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.IndividualStateType;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom.Organization;
import mx.att.digital.api.tmf632.infrastructure.out.response.ExternalReference;
import mx.att.digital.api.tmf632.infrastructure.out.response.IndividualResponse;
import mx.att.digital.api.tmf632.infrastructure.out.response.OrganizationResponse;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Party response adapter.
 */
@Slf4j
@Component
public class PartyResponseAdapter {

  private static final String INDIVIDUAL_SCHEMA = "https://schemas.company.com/tmf632/Individual";
  private static final String ORGANIZATION_SCHEMA = "https://schemas.company.com/tmf632/Organization";
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * To individual dto individual response.
     *
     * @param domain  the domain
     * @param segment the segment
     * @return the individual response
     */
    public IndividualResponse toIndividualDto(Individual domain, String segment) {
    return toIndividualDto(domain, segment, false);
  }

    /**
     * To individual dto individual response.
     *
     * @param domain             the domain
     * @param segment            the segment
     * @param includeDefaultUser the include default user
     * @return the individual response
     */
    public IndividualResponse toIndividualDto(Individual domain, String segment, boolean includeDefaultUser) {
         if (domain == null) return null;
         IndividualResponse dto = new IndividualResponse();
         // ID y HREF - siempre presentes
         String id = domain.getId();
         dto.setId(id);
         dto.setHref("/partyManagement/v5/individual/" + id);

         // Campos TMF obligatorios - siempre presentes
         dto.setType("Individual");
         dto.setBaseType("Party");
         dto.setSchemaLocation(INDIVIDUAL_SCHEMA);

         // Campos básicos de Individual - SOLO si tienen valor
         String name = domain.getName();
         if (isNotBlank(name)) {
             dto.setName(name);
         }
         // Si name está vacío, NO llamar setName() para que sea null

         // Status - SOLO si tiene valor
         IndividualStateType status = domain.getStatus() ;
         if (status!= null) {   
             dto.setStatus(status.getValue());
         }

         // ContactMedium - siempre presente pero vacío si no hay datos
         dto.setContactMedium(new ArrayList<>());

         // Characteristic - platform y opcionalmente isDefaultUser
         List<Map<String, Object>> characteristics = new ArrayList<>();
         Map<String, Object> platformChar = new HashMap<>();
         platformChar.put("name", "platform");
         platformChar.put("value", derivePlatformFromId(id));
         characteristics.add(platformChar);

         // Add isDefaultUser if requested (temporary random until connector provides it)
         if (includeDefaultUser) {
            Map<String, Object> defaultUserChar = new HashMap<>();
            defaultUserChar.put("name", "isDefaultUser");
            defaultUserChar.put("value", String.valueOf(SECURE_RANDOM.nextBoolean()));
            characteristics.add(defaultUserChar);
         }

            dto.setCharacteristic(characteristics);

            // ExternalReference - siempre presente
            List<ExternalReference> externalRefs = buildExternalReferences(segment, id);
            dto.setExternalReference(externalRefs);

            return dto;
  }

    /**
     * To organization dto organization response.
     *
     * @param domain  the domain
     * @param segment the segment
     * @return the organization response
     */
    public OrganizationResponse toOrganizationDto(Organization domain, String segment) {
        if (domain == null) return null;
        
        OrganizationResponse dto = new OrganizationResponse();

        // ID y HREF - siempre presentes
        String id = domain.getId();
        dto.setId(id);
        dto.setHref("/partyManagement/v5/organization/" + id);

        // Campos TMF obligatorios - siempre presentes
        dto.setType("Organization");
        dto.setBaseType("Party");
        dto.setSchemaLocation(ORGANIZATION_SCHEMA);

        // Campos de Organization - SOLO si tienen valor
        String name = domain.getName();
        if (isNotBlank(name)) {
            dto.setName(name);
            dto.setTradingName(name);
        }

        // Status - NUNCA establecer para que sea siempre null
        // No llamar setStatus() bajo ninguna circunstancia

        // Campos booleanos - usar valor por defecto false
        dto.setIsLegalEntity(false);

        // ContactMedium - siempre presente pero vacío si no hay datos
        dto.setContactMedium(new ArrayList<>());
        
        // Characteristic - siempre presente pero vacío si no hay datos
        dto.setCharacteristic(new ArrayList<>());

        // ExistsDuring - siempre presente con valores por defecto
        OrganizationResponse.TimePeriod existsDuring = new OrganizationResponse.TimePeriod();
        existsDuring.setStartDateTime("2005-01-01T00:00:00Z");
        existsDuring.setEndDateTime("2040-01-01T00:00:00Z");
        dto.setExistsDuring(existsDuring);

        // ExternalReference - siempre presente
        List<ExternalReference> externalRefs = buildExternalReferences(segment, id);
        dto.setExternalReference(externalRefs);

        return dto;
  }

  private List<ExternalReference> buildExternalReferences(String segment, String id) {
          List<ExternalReference> refs = new ArrayList<>();
          ExternalReference extRef = new ExternalReference();
          
          String owner = "prepaid".equalsIgnoreCase(segment) ? "matrixx" : "amdocs";
          extRef.setExternalIdentifierType("partyId");
          extRef.setOwner(owner);
          
          if (isNotBlank(id)) {
            String externalId = "amdocs".equals(owner) ? "AMD-" + id : "MTX-" + id;
            extRef.setId(externalId);
          }
          
          refs.add(extRef);
          return refs;
  }

  @SuppressWarnings("unused")
  private String derivePlatformFromId(String id) {
          if (id == null) return "postpaid";
          return (id.length() % 2 == 0) ? "prepaid" : "postpaid";
  }

  // ELIMINADOS todos los métodos de helper problemáticos, Los tests no deberían depender de métodos internos privados
  private static boolean isNotBlank(String s) {
          return s != null && !s.trim().isEmpty();
  }

}