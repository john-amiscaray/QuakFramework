package io.john.amiscaray.quak.http.response;

import io.john.amiscaray.quak.http.status.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP response.
 * @param headers The headers as a map.
 * @param status The status code.
 * @param body The response body.
 * @param <T> The type of the response body.
 */
public record Response<T>(Map<String, String> headers, int status, T body) {

    /**
     * Create a response with no headers.
     * @param status The HTTP status code.
     * @param body The response body.
     */
    public Response(int status, T body) {
        this(new HashMap<>(), status, body);
    }

    /**
     * Returns a response with the given response body and status code of 200.
     * @param body The response body.
     * @return A response object.
     * @param <T> The type of the body.
     */
    public static <T> Response<T> of(T body) {
        return new Response<>(HttpStatus.OK.getCode(), body);
    }

}
