package io.john.amiscaray.backend.framework.web.handler.request;

import java.util.Map;

public record DynamicPathRequest<T>(Map<String, String> headers,
                                       RequestMethod method,
                                       Map<String, String> pathVariables,
                                       T body) implements Request<T>{
}
