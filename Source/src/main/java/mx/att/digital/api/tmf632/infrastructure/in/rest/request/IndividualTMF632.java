package mx.att.digital.api.tmf632.infrastructure.in.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;


/**
 * The type Individual tmf 632.
 */
@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndividualTMF632 extends AbstractParty {
  private String id;
  private String name;
  private String status;
  private String birthDate;
  private String givenName;
  private String preferredGivenName;
  private String familyName;
  private String fullName;
  private String gender;
  private List<AbstractPartyCharacteristic> partyCharacteristic;
  private List<ContactMedium> contactMedium;
  private List<PartialError> partialErrors;
}
