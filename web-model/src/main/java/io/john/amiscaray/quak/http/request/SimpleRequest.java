package io.john.amiscaray.quak.http.request;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple HTTP request.
 * @param headers The headers as a map.
 * @param queryParams The query parameters as a map.
 * @param method The request method.
 * @param body The request body.
 * @param attributes The attributes for the request (for server-side use).
 * @param <T> The type of the request body.
 */
public record SimpleRequest<T>(Map<String, String> headers,
                               Map<String, String> queryParams,
                               RequestMethod method,
                               T body, Map<String, Object> attributes) implements Request<T>{

    public SimpleRequest(Map<String, String> headers,
                         RequestMethod method,
                         T body) {
        this(headers, new HashMap<>(), method, body, new HashMap<>());
    }

    public SimpleRequest(RequestMethod method, T body) {
        this(new HashMap<>(), new HashMap<>(), method, body, new HashMap<>());
    }

    public SimpleRequest(Map<String, String> queryParams) {
        this(new HashMap<>(), queryParams, RequestMethod.GET, null, new HashMap<>());
    }

}