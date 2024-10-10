package io.john.amiscaray.quak.web.handler;

import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.response.Response;

@FunctionalInterface
public interface RequestHandler<T, U> {

    Response<U> handleRequest(Request<T> request);

}
