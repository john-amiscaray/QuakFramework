package io.john.amiscaray.backend.framework.security.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;

public class HttpBasicAuthFilter extends SecurityFilter {

    public HttpBasicAuthFilter(Authenticator authenticator, SecurityConfig securityConfig) {
        super(authenticator, securityConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var response = (HttpServletResponse) servletResponse;
        var request = (HttpServletRequest) servletRequest;

        var authorizationHeaderValue = request.getHeader("Authorization");
        if (authorizationHeaderValue == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        } else if (!authorizationHeaderValue.startsWith("Basic ")) {
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

            if (validateUserRoles(request, response, authentication)) {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } catch (InvalidCredentialsException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

}
