package mx.att.digital.api.tmf632.domain.enums;

import mx.att.digital.api.tmf632.domain.constants.Constants.*;

import lombok.Getter;

/**
 * The enum Builder error enum.
 */
@Getter
public enum BuilderErrorEnum {
    /**
     * Unexpected error builder error enum.
     */
    UNEXPECTED_ERROR(ContentError.UNEXPECTED_ERROR_CODE, ContentError.GENERAL_ERROR, 500),
    /**
     * Oauth not available builder error enum.
     */
    OAUTH_NOT_AVAILABLE(ContentError.OAUTH_NOT_AVAILABLE_CODE, ContentError.ERROR.concat(
      ContentError.OAUTH_NOT_AVAILABLE_MESSAGE), 500),
    /**
     * Oauth not found builder error enum.
     */
    OAUTH_NOT_FOUND(ContentError.OAUTH_NOT_AVAILABLE_CODE, ContentError.ERROR.concat(
      ContentError.OAUTH_NOT_AVAILABLE_MESSAGE), 404),
    /**
     * Not connected builder error enum.
     */
    NOT_CONNECTED(ContentError.UNEXPECTED_ERROR_CODE, ContentError.ERROR, 400),
    /**
     * Missing required param builder error enum.
     */
    MISSING_REQUIRED_PARAM(ContentError.MISSING_REQUIRED_PARAM_CODE, ContentError.ERROR.concat(
      ContentError.MISSING_REQUIRED_PARAM_MESSAGE), 400),
    /**
     * Not found builder error enum.
     */
    NOT_FOUND("-1", ContentError.EMPTY,404);

    private final String code;
    private final String message;
    private final Integer httpStatus;

    BuilderErrorEnum(String code, String message, Integer httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
