package io.john.amiscaray.quak.http.request;

import java.util.HashMap;
import java.util.Map;

public record DynamicPathRequest<T>(Map<String, String> headers,
                                       Map<String, String> queryParams,
                                       RequestMethod method,
                                       Map<String, String> pathVariables,
                                       T body, Map<String, Object> attributes) implements Request<T>{

    public DynamicPathRequest(RequestMethod method,
                              Map<String, String> pathVariables,
                              T body) {
        this(new HashMap<>(), new HashMap<>(), method, pathVariables, body, new HashMap<>());
    }

    public DynamicPathRequest(Map<String, String> headers,
                              RequestMethod method,
                              Map<String, String> pathVariables,
                              T body) {
        this(headers, new HashMap<>(), method, pathVariables, body, new HashMap<>());
    }

    public DynamicPathRequest(Map<String, String> pathVariables,
                              Map<String, String> queryParams,
                              T body) {
        this(new HashMap<>(), queryParams, RequestMethod.GET, pathVariables, body, new HashMap<>());
    }

}
