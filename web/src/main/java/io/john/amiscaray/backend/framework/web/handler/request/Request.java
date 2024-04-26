package io.john.amiscaray.backend.framework.web.handler.request;

import java.util.Map;

public record Request<T>(Map<String, String> headers, RequestMethod method, T body) {
}
