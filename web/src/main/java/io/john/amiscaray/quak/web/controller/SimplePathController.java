package io.john.amiscaray.quak.web.controller;

import io.john.amiscaray.quak.web.handler.RequestHandler;

/**
 * A simple implementation of {@link io.john.amiscaray.quak.web.controller.PathController}.
 * @param requestBodyType The type of the request body.
 * @param responseBodyType The type of the response body.
 * @param requestHandler A function handling the request and returning a response.
 * @param <T> The type of the request body.
 * @param <U> The type of the response body.
 */
public record SimplePathController<T, U>(Class<T> requestBodyType,
                                                            Class<U> responseBodyType,
                                                            RequestHandler<T, U> requestHandler) implements PathController<T, U> {
}
