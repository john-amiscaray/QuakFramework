package io.john.amiscaray.backend.framework.security.di;

import io.john.amiscaray.backend.framework.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.security.auth.filter.JWTAuthFilter;
import io.john.amiscaray.backend.framework.security.auth.filter.SecurityFilter;
import io.john.amiscaray.backend.framework.security.auth.jwt.JwtUtil;

import java.util.List;

import static io.john.amiscaray.backend.framework.security.di.SecurityDependencyIDs.*;

public class AuthenticationFilterProvider implements DependencyProvider<SecurityFilter> {

    @Override
    public boolean isDependencyOptional() {
        return true;
    }

    @Override
    public DependencyID<SecurityFilter> getDependencyID() {
        return SECURITY_FILTER_DEPENDENCY;
    }

    @Override
    public ProvidedDependency<SecurityFilter> provideDependency(ApplicationContext context) {
        var authenticator = context.getInstance(AUTHENTICATOR_DEPENDENCY);
        var securityConfig = context.getInstance(SECURITY_CONFIG_DEPENDENCY);
        var jwtUtil = new JwtUtil(securityConfig);

        if (SecurityStrategy.JWT.equals(securityConfig.strategy())) {
            return new ProvidedDependency<>(
                    getDependencyID(),
                    new JWTAuthFilter(authenticator, securityConfig, jwtUtil)
                    );
        }
        return new ProvidedDependency<>(getDependencyID(), new HttpBasicAuthFilter(authenticator, securityConfig));
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of(
                SECURITY_CONFIG_DEPENDENCY,
                AUTHENTICATOR_DEPENDENCY
        );
    }

}
