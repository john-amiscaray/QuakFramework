package io.john.amiscaray.quak.security.di;

/**
 * The strategy to authenticate secured endpoints.
 */
public enum AuthenticationStrategy {
    /**
     * Use HTTP basic authentication.
     */
    BASIC,
    /**
     * Use JWT-based authentication.
     */
    JWT
}
