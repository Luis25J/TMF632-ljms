package mx.att.digital.api.tmf632.infrastructure.out.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * The type External reference.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExternalReference {
  private String externalIdentifierType;
  private String id;
  private String owner;

  /**
   * Gets external identifier type.
   *
   * @return the external identifier type
   */
  public String getExternalIdentifierType() { return externalIdentifierType; }

  /**
   * Sets external identifier type.
   *
   * @param externalIdentifierType the external identifier type
   */
  public void setExternalIdentifierType(String externalIdentifierType)
  { this.externalIdentifierType = externalIdentifierType; }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() { return id; }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) { this.id = id; }

  /**
   * Gets owner.
   *
   * @return the owner
   */
  public String getOwner() { return owner; }

  /**
   * Sets owner.
   *
   * @param owner the owner
   */
  public void setOwner(String owner) { this.owner = owner; }
}
