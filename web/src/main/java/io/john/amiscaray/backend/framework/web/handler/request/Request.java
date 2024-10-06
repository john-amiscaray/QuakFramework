package io.john.amiscaray.backend.framework.web.handler.request;

import io.john.amiscaray.backend.framework.security.auth.Authentication;
import io.john.amiscaray.backend.framework.security.auth.filter.AuthenticationFilter;

import java.util.Map;

public sealed interface Request<T> permits SimpleRequest, DynamicPathRequest{

    T body();

    RequestMethod method();

    Map<String, String> headers();

    Map<String, Object> attributes();

    Map<String, String> queryParams();

    default Authentication getUserAuthentication() {
        return (Authentication) attributes().get(AuthenticationFilter.AUTHENTICATION_ATTRIBUTE);
    }

    default String getJWTAuthToken() {
        return (String) attributes().get(AuthenticationFilter.VERIFIED_JWT_ATTRIBUTE);
    }

}
