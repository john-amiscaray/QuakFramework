package io.john.amiscaray.backend.framework.web.handler;

import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.response.Response;

@FunctionalInterface
public interface RequestHandler<T, U> {

    Response<U> handleRequest(Request<T> request);

}
