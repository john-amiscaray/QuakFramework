package io.john.amiscaray.backend.framework.security.config;

import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;
import io.john.amiscaray.backend.framework.core.properties.ApplicationProperty;
import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.di.AuthenticationStrategy;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

public record SecurityConfig(AuthenticationStrategy authenticationStrategy,
                             @Singular("securePathWithRole")
                             Map<EndpointMapping, List<Role>> securedEndpointRoles,
                             String jwtSecretKey,
                             Long jwtSecretExpiryTime) {

    private static ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    @Builder
    public SecurityConfig {

    }

    public SecurityConfig (AuthenticationStrategy strategy, Map<EndpointMapping, List<Role>> securedEndpointRoles) {
        this(strategy, securedEndpointRoles,
                APPLICATION_PROPERTIES.get(ApplicationProperty.JWT_SECRET_KEY),
                Long.parseLong(APPLICATION_PROPERTIES.get(ApplicationProperty.JWT_EXPIRY_TIME)));
    }

}
