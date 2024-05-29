package io.john.amiscaray.backend.framework.web.handler.request;

import java.util.HashMap;
import java.util.Map;

public record DynamicPathRequest<T>(Map<String, String> headers,
                                       Map<String, String> queryParams,
                                       RequestMethod method,
                                       Map<String, String> pathVariables,
                                       T body) implements Request<T>{

    public DynamicPathRequest(RequestMethod method,
                              Map<String, String> pathVariables,
                              T body) {
        this(new HashMap<>(), new HashMap<>(), method, pathVariables, body);
    }

    public DynamicPathRequest(Map<String, String> headers,
                              RequestMethod method,
                              Map<String, String> pathVariables,
                              T body) {
        this(headers, new HashMap<>(), method, pathVariables, body);
    }

    public DynamicPathRequest(Map<String, String> pathVariables,
                              Map<String, String> queryParams,
                              T body) {
        this(new HashMap<>(), queryParams, RequestMethod.GET, pathVariables, body);
    }

}
