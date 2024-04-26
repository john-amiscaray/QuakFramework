package io.john.amiscaray.backend.framework.web.handler;

public record PathController<T, U>(Class<T> requestBodyType, Class<U> responseBodyType, RequestHandler<T, U> requestHandler) {
}
