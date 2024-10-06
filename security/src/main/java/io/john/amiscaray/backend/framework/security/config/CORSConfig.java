package io.john.amiscaray.backend.framework.security.config;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public record CORSConfig(
        @Singular("allowOrigin")
        List<String> allowedOrigins,
        boolean allowAllHeaders,
        @Singular("allowHeader")
        List<String> allowedHeaders,
        @Singular("allowMethod")
        List<String> allowedMethods) {

    public CORSConfig(List<String> allowedOrigins) {
        this(allowedOrigins,
                true,
                List.of(),
                List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD", "CONNECT", "TRACE"));
    }

    public static CORSConfig allowAll() {
        return new CORSConfig(List.of("*"));
    }

}
