package io.john.amiscaray.backend.framework.web.handler;

import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.response.Response;

@FunctionalInterface
public interface DynamicPathRequestHandler<T, U> extends RequestHandler<T, U>{

    Response<U> handleDynamicPathRequest(DynamicPathRequest<T> request);

    default Response<U> handleRequest(Request<T> request) {
        return handleDynamicPathRequest((DynamicPathRequest<T>) request);
    }

}
