package io.john.amiscaray.quak.web.handler;

import io.john.amiscaray.quak.http.request.DynamicPathRequest;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.response.Response;

/**
 * A function used to handle an HTTP request for a single path. This request is for a path with path variables.
 * @param <T> The type of the request body.
 * @param <U> The type of the response body.
 */
@FunctionalInterface
public interface DynamicPathRequestHandler<T, U> extends RequestHandler<T, U>{

    /**
     * Handles a request with path variables.
     * @param request The HTTP request.
     * @return The HTTP response.
     */
    Response<U> handleDynamicPathRequest(DynamicPathRequest<T> request);

    /**
     * Handles a generic HTTP request.
     * @param request The HTTP request.
     * @return The HTTP response.
     */
    default Response<U> handleRequest(Request<T> request) {
        return handleDynamicPathRequest((DynamicPathRequest<T>) request);
    }

}
