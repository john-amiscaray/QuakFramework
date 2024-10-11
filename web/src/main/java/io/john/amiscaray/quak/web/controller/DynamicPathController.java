package io.john.amiscaray.quak.web.controller;

import io.john.amiscaray.quak.web.handler.DynamicPathRequestHandler;

public record DynamicPathController<T, U>(Class<T> requestBodyType,
                                    Class<U> responseBodyType,
                                    DynamicPathRequestHandler<T, U> requestHandler) implements PathController<T, U> {
}
