package io.john.amiscaray.backend.framework.web.handler.request;

import io.john.amiscaray.backend.framework.security.auth.Authentication;
import io.john.amiscaray.backend.framework.security.auth.filter.SecurityFilter;

import java.util.Map;

public sealed interface Request<T> permits SimpleRequest, DynamicPathRequest{

    T body();

    RequestMethod method();

    Map<String, String> headers();

    Map<String, Object> attributes();

    Map<String, String> queryParams();

    default Authentication getUserAuthentication() {
        return (Authentication) attributes().get(SecurityFilter.AUTHENTICATION_ATTRIBUTE);
    }

    default String getJWTAuthToken() {
        return (String) attributes().get(SecurityFilter.VERIFIED_JWT_ATTRIBUTE);
    }

}
