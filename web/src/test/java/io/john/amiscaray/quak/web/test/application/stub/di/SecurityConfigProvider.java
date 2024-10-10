package io.john.amiscaray.quak.web.test.application.stub.di;

import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;
import io.john.amiscaray.quak.security.auth.principal.role.Role;
import io.john.amiscaray.quak.security.config.CORSConfig;
import io.john.amiscaray.quak.security.config.EndpointMapping;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import io.john.amiscaray.quak.security.di.AuthenticationStrategy;
import io.john.amiscaray.quak.security.di.SecurityDependencyIDs;

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
                .securePathWithCorsConfig("/*", CORSConfig.allowAll())
                .jwtSecretExpiryTime(Duration.ofHours(10).toMillis())
                .jwtSecretKey("Something Secret")
                .build();
    }

}
