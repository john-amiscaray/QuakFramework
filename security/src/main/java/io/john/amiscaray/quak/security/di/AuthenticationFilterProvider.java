package io.john.amiscaray.quak.security.di;

import io.john.amiscaray.quak.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.security.auth.filter.JWTAuthFilter;
import io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter;
import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;

import java.util.List;

import static io.john.amiscaray.quak.security.di.SecurityDependencyIDs.*;

public class AuthenticationFilterProvider implements DependencyProvider<AuthenticationFilter> {

    @Override
    public boolean isDependencyOptional() {
        return true;
    }

    @Override
    public DependencyID<AuthenticationFilter> getDependencyID() {
        return SECURITY_FILTER_DEPENDENCY;
    }

    @Override
    public ProvidedDependency<AuthenticationFilter> provideDependency(ApplicationContext context) {
        var authenticator = context.getInstance(AUTHENTICATOR_DEPENDENCY);
        var securityConfig = context.getInstance(SECURITY_CONFIG_DEPENDENCY);
        var jwtUtil = new JwtUtil(securityConfig);

        if (AuthenticationStrategy.JWT.equals(securityConfig.authenticationStrategy())) {
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
