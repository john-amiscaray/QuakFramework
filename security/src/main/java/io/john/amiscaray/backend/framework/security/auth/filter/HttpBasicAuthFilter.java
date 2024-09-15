package io.john.amiscaray.backend.framework.security.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.Base64;

@AllArgsConstructor
public class HttpBasicAuthFilter implements Filter {

    private Authenticator authenticator;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var response = (HttpServletResponse) servletResponse;

        var authorizationHeaderValue = ((HttpServletRequest) servletRequest).getHeader("Authorization");
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
            servletRequest.setAttribute("authentication", authenticator.authenticate(credentials));
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidCredentialsException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        }
    }

}
