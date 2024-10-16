package io.john.amiscaray.quak.security.auth.filter;

import io.john.amiscaray.quak.core.properties.ApplicationProperties;
import io.john.amiscaray.quak.core.properties.ApplicationProperty;
import io.john.amiscaray.quak.security.auth.principal.RoleAttachedPrincipal;
import io.john.amiscaray.quak.security.auth.principal.role.Role;
import io.john.amiscaray.quak.security.config.EndpointMapping;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import io.john.amiscaray.quak.security.filter.SecurityFilter;
import io.john.amiscaray.quak.security.auth.Authentication;
import io.john.amiscaray.quak.security.auth.Authenticator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * A filter used to verify the credentials given in the Authorization header.
 */
@AllArgsConstructor
public abstract class AuthenticationFilter extends SecurityFilter {

    protected Authenticator authenticator;
    protected SecurityConfig securityConfig;

    /**
     * A request attribute for the verified JWT when using the JWT authentication strategy.
     */
    public static final String VERIFIED_JWT_ATTRIBUTE = "jwt";
    /**
     * A request attribute for the verified {@link io.john.amiscaray.quak.security.auth.Authentication} produced by the filter.
     */
    public static final String AUTHENTICATION_ATTRIBUTE = "authentication";

    private static final Logger LOG = LoggerFactory.getLogger(HttpBasicAuthFilter.class);

    protected boolean validateUserRoles(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var securedURLPattern = getMatchingSecuredEndpoint(request.getRequestURI(), request.getMethod());
        if (securedURLPattern != null) {
            var validRoles = securityConfig.securedEndpointRoles().get(securedURLPattern);
            var principal = authentication.getIssuedTo();
            if (validRoles.contains(Role.any())) {
                return true;
            }
            if (!(principal instanceof RoleAttachedPrincipal roleAttachedPrincipal)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                LOG.warn("Path is role secured but authenticator gave principal without any roles. Use a RoleAttachedPrincipal to represent the user principal.");
                return false;
            } else {
                var roles = roleAttachedPrincipal.getRoles();
                var hasValidRole = false;
                for (var role : roles) {
                    if (validRoles.contains(role)) {
                        hasValidRole = true;
                        break;
                    }
                }
                if (!hasValidRole) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

    protected EndpointMapping getMatchingSecuredEndpoint(String url, String method) {
        var servletContextPath = ApplicationProperties.getInstance().getOrElse(ApplicationProperty.CONTEXT_PATH, "");
        for (var entry : securityConfig.securedEndpointRoles().entrySet()) {
            if (urlMatchesPathPattern(url, servletContextPath + entry.getKey().url()) && requestMethodMatchesMatchers(method, entry.getKey().methods())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private boolean requestMethodMatchesMatchers(String method, List<EndpointMapping.RequestMethodMatcher> matchers) {
        if (matchers.contains(EndpointMapping.RequestMethodMatcher.ALL)) {
            return true;
        } else if (matchers.contains(EndpointMapping.RequestMethodMatcher.valueOf(method))) {
            return true;
        } else {
            return List.of(
                    EndpointMapping.RequestMethodMatcher.POST,
                    EndpointMapping.RequestMethodMatcher.DELETE,
                    EndpointMapping.RequestMethodMatcher.PATCH,
                    EndpointMapping.RequestMethodMatcher.PUT
            ).contains(EndpointMapping.RequestMethodMatcher.valueOf(method)) && matchers.contains(EndpointMapping.RequestMethodMatcher.ANY_MODIFYING);
        }
    }


}
