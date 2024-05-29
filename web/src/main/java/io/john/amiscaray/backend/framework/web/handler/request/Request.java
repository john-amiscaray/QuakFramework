package io.john.amiscaray.backend.framework.web.handler.request;

import java.util.Map;

public sealed interface Request<T> permits SimpleRequest, DynamicPathRequest{

    T body();

    RequestMethod method();

    Map<String, String> headers();

    Map<String, String> queryParams();

}
