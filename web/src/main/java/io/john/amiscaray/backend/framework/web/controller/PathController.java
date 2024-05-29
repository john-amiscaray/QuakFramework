package io.john.amiscaray.backend.framework.web.controller;

import io.john.amiscaray.backend.framework.web.handler.RequestHandler;

public sealed interface PathController<T, U> permits SimplePathController, DynamicPathController{

    Class<T> requestBodyType();
    Class<U> responseBodyType();
    RequestHandler<T, U> requestHandler();

}
