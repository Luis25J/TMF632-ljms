package mx.att.digital.api.tmf632.application.usecase.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Consent;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Contact;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ContactMedium;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.IndividualTMF632;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.ObjectArrayCharacteristic;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.Remove;
import mx.att.digital.api.tmf632.infrastructure.in.rest.request.StringCharacteristic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * The type Individual tmf 632 util.
 */
public class IndividualTMF632Util {

    private IndividualTMF632Util(){}

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String STR_CONSENTS = "consents";
    private static final String STR_CONTACTS = "contacts";

    /**
     * Get email from request string.
     *
     * @param tmf632Request the tmf 632 request
     * @return the string
     */
    public static String getEmailFromRequest(IndividualTMF632 tmf632Request){
          return Optional.ofNullable(tmf632Request.getContactMedium())
                         .orElse(Collections.emptyList())
                         .stream()
                         .filter(cm -> "EmailContactMedium".equals(cm.getType()))
                         .map(ContactMedium::getEmailAddress)
                         .findFirst()
                         .orElse(null);
   }

    /**
     * Get phone from request string.
     *
     * @param tmf632Request the tmf 632 request
     * @return the string
     */
    public static String getPhoneFromRequest(IndividualTMF632 tmf632Request){
          return Optional.ofNullable(tmf632Request.getContactMedium())
                         .orElse(Collections.emptyList())
                         .stream()
                         .filter(cm -> "PhoneContactMedium".equals(cm.getType()))
                         .map(ContactMedium::getPhoneNumber)
                         .findFirst()
                         .orElse(null);
   }

    /**
     * Get url photo from request string.
     *
     * @param tmf632Request the tmf 632 request
     * @return the string
     */
    public static String getUrlPhotoFromRequest (IndividualTMF632 tmf632Request){
          return Optional.ofNullable(tmf632Request.getPartyCharacteristic())
                         .orElse(Collections.emptyList())
                         .stream()
                         .filter(ch -> "avatarPicture".equals(ch.getName()))
                         .findFirst()
                         .filter(StringCharacteristic.class::isInstance)
                         .map(StringCharacteristic.class::cast)
                         .map(StringCharacteristic::getValue)
                         .orElse(null);
   }

    /**
     * Get password from request string.
     *
     * @param tmf632Request the tmf 632 request
     * @return the string
     */
    public static String getPasswordFromRequest(IndividualTMF632 tmf632Request){
          return Optional.ofNullable(tmf632Request.getPartyCharacteristic())
                         .orElse(Collections.emptyList())
                         .stream()
                         .filter(ch -> "password".equals(ch.getName()))
                         .findFirst()
                         .filter(StringCharacteristic.class::isInstance)
                         .map(StringCharacteristic.class::cast)
                         .map(StringCharacteristic::getValue)
                         .orElse(null);
   }

    /**
     * Count consents from request int.
     *
     * @param request the request
     * @return the int
     */
    public static int countConsentsFromRequest(IndividualTMF632 request) {
          var partyChars = request.getPartyCharacteristic();
          if (partyChars == null) {
              return 0;
           }

           return partyChars.stream()
               .filter(ach -> ach instanceof ObjectArrayCharacteristic oac
                              && STR_CONSENTS.equals(oac.getName()))
               .mapToInt(ach -> ((ObjectArrayCharacteristic) ach).getValue().size())
               .findFirst()
               .orElse(0);
   }

    /**
     * Gets contacts from request.
     *
     * @param request the request
     * @return the contacts from request
     */
    public static List<Contact> getContactsFromRequest(IndividualTMF632 request) {
        var partyChars = request.getPartyCharacteristic();
        if (partyChars == null) {
            return List.of();
        }

        return partyChars.stream().flatMap(
                    ach -> (ach instanceof ObjectArrayCharacteristic oac
                            && STR_CONTACTS.equals(oac.getName()))
                            ? oac.getValue().stream()
                            : Stream.empty())
                   .map(mapContact -> mapper.convertValue(mapContact, Contact.class))
                   .toList();
    }

    /**
     * Gets consents from request.
     *
     * @param request the request
     * @return the consents from request
     */
    public static List<Consent> getConsentsFromRequest(IndividualTMF632 request) {
        var partyChars = request.getPartyCharacteristic();
        if (partyChars == null) {
            return List.of();
        }

        return partyChars.stream().flatMap(
                    ach -> (ach instanceof ObjectArrayCharacteristic oac
                            && STR_CONSENTS.equals(oac.getName()))
                            ? oac.getValue().stream()
                            : Stream.empty())
                   .map(mapContact -> mapper.convertValue(mapContact, Consent.class))
                   .toList();
    }

    /**
     * Gets chars to removes from request.
     *
     * @param request the request
     * @return the chars to removes from request
     */
    public static List<Remove> getCharsToRemovesFromRequest(IndividualTMF632 request) {
           var partyChars = request.getPartyCharacteristic();
           if (partyChars == null) {
                return List.of();
           }

            return partyChars.stream()
                .flatMap(ach -> (ach instanceof ObjectArrayCharacteristic oac
                          && (STR_CONTACTS.equals(oac.getName())
                              || STR_CONSENTS.equals(oac.getName())))
                        ? oac.getValue().stream()
                        : Stream.empty())
                .filter(map -> map.toString().contains("@op"))
                // convert to Remove
                .map(map -> mapper.convertValue(map, Remove.class))
                .toList();  // returns an unmodifiable List<Remove>
    }

    /**
     * Gets address from request.
     *
     * @param request the request
     * @return the address from request
     */
    public static List<ContactMedium> getAddressFromRequest(IndividualTMF632 request) {
           var lstContactMedium = request.getContactMedium();
           if (lstContactMedium == null) {
               return List.of();
           }

           return lstContactMedium.stream()
                 .filter(cm -> "GeographicAddressContactMedium".equals(cm.getType()))
                 .toList(); 
    }
}
