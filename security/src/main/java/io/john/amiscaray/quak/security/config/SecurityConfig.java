package io.john.amiscaray.quak.security.config;

import io.john.amiscaray.quak.core.properties.ApplicationProperties;
import io.john.amiscaray.quak.core.properties.ApplicationProperty;
import io.john.amiscaray.quak.security.auth.principal.role.Role;
import io.john.amiscaray.quak.security.di.AuthenticationStrategy;
import lombok.Builder;
import lombok.Singular;

import java.util.List;
import java.util.Map;

/**
 * The Application's security config.
 * @param authenticationStrategy The authentication strategy to use for secured endpoints.
 * @param securedEndpointRoles A map representing the roles to use to secure a given endpoint.
 * @param pathCorsConfigMap A map of paths and their CORS config.
 * @param jwtSecretKey The JWT secret key to use if using the JWT authentication strategy.
 * @param jwtSecretExpiryTime The JWT expiry time to use if using the JWT authentication strategy.
 */
public record SecurityConfig(AuthenticationStrategy authenticationStrategy,
                             @Singular("securePathWithRole") // TODO change this to secureEndpointWithRole
                             Map<EndpointMapping, List<Role>> securedEndpointRoles,
                             @Singular("securePathWithCorsConfig") // TODO change this to secureEndpointWithCorsConfig
                             Map<String, CORSConfig> pathCorsConfigMap,
                             String jwtSecretKey,
                             Long jwtSecretExpiryTime) {

    private static ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    @Builder
    public SecurityConfig {

    }

    /**
     * Create a security config allowing all CORS and using JWT.
     * @param strategy The authentication strategy.
     * @param securedEndpointRoles A map representing the roles to use to secure a given endpoint.
     */
    public SecurityConfig (AuthenticationStrategy strategy, Map<EndpointMapping, List<Role>> securedEndpointRoles) {
        this(strategy, securedEndpointRoles,
                Map.of("/*", CORSConfig.allowAll()),
                APPLICATION_PROPERTIES.get(ApplicationProperty.JWT_SECRET_KEY),
                Long.parseLong(APPLICATION_PROPERTIES.get(ApplicationProperty.JWT_EXPIRY_TIME)));
    }

}
