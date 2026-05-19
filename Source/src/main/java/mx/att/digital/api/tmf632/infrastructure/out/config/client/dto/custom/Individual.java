package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.custom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * The type Individual.
 */
@Data

/**
 * The Class PartyDomainBuilder.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Individual {

    // Identificadores básicos usados por conectores y tests
    private String id;
    private String name;

    @NotNull
    private String gender;
    @NotNull
    private String placeOfBirth;
    @NotNull
    private String countryOfBirth;
    @NotNull
    private String nationality;
    @NotNull
    private String maritalStatus;

    @NotNull
    private @Nullable IndividualStateType status;
}

