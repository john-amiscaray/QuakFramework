package io.john.amiscaray.quak.security.auth.filter;

import io.john.amiscaray.quak.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import io.john.amiscaray.quak.security.auth.Authenticator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;

/**
 * Applies HTTP Basic Authentication
 */
public class HttpBasicAuthFilter extends AuthenticationFilter {

    public HttpBasicAuthFilter(Authenticator authenticator, SecurityConfig securityConfig) {
        super(authenticator, securityConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var response = (HttpServletResponse) servletResponse;
        var request = (HttpServletRequest) servletRequest;
        var securedURLPattern = getMatchingSecuredEndpoint(request.getRequestURI(), request.getMethod());

        if (securedURLPattern == null) {
            filterChain.doFilter(request, response);
            return;
        }

        var authorizationHeaderValue = request.getHeader("Authorization");
        if (authorizationHeaderValue == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } else if (!authorizationHeaderValue.startsWith("Basic ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Missing or invalid Authorization header");
            return;
        }

        var plainTextCredentials = new String(Base64.getDecoder().decode(authorizationHeaderValue.substring(6)))
                .split(":", 2);

        if (plainTextCredentials.length != 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("Malformed credentials");
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
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(e.getMessage());
        }
    }

}
