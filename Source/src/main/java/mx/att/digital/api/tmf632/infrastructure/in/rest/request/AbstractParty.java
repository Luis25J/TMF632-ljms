package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;


/**
 * The type Abstract party.
 */
@JsonSubTypes({
  @JsonSubTypes.Type(value = IndividualTMF632.class, name = "Individual")
})
@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractParty {
  @JsonProperty("@type") private String type;
  @JsonProperty("@baseType") private String baseType;
  @JsonProperty("@schemaLocation")  private String schemaLocation;
  @JsonProperty("href") private String href;
}