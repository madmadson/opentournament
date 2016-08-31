package madson.org.opentournament.utility.web;

public enum ResponseState {

    /**
     * 200, 201, 202, 204, 207 response.
     */
    SUCCESSFUL(200, 201, 202, 204, 207),

    /**
     * 302 response.
     */
    REDIRECT(302),

    /**
     * 401 response.
     */
    UNAUTHENTICATED(401),

    /**
     * 403 response.
     */
    NOT_ALLOWED(403),

    /**
     * 404.
     */
    NOT_FOUND(404),

    /**
     * 400.
     */
    REQUEST_ERROR(400),

    /**
     * 500 server error.
     */
    SERVER_ERROR(500, 503),

    /**
     * 503, Service not available (temporary).
     */
    SERVICE_UNAVAILABLE(503),

    /**
     * Not mapped status code.
     */
    UNKNOWN(-1),

    /**
     * Some Error with the Network. E.g. could not connect, IOError.
     */
    NETWORK_ERROR(-1),

    /**
     * Error in the communication with the server. e.g. not the expected JSON Format -> JSONException
     */
    COMMUNICATION_ERROR(-1),

    /**
     * Error while creating a web service, e.g. some problem with the certificate loading.
     */
    SERVICE_CREATION_ERROR(-1),

    /**
     * The server returned the wrong server certificate. Either the server certificate has changed and we have the wrong
     * (e.g. too old) certificate imported in the app (if user has not updated the app) or the user was directed to the
     * wrong server (changes on the dns server, probably attack).
     */
    WRONG_CERTIFICATE_ERROR(-1),

    /**
     * No Cookie given for Cookie Auth.
     */
    NO_COOKIE(-1),

    /**
     * Generic Error code 1 (for custom cases in apps).
     */
    ERROR_CODE_1(-1),

    /**
     * Generic Error code 2 (for custom cases in apps).
     */
    ERROR_CODE_2(-1);

    private int[] statusCodes;

    ResponseState(int... statusCodes) {

        this.statusCodes = statusCodes;
    }

    public static ResponseState byStatusCode(int statusCode) {

        for (ResponseState responseState : values()) {
            for (int code : responseState.statusCodes) {
                if (code == statusCode) {
                    return responseState;
                }
            }
        }

        return ResponseState.UNKNOWN;
    }
}
