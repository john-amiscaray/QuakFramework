package io.john.amiscaray.quak.http.request;


import io.john.amiscaray.quak.security.auth.Authentication;
import io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter;

import java.util.Map;

/**
 * Represents an HTTP Request.
 * @param <T> The type of the request body.
 */
public sealed interface Request<T> permits SimpleRequest, DynamicPathRequest{

    /**
     * @return The request body.
     */
    T body();

    /**
     * @return The request method.
     */
    RequestMethod method();

    /**
     * @return The request headers as a map.
     */
    Map<String, String> headers();

    /**
     * @return Attributes for the request (for server-side use).
     */
    Map<String, Object> attributes();

    /**
     * @return The query params as a map.
     */
    Map<String, String> queryParams();

    /**
     * Retrieves the user's authentication from the request attributes.
     * @return The user's authentication.
     */
    default Authentication getUserAuthentication() {
        return (Authentication) attributes().get(AuthenticationFilter.AUTHENTICATION_ATTRIBUTE);
    }

    /**
     * Retrieves the user's JWT from the request attributes.
     * @return The user's JWT.
     */
    default String getJWTAuthToken() {
        return (String) attributes().get(AuthenticationFilter.VERIFIED_JWT_ATTRIBUTE);
    }

}
