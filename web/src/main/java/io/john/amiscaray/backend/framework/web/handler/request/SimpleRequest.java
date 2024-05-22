package io.john.amiscaray.backend.framework.web.handler.request;

import java.util.HashMap;
import java.util.Map;

public record SimpleRequest<T>(Map<String, String> headers,
                               RequestMethod method,
                               T body) implements Request<T>{

    public SimpleRequest(RequestMethod method, T body) {
        this(new HashMap<>(), method, body);
    }

}
