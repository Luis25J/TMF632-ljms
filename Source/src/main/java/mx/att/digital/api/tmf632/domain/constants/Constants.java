package mx.att.digital.api.tmf632.domain.constants;

import java.io.Serial;
import java.io.Serializable;

/**
 * The type Constants.
 */
public class Constants {

    /**
     * The constant SUCCESS_CODE.
     */
// Success Constants
    public static final String   SUCCESS_CODE            = "00";
    /**
     * The constant SUCCESS_MESSAGE.
     */
// Mensaje de éxito
    public static final String   SUCCESS_MESSAGE         = "Transación Exitosa";
    /**
     * The constant BEARER.
     */
    public static final String   BEARER                  = "Bearer ";
    /**
     * The constant BASIC.
     */
    public static final String   BASIC                   = "Basic ";
    /**
     * The constant TOKEN_ID.
     */
    public static final String   TOKEN_ID = "tokenWSO2PartyManagement";

    /**
     * The type Log trace.
     */
    public static final class LogTrace implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
        /**
         * The constant CONNECTION_NOT_REACHED.
         */
        public static final String CONNECTION_NOT_REACHED = "Connection not reached";
        /**
         * The constant INITT.
         */
        public static final String INITT = "Init timestamp: ";
        /**
         * The constant OUTT.
         */
        public static final String OUTT = "Out timestamp: ";
        /**
         * The constant CALL.
         */
        public static final String CALL = "Endpoint: ";
        /**
         * The constant TIME.
         */
        public static final String TIME = ", Execution: ";
        /**
         * The constant REQ.
         */
        public static final String REQ = "Request: ";
        /**
         * The constant RESP.
         */
        public static final String RESP = "Response: ";
  }

    /**
     * The type Content error.
     */
    public static final class ContentError implements Serializable {
        @Serial
        private static final long  serialVersionUID                               = 1L;

        /**
         * The constant ERROR.
         */
        public static final String ERROR                                          = "Error en ";
        /**
         * The constant EMPTY.
         */
        public static final String EMPTY                                          = "";
        /**
         * The constant HEADER_INCOMPLETE_PARAMS.
         */
        public static final String HEADER_INCOMPLETE_PARAMS                       = "HEADER_INCOMPLETE_PARAMS";
        /**
         * The constant HEADER_FORMATTING_ERROR.
         */
        public static final String HEADER_FORMATTING_ERROR                        = "HEADER_FORMATTING_ERROR";
        /**
         * The constant BODY_INCOMPLETE_PARAMS.
         */
        public static final String BODY_INCOMPLETE_PARAMS                         = "BODY_INCOMPLETE_PARAMS";
        /**
         * The constant BODY_FORMATTING_ERROR.
         */
        public static final String BODY_FORMATTING_ERROR                          = "BODY_FORMATTING_ERROR";
        /**
         * The constant MISSING_REQUIRED_PARAM_CODE.
         */
        public static final String MISSING_REQUIRED_PARAM_CODE                    = "-1";
        /**
         * The constant MISSING_REQUIRED_PARAM_MESSAGE.
         */
        public static final String MISSING_REQUIRED_PARAM_MESSAGE                 = "";
        /**
         * The constant UNEXPECTED_ERROR_CODE.
         */
        public static final String UNEXPECTED_ERROR_CODE                          = "-1";
        /**
         * The constant GENERAL_ERROR.
         */
        public static final String GENERAL_ERROR                                  = "General Exception: ";
        /**
         * The constant NOT_CONNECTED.
         */
        public static final String NOT_CONNECTED                                  = "NOT_CONNECTED";
        /**
         * The constant OAUTH_NOT_AVAILABLE_CODE.
         */
        public static final String OAUTH_NOT_AVAILABLE_CODE                       = "13";
        /**
         * The constant OAUTH_NOT_AVAILABLE_MESSAGE.
         */
        public static final String OAUTH_NOT_AVAILABLE_MESSAGE                    = "Service /oauth2/token: ";
    }

    private Constants() {
        // Needed to instance
    }
}
