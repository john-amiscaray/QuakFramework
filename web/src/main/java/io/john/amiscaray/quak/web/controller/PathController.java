package io.john.amiscaray.quak.web.controller;

import io.john.amiscaray.quak.web.handler.RequestHandler;

/**
 * Used to handle requests for a single path.
 * @param <T> The type of the request body.
 * @param <U> The type of the response body.
 */
public sealed interface PathController<T, U> permits SimplePathController, DynamicPathController{

    /**
     * @return The type of the request body.
     */
    Class<T> requestBodyType();

    /**
     * @return The type of the response body.
     */
    Class<U> responseBodyType();

    /**
     * @return A function handling the request and returning a response.
     */
    RequestHandler<T, U> requestHandler();

}
