package io.john.amiscaray.backend.framework.web.handler.response;

import java.util.HashMap;
import java.util.Map;

public record Response<T>(Map<String, String> headers, int status, T body) {

    public Response(int status, T body) {
        this(new HashMap<>(), status, body);
    }

}
