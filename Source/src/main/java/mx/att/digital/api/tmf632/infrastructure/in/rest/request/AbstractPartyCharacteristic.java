package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

/**
 * The type Abstract party characteristic.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = StringArrayCharacteristic.class, name = "StringArrayCharacteristic"),
  @JsonSubTypes.Type(value = StringCharacteristic.class,      name = "StringCharacteristic"),
  @JsonSubTypes.Type(value = ObjectArrayCharacteristic.class, name = "ObjectArrayCharacteristic")
})
@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractPartyCharacteristic {
  @JsonProperty("@type") 
  private String type;
  private String valueType;
  private String name;
}
