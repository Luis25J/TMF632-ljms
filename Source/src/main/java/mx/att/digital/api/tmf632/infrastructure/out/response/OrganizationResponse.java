package mx.att.digital.api.tmf632.infrastructure.out.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Organization response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationResponse {

  private String id;

    @JsonProperty("@type")
    private String type;

    @JsonProperty("@baseType")
    private String baseType;

    @JsonProperty("@schemaLocation")
    private String schemaLocation;

    @JsonProperty("href")
    private String href;

    private String name;
    private String status;
    private boolean isLegalEntity;
    private String tradingName;
    private List<Map<String, Object>> contactMedium;
    private List<Map<String, Object>> characteristic;
    private TimePeriod existsDuring;
    private List<ExternalReference> externalReference;

  /**
   * The type Time period.
   */
  public static class TimePeriod {
        private String startDateTime;
        private String endDateTime;

    /**
     * Gets start date time.
     *
     * @return the start date time
     */
    public String getStartDateTime() { return startDateTime; }

    /**
     * Sets start date time.
     *
     * @param startDdateTime the start ddate time
     */
    public void setStartDateTime(String startDdateTime) { this.startDateTime = startDdateTime; }

    /**
     * Gets end date time.
     *
     * @return the end date time
     */
    public String getEndDateTime() { return endDateTime; }

    /**
     * Sets end date time.
     *
     * @param endDdateTime the end ddate time
     */
    public void setEndDateTime(String endDdateTime) { this.endDateTime = endDdateTime; }
    }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() { return id; }

  /**
   * Sets id.
   *
   * @param iid the iid
   */
  public void setId(String iid) { this.id = iid; }

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() { return type; }

  /**
   * Sets type.
   *
   * @param typee the typee
   */
  public void setType(String typee) { this.type = typee; }

  /**
   * Gets base type.
   *
   * @return the base type
   */
  public String getBaseType() { return baseType; }

  /**
   * Sets base type.
   *
   * @param baseTypee the base typee
   */
  public void setBaseType(String baseTypee) { this.baseType = baseTypee; }

  /**
   * Gets schema location.
   *
   * @return the schema location
   */
  public String getSchemaLocation() { return schemaLocation; }

  /**
   * Sets schema location.
   *
   * @param scchemaLocation the scchema location
   */
  public void setSchemaLocation(String scchemaLocation) { this.schemaLocation = scchemaLocation; }

  /**
   * Gets href.
   *
   * @return the href
   */
  public String getHref() { return href; }

  /**
   * Sets href.
   *
   * @param hrref the hrref
   */
  public void setHref(String hrref) { this.href = hrref; }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() { return name; }

  /**
   * Sets name.
   *
   * @param nname the nname
   */
  public void setName(String nname) { this.name = nname; }

  /**
   * Gets status.
   *
   * @return the status
   */
  public String getStatus() { return status; }

  /**
   * Sets status.
   *
   * @param statuus the statuus
   */
  public void setStatus(String statuus) { this.status = statuus; }

  /**
   * Gets is legal entity.
   *
   * @return the is legal entity
   */
  public boolean getIsLegalEntity() { return isLegalEntity; }

  /**
   * Sets is legal entity.
   *
   * @param isLegallEntity the is legall entity
   */
  public void setIsLegalEntity(boolean isLegallEntity) { this.isLegalEntity = isLegallEntity; }

  /**
   * Gets trading name.
   *
   * @return the trading name
   */
  public String getTradingName() { return tradingName; }

  /**
   * Sets trading name.
   *
   * @param tradinggName the tradingg name
   */
  public void setTradingName(String tradinggName) { this.tradingName = tradinggName; }

  /**
   * Gets contact medium.
   *
   * @return the contact medium
   */
  public List<Map<String, Object>> getContactMedium() { return contactMedium; }

  /**
   * Sets contact medium.
   *
   * @param contactMeedium the contact meedium
   */
  public void setContactMedium(List<Map<String, Object>> contactMeedium) { this.contactMedium = contactMeedium; }

  /**
   * Gets characteristic.
   *
   * @return the characteristic
   */
  public List<Map<String, Object>> getCharacteristic() { return characteristic; }

  /**
   * Sets characteristic.
   *
   * @param characteeristic the characteeristic
   */
  public void setCharacteristic(List<Map<String, Object>> characteeristic) { this.characteristic = characteeristic; }

  /**
   * Gets exists during.
   *
   * @return the exists during
   */
  public TimePeriod getExistsDuring() { return existsDuring; }

  /**
   * Sets exists during.
   *
   * @param existsDuring the exists during
   */
  public void setExistsDuring(TimePeriod existsDuring) { this.existsDuring = existsDuring; }

  /**
   * Gets external reference.
   *
   * @return the external reference
   */
  public List<ExternalReference> getExternalReference() { return externalReference; }

  /**
   * Sets external reference.
   *
   * @param extternalReference the extternal reference
   */
  public void setExternalReference(List<ExternalReference> extternalReference)
    { this.externalReference = extternalReference; }

}