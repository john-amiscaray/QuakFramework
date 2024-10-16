package io.john.amiscaray.quak.web.handler;

import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.response.Response;

/**
 * A function used to handle HTTP requests.
 * @param <T> The type of the request body.
 * @param <U> The type of the response body.
 */
@FunctionalInterface
public interface RequestHandler<T, U> {

    /**
     * Handles an HTTP request.
     * @param request The HTTP request.
     * @return The HTTP response.
     */
    Response<U> handleRequest(Request<T> request);

}
