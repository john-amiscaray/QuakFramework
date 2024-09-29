package io.john.amiscaray.backend.framework.web.test.application.stub.di;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provider;
import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.config.EndpointMapping;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import io.john.amiscaray.backend.framework.security.di.AuthenticationStrategy;
import io.john.amiscaray.backend.framework.security.di.SecurityDependencyIDs;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Provider
public class SecurityConfigProvider {

    @Provide(dependencyName = SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY_NAME)
    public SecurityConfig securityConfig() {
        return SecurityConfig.builder()
                .authenticationStrategy(AuthenticationStrategy.BASIC)
                .securedEndpointRoles(Map.of(new EndpointMapping("/secured", List.of(EndpointMapping.RequestMethodMatcher.ALL)), List.of(Role.any())))
                .jwtSecretExpiryTime(Duration.ofHours(10).toMillis())
                .jwtSecretKey("Something Secret")
                .build();
    }

}
