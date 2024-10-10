package io.john.amiscaray.backend.framework.http.response;

import io.john.amiscaray.backend.framework.http.status.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public record Response<T>(Map<String, String> headers, int status, T body) {

    public Response(int status, T body) {
        this(new HashMap<>(), status, body);
    }

    public static <T> Response<T> of(T body) {
        return new Response<>(HttpStatus.OK.getCode(), body);
    }

}
