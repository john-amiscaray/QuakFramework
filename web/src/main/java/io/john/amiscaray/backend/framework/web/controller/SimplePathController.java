package io.john.amiscaray.backend.framework.web.controller;

import io.john.amiscaray.backend.framework.web.handler.RequestHandler;

public record SimplePathController<T, U>(Class<T> requestBodyType,
                                                            Class<U> responseBodyType,
                                                            RequestHandler<T, U> requestHandler) implements PathController<T, U> {
}
