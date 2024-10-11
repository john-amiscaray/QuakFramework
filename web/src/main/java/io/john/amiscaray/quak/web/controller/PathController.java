package io.john.amiscaray.quak.web.controller;

import io.john.amiscaray.quak.web.handler.RequestHandler;

public sealed interface PathController<T, U> permits SimplePathController, DynamicPathController{

    Class<T> requestBodyType();
    Class<U> responseBodyType();
    RequestHandler<T, U> requestHandler();

}
