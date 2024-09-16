package io.john.amiscaray.backend.framework.security.di;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.Filter;

public class SecurityDependencyIDs {

    public static final DependencyID<SecurityConfig> SECURITY_CONFIG_DEPENDENCY = new DependencyID<>("applicationSecurityConfig", SecurityConfig.class);
    public static final DependencyID<Filter> SECURITY_FILTER_DEPENDENCY = new DependencyID<>("applicationSecurityFilter", Filter.class);
    public static final DependencyID<Authenticator> AUTHENTICATOR_DEPENDENCY = new DependencyID<>("applicationAuthenticator", Authenticator.class);

}
