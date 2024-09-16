package io.john.amiscaray.backend.framework.security.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.principal.RoleAttachedPrincipal;
import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;

@AllArgsConstructor
public class HttpBasicAuthFilter implements Filter {

    private Authenticator authenticator;
    private SecurityConfig config;
    private static final Logger LOG = LoggerFactory.getLogger(HttpBasicAuthFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var response = (HttpServletResponse) servletResponse;
        var request = (HttpServletRequest) servletRequest;

        var authorizationHeaderValue = request.getHeader("Authorization");
        if (authorizationHeaderValue == null || !authorizationHeaderValue.startsWith("Basic ")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid Authorization header");
            return;
        }

        var plainTextCredentials = new String(Base64.getDecoder().decode(authorizationHeaderValue.substring(6)))
                .split(":", 2);

        if (plainTextCredentials.length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed credentials");
            return;
        }

        var credentials = new SimpleCredentials(plainTextCredentials[0], plainTextCredentials[1]);

        try {
            var authentication = authenticator.authenticate(credentials);
            servletRequest.setAttribute("authentication", authentication);

            // TODO extract this into a method of a generic SecurityFilter class/interface
            if (config.securedEndpointRoles().containsKey(request.getRequestURI())){
                var validRoles = config.securedEndpointRoles().get(request.getRequestURI());
                var principal = authentication.getIssuedTo();
                if (validRoles.contains(Role.any())) {
                    return;
                }
                if (!(principal instanceof RoleAttachedPrincipal roleAttachedPrincipal)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    LOG.warn("Path is role secured but authenticator gave principal without any roles. Use a RoleAttachedPrincipal to represent the user principal.");
                    return;
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
                        return;
                    }
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidCredentialsException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

}
