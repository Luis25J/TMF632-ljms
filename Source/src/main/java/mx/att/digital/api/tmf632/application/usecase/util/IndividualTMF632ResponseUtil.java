package mx.att.digital.api.tmf632.application.usecase.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import mx.att.digital.api.tmf632.infrastructure.exception.FieldMappingException;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.AbstractPartyCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Consent;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Contact;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ObjectArrayCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.PartialError;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.StringArrayCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.StringCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.customciam2.SCIM2User;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnector;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorConsent;
import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.userconnector.UserConnectorContact;

/**
 * The type Individual tmf 632 response util.
 */
@Slf4j
public class IndividualTMF632ResponseUtil {

    private static final String STRING_TYPE = "string";
    private static final String STRING_CHAR_TYPE = "StringCharacteristic";
    private static final String ARRAY_TYPE = "array";

    /**
     * Build individual response individual tmf 632.
     *
     * @param userSCIM2     the user scim 2
     * @param userConnector the user connector
     * @param lstErrors     the lst errors
     * @return the individual tmf 632
     */
    public IndividualTMF632 buildIndividualResponse(SCIM2User userSCIM2, UserConnector userConnector,
                                                    List<PartialError> lstErrors){
            IndividualTMF632 individual = new IndividualTMF632();

            individual.setHref("/partyManagement/v5/individual/"+ userSCIM2.getUserId());
            individual.setSchemaLocation("https://schemas.company.com/tmf632/Individual");
            individual.setType("Individual");
            individual.setBaseType("Party");
            individual.setId(userSCIM2.getUserId());
            individual.setGivenName(userSCIM2.getFirstName());          
            individual.setFamilyName(userSCIM2.getLastName());
            individual.setFullName(userSCIM2.getFirstName() + " " + userSCIM2.getLastName());

            if(userConnector == null){
               PartialError error = new PartialError();
               error.setCode("02");
               error.setMessage("User does not exist or is inactive in extended DB");
               if(lstErrors == null){
                  lstErrors = new ArrayList<>();
               }
               lstErrors.add(error);
             
           } else{
              if(userConnector.getProfile() != null){
                  if(userConnector.getProfile().getBirthDate() != null){
                    individual.setBirthDate(userConnector.getProfile().getBirthDate());
                  }

                  if(userConnector.getProfile().getAlias() != null){
                    individual.setPreferredGivenName(userConnector.getProfile().getAlias());
                  }
              }
           
              individual.setContactMedium(ContactMediumRespUtil.buildListContactMedium(userConnector));
              individual.setPartyCharacteristic(buildPartyCharacteristic(userConnector, userSCIM2));
            }
           if(lstErrors != null && !lstErrors.isEmpty()){
            individual.setPartialErrors(lstErrors);
           }

          return individual;
  }
  
  private List<AbstractPartyCharacteristic> buildPartyCharacteristic(UserConnector userConnector, SCIM2User userSCIM2){
          List<AbstractPartyCharacteristic> partyCharacteristics = new ArrayList<>();

          if(userSCIM2.getUrlPhoto() != null && !userSCIM2.getUrlPhoto().isBlank()){
             StringCharacteristic strAvatar = new StringCharacteristic();
             strAvatar.setType(STRING_CHAR_TYPE);
             strAvatar.setValueType(STRING_TYPE);
             strAvatar.setName("avatarPicture");
             strAvatar.setValue(userSCIM2.getUrlPhoto());
             partyCharacteristics.add(strAvatar);
          }

          if(userSCIM2.getPassword() != null && !userSCIM2.getPassword().isBlank()){
             StringCharacteristic strPwd = new StringCharacteristic();
             strPwd.setType(STRING_CHAR_TYPE);
             strPwd.setValueType(STRING_TYPE);
             strPwd.setName("password");
             strPwd.setValue(userSCIM2.getPassword());
             partyCharacteristics.add(strPwd);
          }

          if(userSCIM2.getUserName() != null && !userSCIM2.getUserName().isBlank()){
             StringCharacteristic userName = new StringCharacteristic();
             userName.setType(STRING_CHAR_TYPE);
             userName.setValueType(STRING_TYPE);
             userName.setName("userName");
             userName.setValue(userSCIM2.getUserName());
             partyCharacteristics.add(userName);
          }

          if(userConnector != null){
            if(userConnector.getInterestedTAGs() != null && !userConnector.getInterestedTAGs() .isEmpty()){
              StringArrayCharacteristic strCh = new StringArrayCharacteristic();
              strCh.setType("StringArrayCharacteristic");
              strCh.setValueType(ARRAY_TYPE);
              strCh.setName("interestingItem");
              strCh.setValue(userConnector.getInterestedTAGs());
              partyCharacteristics.add(strCh);
            }


            this.buildConsents(partyCharacteristics, userConnector);
            this.buildContacts(partyCharacteristics, userConnector);
        }
        return partyCharacteristics;
    }

    private void buildConsents(List<AbstractPartyCharacteristic> partyCharacteristics, UserConnector userConnector){
            if(userConnector.getConsents() != null && !userConnector.getConsents().isEmpty() ){
                Consent consent;
                ObjectArrayCharacteristic consentCh = new ObjectArrayCharacteristic();
                   consentCh.setType("ObjectArrayCharacteristic");
                   consentCh.setValueType(ARRAY_TYPE);
                   consentCh.setName("consents");
                List<Map<String, Object>>  lstConsents = new ArrayList<>();
                for(UserConnectorConsent item: userConnector.getConsents()){
                   consent =  new Consent();
                   consent.setConsentId(item.getConsentId());
                   consent.setConsentType(item.getConsentType());
                   consent.setStatus(item.getStatus());
                   consent.setCreatedAt(item.getCreatedAt().substring(0, 19)+"Z");
                   consent.setUpdatedAt(item.getUpdatedAt().substring(0, 19)+"Z");
                   lstConsents.add(toMap(consent, Consent.class));
                }
              consentCh.setValue(lstConsents);
              partyCharacteristics.add(consentCh);
            }
    }

    private void buildContacts(List<AbstractPartyCharacteristic> partyCharacteristics, UserConnector userConnector){

            if(userConnector.getContacts() != null && !userConnector.getContacts().isEmpty()){
                Contact contact;
                ObjectArrayCharacteristic contactCh = new ObjectArrayCharacteristic();
                   contactCh.setType("ObjectArrayCharacteristic");
                   contactCh.setValueType(ARRAY_TYPE);
                   contactCh.setName("contacts");
               List<Map<String, Object>> lstContacts = new ArrayList<>();
               for(UserConnectorContact item: userConnector.getContacts()){
                   contact =  new Contact();
                   contact.setAlias(item.getAlias());
                   contact.setContactId(item.getContactId());
                   contact.setContactPhone(item.getContactPhone());
                   contact.setCreatedAt(item.getCreatedAt().substring(0, 19)+"Z");
                   contact.setIsActive(item.getIsActive());
                   contact.setUpdatedAt(item.getUpdatedAt().substring(0, 19)+"Z");
                   lstContacts.add(toMap(contact, Contact.class));
               }
               contactCh.setValue(lstContacts);
               partyCharacteristics.add(contactCh);
            }         
    }

    private Map<String, Object> toMap(Object object, Class<?> clazz){
        Map<String, Object> result = new LinkedHashMap<>();
        if (object == null) {
            return result;
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                result.put(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                throw new FieldMappingException("Not access to field '" +
                               field.getName() + "' of class " + clazz.getSimpleName(), e);
            }
        }
        return result;
    }

}