package io.john.amiscaray.quak.security.di;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;

import java.util.List;

/**
 * Provides the application with a {@link io.john.amiscaray.quak.security.auth.jwt.JwtUtil JWTUtil} instance.
 */
public class JWTUtilProvider implements DependencyProvider<JwtUtil> {

    @Override
    public DependencyID<JwtUtil> getDependencyID() {
        return SecurityDependencyIDs.JWT_UTIL_DEPENDENCY_ID;
    }

    @Override
    public ProvidedDependency<JwtUtil> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(getDependencyID(), new JwtUtil(context.getInstance(SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY)));
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of(
                SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY
        );
    }

}
