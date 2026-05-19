package mx.att.digital.api.tmf632.application.usecase.util;

import mx.att.digital.api.tmf632.infrastructure.out.config.client.dto.tmf629.TMF629CustomResponse;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The type Tmf 629 util.
 */
public class Tmf629Util {

    private Tmf629Util(){}
    /**
     * The constant ALLOWED_PAYMENT_CATEGORIES.
     */
    public static final Set<String> ALLOWED_PAYMENT_CATEGORIES =
            Set.of("prepago", "prepaid", "hybrid");

    /**
     * Is valid payment category boolean.
     *
     * @param response the response
     * @return the boolean
     */
    public static boolean isValidPaymentCategory(List<TMF629CustomResponse> response) {
                return Optional.ofNullable(response)
                .orElse(List.of()).stream()
                .flatMap(pr -> Optional.ofNullable(pr.getCharacteristic())
                        .orElse(List.of())
                        .stream())
                .filter(c -> "Characteristic".equalsIgnoreCase(c.type()))
                .filter(c -> "paymentCategory".equalsIgnoreCase(c.name()))
                .map(TMF629CustomResponse.TMF629Characteristic::valueType)
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .anyMatch(value ->
                        ALLOWED_PAYMENT_CATEGORIES.stream()
                        .anyMatch(value::contains)
                );
}

}
