package io.john.amiscaray.backend.framework.web.handler.request;

import java.util.HashMap;
import java.util.Map;

public record SimpleRequest<T>(Map<String, String> headers,
                               Map<String, String> queryParams,
                               RequestMethod method,
                               T body) implements Request<T>{

    public SimpleRequest(Map<String, String> headers,
                         RequestMethod method,
                         T body) {
        this(headers, new HashMap<>(), method, body);
    }

    public SimpleRequest(RequestMethod method, T body) {
        this(new HashMap<>(), new HashMap<>(), method, body);
    }

    public SimpleRequest(Map<String, String> queryParams) {
        this(new HashMap<>(), queryParams, RequestMethod.GET, null);
    }

}