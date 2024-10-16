package io.john.amiscaray.quak.web.controller;

import io.john.amiscaray.quak.web.handler.DynamicPathRequestHandler;

/**
 * A handles a request for a path with path variables.
 * @param requestBodyType The type of the request body.
 * @param responseBodyType The type of the response body.
 * @param requestHandler A function used to handle the request.
 * @param <T> The type of the request body.
 * @param <U> The type of the response body.
 */
public record DynamicPathController<T, U>(Class<T> requestBodyType,
                                    Class<U> responseBodyType,
                                    DynamicPathRequestHandler<T, U> requestHandler) implements PathController<T, U> {
}
