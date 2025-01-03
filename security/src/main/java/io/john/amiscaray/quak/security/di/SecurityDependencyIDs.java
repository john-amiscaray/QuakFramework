package io.john.amiscaray.quak.security.di;

import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter;
import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;
import io.john.amiscaray.quak.security.cors.filter.CORSFilter;
import io.john.amiscaray.quak.security.config.SecurityConfig;

/**
 * The dependency IDs and names for security configs.
 */
public class SecurityDependencyIDs {

    /**
     * The name for a dependency for the application's {@link io.john.amiscaray.quak.security.config.SecurityConfig SecurityConfig}.
     */
    public static final String SECURITY_CONFIG_DEPENDENCY_NAME = "applicationSecurityConfig";
    /**
     * The name for a dependency for the application's {@link io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter AuthenticationFilter}.
     */
    public static final String SECURITY_FILTER_DEPENDENCY_NAME = "applicationSecurityFilter";
    /**
     * The name for a dependency for the application's {@link io.john.amiscaray.quak.security.auth.Authenticator Authenticator}.
     */
    public static final String AUTHENTICATOR_DEPENDENCY_NAME = "applicationAuthenticator";
    /**
     * The name for a dependency for the application's {@link io.john.amiscaray.quak.security.cors.filter.CORSFilter CORSFilter}.
     */
    public static final String CORS_FILTER_DEPENDENCY_NAME = "corsFilter";
    /**
     * The name for a dependency for the application's {@link io.john.amiscaray.quak.security.auth.jwt.JwtUtil JWTUtil}.
     */
    public static final String JWT_UTIL_DEPENDENCY_NAME = "jwtUtil";

    /**
     * The dependency ID for the application's {@link io.john.amiscaray.quak.security.config.SecurityConfig SecurityConfig}.
     */
    public static final DependencyID<SecurityConfig> SECURITY_CONFIG_DEPENDENCY = new DependencyID<>(SECURITY_CONFIG_DEPENDENCY_NAME, SecurityConfig.class);
    /**
     * The dependency ID for the application's {@link io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter AuthenticationFilter}.
     */
    public static final DependencyID<AuthenticationFilter> SECURITY_FILTER_DEPENDENCY = new DependencyID<>(SECURITY_FILTER_DEPENDENCY_NAME, AuthenticationFilter.class);
    /**
     * The dependency ID for the application's {@link io.john.amiscaray.quak.security.auth.Authenticator Authenticator}.
     */
    public static final DependencyID<Authenticator> AUTHENTICATOR_DEPENDENCY = new DependencyID<>(AUTHENTICATOR_DEPENDENCY_NAME, Authenticator.class);
    /**
     * The dependency ID for the application's {@link io.john.amiscaray.quak.security.cors.filter.CORSFilter CORSFilter}.
     */
    public static final DependencyID<CORSFilter> CORS_FILTER_DEPENDENCY_ID = new DependencyID<>(CORS_FILTER_DEPENDENCY_NAME, CORSFilter.class);
    /**
     * The dependency ID for the application's {@link io.john.amiscaray.quak.security.auth.jwt.JwtUtil JWTUtil}.
     */
    public static final DependencyID<JwtUtil> JWT_UTIL_DEPENDENCY_ID = new DependencyID<>(JWT_UTIL_DEPENDENCY_NAME, JwtUtil.class);

}
