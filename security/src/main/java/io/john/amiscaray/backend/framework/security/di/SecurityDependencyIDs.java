package io.john.amiscaray.backend.framework.security.di;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.Filter;

public class SecurityDependencyIDs {

    public static final String SECURITY_CONFIG_DEPENDENCY_NAME = "applicationSecurityConfig";
    public static final String SECURITY_FILTER_DEPENDENCY_NAME = "applicationSecurityFilter";
    public static final String AUTHENTICATOR_DEPENDENCY_NAME = "applicationAuthenticator";

    public static final DependencyID<SecurityConfig> SECURITY_CONFIG_DEPENDENCY = new DependencyID<>(SECURITY_CONFIG_DEPENDENCY_NAME, SecurityConfig.class);
    public static final DependencyID<Filter> SECURITY_FILTER_DEPENDENCY = new DependencyID<>(SECURITY_FILTER_DEPENDENCY_NAME, Filter.class);
    public static final DependencyID<Authenticator> AUTHENTICATOR_DEPENDENCY = new DependencyID<>(AUTHENTICATOR_DEPENDENCY_NAME, Authenticator.class);

}
