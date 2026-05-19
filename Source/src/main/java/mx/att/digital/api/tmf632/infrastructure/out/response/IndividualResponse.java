package mx.att.digital.api.tmf632.infrastructure.out.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * The type Individual response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualResponse {
    
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
    private List<Map<String, Object>> contactMedium;
    private List<Map<String, Object>> characteristic;
    private List<ExternalReference> externalReference;

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() { return id; }

    /**
     * Sets id.
     *
     * @param idd the idd
     */
    public void setId(String idd) { this.id = idd; }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() { return type; }

    /**
     * Sets type.
     *
     * @param tyype the tyype
     */
    public void setType(String tyype) { this.type = tyype; }

    /**
     * Gets base type.
     *
     * @return the base type
     */
    public String getBaseType() { return baseType; }

    /**
     * Sets base type.
     *
     * @param baseTyype the base tyype
     */
    public void setBaseType(String baseTyype) { this.baseType = baseTyype; }

    /**
     * Gets schema location.
     *
     * @return the schema location
     */
    public String getSchemaLocation() { return schemaLocation; }

    /**
     * Sets schema location.
     *
     * @param schemmaLocation the schemma location
     */
    public void setSchemaLocation(String schemmaLocation) { this.schemaLocation = schemmaLocation; }

    /**
     * Gets href.
     *
     * @return the href
     */
    public String getHref() { return href; }

    /**
     * Sets href.
     *
     * @param hreef the hreef
     */
    public void setHref(String hreef) { this.href = hreef; }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Sets name.
     *
     * @param naame the naame
     */
    public void setName(String naame) { this.name = naame; }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() { return status; }

    /**
     * Sets status.
     *
     * @param sstatus the sstatus
     */
    public void setStatus(String sstatus) { this.status = sstatus; }

    /**
     * Gets contact medium.
     *
     * @return the contact medium
     */
    public List<Map<String, Object>> getContactMedium() { return contactMedium; }

    /**
     * Sets contact medium.
     *
     * @param contacctMedium the contacct medium
     */
    public void setContactMedium(List<Map<String, Object>> contacctMedium) { this.contactMedium = contacctMedium; }

    /**
     * Gets characteristic.
     *
     * @return the characteristic
     */
    public List<Map<String, Object>> getCharacteristic() { return characteristic; }

    /**
     * Sets characteristic.
     *
     * @param characterisstic the characterisstic
     */
    public void setCharacteristic(List<Map<String, Object>> characterisstic) { this.characteristic = characterisstic; }

    /**
     * Gets external reference.
     *
     * @return the external reference
     */
    public List<ExternalReference> getExternalReference() { return externalReference; }

    /**
     * Sets external reference.
     *
     * @param exxternalReference the exxternal reference
     */
    public void setExternalReference(List<ExternalReference> exxternalReference)
    { this.externalReference = exxternalReference; }
}
