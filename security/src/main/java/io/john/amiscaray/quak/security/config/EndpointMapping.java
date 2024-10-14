package io.john.amiscaray.quak.security.config;

import java.util.List;

/**
 * A record which matches endpoint(s) for use in the {@link io.john.amiscaray.quak.security.config.SecurityConfig}.
 * @param url The URL.
 * @param methods The method(s).
 */
public record EndpointMapping (String url, List<RequestMethodMatcher> methods) {

    public EndpointMapping (String url) {
        this(url, List.of(RequestMethodMatcher.ALL));
    }

    public enum RequestMethodMatcher {
        GET,
        HEAD,
        POST,
        PUT,
        DELETE,
        CONNECT,
        OPTIONS,
        TRACE,
        PATCH,
        ALL,
        ANY_MODIFYING
    }

}
