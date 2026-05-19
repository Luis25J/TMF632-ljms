package mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2;

import lombok.*;

/**
 * The type Scim 2 user delete.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * UserSCIM2Delete
 */
public class SCIM2UserDelete {
        
    private String userId;
    private Boolean deleted;

}
