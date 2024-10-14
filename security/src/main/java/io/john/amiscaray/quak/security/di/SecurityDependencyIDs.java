package io.john.amiscaray.quak.security.di;

import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter;
import io.john.amiscaray.quak.security.cors.filter.CORSFilter;
import io.john.amiscaray.quak.security.config.SecurityConfig;

/**
 * The dependency IDs and names for security configs.
 */
public class SecurityDependencyIDs {

    public static final String SECURITY_CONFIG_DEPENDENCY_NAME = "applicationSecurityConfig";
    public static final String SECURITY_FILTER_DEPENDENCY_NAME = "applicationSecurityFilter";
    public static final String AUTHENTICATOR_DEPENDENCY_NAME = "applicationAuthenticator";
    public static final String CORS_FILTER_DEPENDENCY_NAME = "corsFilter";

    public static final DependencyID<SecurityConfig> SECURITY_CONFIG_DEPENDENCY = new DependencyID<>(SECURITY_CONFIG_DEPENDENCY_NAME, SecurityConfig.class);
    public static final DependencyID<AuthenticationFilter> SECURITY_FILTER_DEPENDENCY = new DependencyID<>(SECURITY_FILTER_DEPENDENCY_NAME, AuthenticationFilter.class);
    public static final DependencyID<Authenticator> AUTHENTICATOR_DEPENDENCY = new DependencyID<>(AUTHENTICATOR_DEPENDENCY_NAME, Authenticator.class);
    public static final DependencyID<CORSFilter> CORS_FILTER_DEPENDENCY_ID = new DependencyID<>(CORS_FILTER_DEPENDENCY_NAME, CORSFilter.class);

}
