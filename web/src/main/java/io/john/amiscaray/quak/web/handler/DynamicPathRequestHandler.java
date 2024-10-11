package io.john.amiscaray.quak.web.handler;

import io.john.amiscaray.quak.http.request.DynamicPathRequest;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.response.Response;

@FunctionalInterface
public interface DynamicPathRequestHandler<T, U> extends RequestHandler<T, U>{

    Response<U> handleDynamicPathRequest(DynamicPathRequest<T> request);

    default Response<U> handleRequest(Request<T> request) {
        return handleDynamicPathRequest((DynamicPathRequest<T>) request);
    }

}
