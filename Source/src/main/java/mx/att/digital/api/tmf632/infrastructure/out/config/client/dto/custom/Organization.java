package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Organization.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
  private String id;
  private String type;      // "@type"
  private String baseType;  // "@baseType"
  private String name;
  private String status;
  private boolean isLegalEntity;
}
