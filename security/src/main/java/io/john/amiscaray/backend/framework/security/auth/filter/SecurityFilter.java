package io.john.amiscaray.backend.framework.security.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authentication;
import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.principal.RoleAttachedPrincipal;
import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@AllArgsConstructor
public abstract class SecurityFilter implements Filter {

    protected Authenticator authenticator;
    protected SecurityConfig securityConfig;

    public static final String VERIFIED_JWT_ATTRIBUTE = "jwt";
    public static final String AUTHENTICATION_ATTRIBUTE = "authentication";

    private static final Logger LOG = LoggerFactory.getLogger(HttpBasicAuthFilter.class);

    protected boolean validateUserRoles(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        if (securityConfig.securedEndpointRoles().containsKey(request.getRequestURI())) {
            var validRoles = securityConfig.securedEndpointRoles().get(request.getRequestURI());
            var principal = authentication.getIssuedTo();
            if (validRoles.contains(Role.any())) {
                return true;
            }
            if (!(principal instanceof RoleAttachedPrincipal roleAttachedPrincipal)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }


}
