package io.john.amiscaray.backend.framework.security.config;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public record CORSConfig(
        @Singular("allowOrigin")
        List<String> allowedOrigins,
        @Singular("allowHeaders")
        List<String> allowedHeaders,
        @Singular("allowMethods")
        List<String> allowedMethods) {

    public CORSConfig(List<String> allowedOrigins) {
        this(allowedOrigins,
                List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"),
                List.of("Authorization"));
    }

    public static CORSConfig allowAll() {
        return new CORSConfig(List.of("*"));
    }

}
