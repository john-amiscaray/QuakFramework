package io.john.amiscaray.quak.web.controller;

import io.john.amiscaray.quak.web.handler.RequestHandler;

public record SimplePathController<T, U>(Class<T> requestBodyType,
                                                            Class<U> responseBodyType,
                                                            RequestHandler<T, U> requestHandler) implements PathController<T, U> {
}
