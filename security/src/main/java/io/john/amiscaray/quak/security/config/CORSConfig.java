package io.john.amiscaray.quak.security.config;

import lombok.Builder;
import lombok.Singular;

import java.util.List;

/**
 * The application's CORS configuration for specific paths. CORS configs for each path is specified in the {@link io.john.amiscaray.quak.security.config.SecurityConfig}.
 * @param allowedOrigins The allowed origins.
 * @param allowAllHeaders Whether all headers should be allowed.
 * @param allowedHeaders The allowed headers.
 * @param allowedMethods The allowed methods.
 */
@Builder
public record CORSConfig(
        @Singular("allowOrigin")
        List<String> allowedOrigins,
        boolean allowAllHeaders,
        @Singular("allowHeader")
        List<String> allowedHeaders,
        @Singular("allowMethod")
        List<String> allowedMethods) {

    /**
     * Create a CORS config allowing all headers and methods for the given origins.
     * @param allowedOrigins The allowed origins.
     */
    public CORSConfig(List<String> allowedOrigins) {
        this(allowedOrigins,
                true,
                List.of(),
                List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS", "HEAD", "CONNECT", "TRACE"));
    }

    /**
     * @return A CORS config allowing all requests.
     */
    public static CORSConfig allowAll() {
        return new CORSConfig(List.of("*"));
    }

}
