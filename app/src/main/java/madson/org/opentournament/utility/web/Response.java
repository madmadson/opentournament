package madson.org.opentournament.utility.web;

import java.util.List;
import java.util.Map;


/**
 * A response from a HTTP request. Encapsulates the actual response body, the headers, the status code with a parsed
 * state enum for easier handling of the status code, and an error message.
 */
public class Response<T> {

    private T payload;
    private Map<String, List<String>> headerFields;
    private int statusCode;

    public Response(T payload, Map<String, List<String>> headerFields, int statusCode) {

        this.payload = payload;
        this.headerFields = headerFields;
        this.statusCode = statusCode;
    }

    public T getPayload() {

        return payload;
    }


    public Map<String, List<String>> getHeaderFields() {

        return headerFields;
    }


    public int getStatusCode() {

        return statusCode;
    }
}
