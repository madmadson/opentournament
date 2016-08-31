package madson.org.opentournament.utility.web;

/**
 * Exception wrapping errors in the HttpRequest. Contains the http status code for further error handling.
 */
public class HttpRequestException extends Exception {

    private int statusCode;
    private ResponseState state;
    private String errorMessage;

    public HttpRequestException(int statusCode, String errorMessage, ResponseState state) {

        super(errorMessage != null ? errorMessage : "HttpRequest returned statusCode: " + statusCode);
        this.statusCode = statusCode;
        this.state = state;
        this.errorMessage = errorMessage;
    }

    public int getStatusCode() {

        return statusCode;
    }


    public ResponseState getState() {

        return state;
    }


    public boolean hasError() {

        return errorMessage != null;
    }


    public String getError() {

        return errorMessage;
    }
}
