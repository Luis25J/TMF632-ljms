package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ValidFor;

/**
 * The type Tmf 629 custom response.
 */
@Data
public class TMF629CustomResponse {

    private String baseType;
    private String schemaLocation;
    private String href;
    private String id;
    private String name;
    private String description;
    private String role;
    private TMF629EngagedParty engagedParty;
    private String partyRoleSpecification;
    private List<TMF629Characteristic> characteristic;
    private List<String> account;
    private List<String> agreement;
    private List<TMF629ContactMedium> contactMedium;
    private List<String> paymentMethod;
    private List<String> creditProfile;
    private List<String> relatedParty;
    private String status;
    private String statusReason;
    private ValidFor validFor;


    /**
     * The type Tmf 629 engaged party.
     */
    public record TMF629EngagedParty(
        String type,
        String baseType,
        String schemaLocation,
        String href,
        String id,
        String name,
        String referredType
    ){}

    /**
     * The type Tmf 629 characteristic.
     */
    public record TMF629Characteristic(
    @JsonProperty("@type")
    String type,
    @JsonProperty("@baseType")
    String baseType,
    @JsonProperty("@schemaLocation")
    String schemaLocation,
    String id,
    String name,
    String valueType,
    List<String> characteristicRelationship
    ) {}

    /**
     * The type Tmf 629 contact medium.
     */
    public record TMF629ContactMedium(
        String type,
        String baseType,
        String schemaLocation,
        String id,
        String preferred,
        String contactType,
        ValidFor validFor,
        String phoneNumber
    ){}

}
