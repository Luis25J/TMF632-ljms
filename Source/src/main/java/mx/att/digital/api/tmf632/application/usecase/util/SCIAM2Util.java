package mx.att.digital.api.tmf632.application.usecase.util;

import java.util.Optional;
import java.util.function.Consumer;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Reference;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2Request;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;


/**
 * The type Sciam 2 util.
 */
public class SCIAM2Util {

    private SCIAM2Util(){}

    /**
     * Build user to update scim 2 user.
     *
     * @param tmf632Request the tmf 632 request
     * @param userSCIM2     the user scim 2
     * @return the scim 2 user
     */
    public static SCIM2User buildUserToUpdate(IndividualTMF632 tmf632Request, SCIM2User userSCIM2){
          Optional.ofNullable(tmf632Request.getGivenName()).ifPresent(userSCIM2::setFirstName);
          Optional.ofNullable(tmf632Request.getFamilyName()).ifPresent(userSCIM2::setLastName);
          Optional.ofNullable(IndividualTMF632Util.getPhoneFromRequest(tmf632Request)).ifPresent(userSCIM2::setPhone);
          Optional.ofNullable(IndividualTMF632Util.getEmailFromRequest(tmf632Request)).ifPresent(userSCIM2::setEmail);
          Optional.ofNullable(IndividualTMF632Util.getUrlPhotoFromRequest(tmf632Request)).
                                                                                    ifPresent(userSCIM2::setUrlPhoto);
          userSCIM2.setIsAdmin("true");

          return userSCIM2;      
   }

    /**
     * Get ciam request to create scim 2 request.
     *
     * @param tmf632Request the tmf 632 request
     * @return the scim 2 request
     */
    public static SCIM2Request getCIAMRequestToCreate(IndividualTMF632 tmf632Request){
               
          SCIM2Request request = new SCIM2Request();
          SCIM2Reference reference = new SCIM2Reference("ADBILBAO");
          SCIM2User userSCIM2 = new SCIM2User();
        
          String email = IndividualTMF632Util.getEmailFromRequest(tmf632Request);
          String phone = IndividualTMF632Util.getPhoneFromRequest(tmf632Request);
          String urlPhoto = IndividualTMF632Util.getUrlPhotoFromRequest(tmf632Request);

          userSCIM2.setUserName(notBlank(email) ? email : phone);
          userSCIM2.setPassword(IndividualTMF632Util.getPasswordFromRequest(tmf632Request));
          userSCIM2.setConsent("S");
          userSCIM2.setNumberConsents(IndividualTMF632Util.countConsentsFromRequest(tmf632Request) + "");
          userSCIM2.setIsAdmin("true");

          setIfNotBlank(userSCIM2::setEmail,      email);
          setIfNotBlank(userSCIM2::setPhone,      phone);
          setIfNotBlank(userSCIM2::setFirstName,  tmf632Request.getGivenName());
          setIfNotBlank(userSCIM2::setLastName,   tmf632Request.getFamilyName());
          setIfNotBlank(userSCIM2::setUrlPhoto,   urlPhoto);

          request.setReference(reference);
          request.setUser(userSCIM2);

          return request;

    }

    private static void setIfNotBlank(Consumer<String> setter, String val) {
        if (val != null && !val.isBlank()) {
            setter.accept(val);
        }
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

}
