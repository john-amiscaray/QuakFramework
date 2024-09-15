package io.john.amiscaray.auth.filter;

import io.john.amiscaray.auth.Authenticator;
import io.john.amiscaray.auth.credentials.SimpleCredentials;
import io.john.amiscaray.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.config.SecurityConfiguration;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;

public class HttpBasicAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        var applicationContext = ApplicationContext.getInstance();
        var authenticator = applicationContext.getInstance(new DependencyID<>(
                  SecurityConfiguration.AUTHENTICATOR_DEPENDENCY,
                  Authenticator.class
        ));
        var response = (HttpServletResponse) servletResponse;

        var authorizationHeaderValue = ((HttpServletRequest) servletRequest).getHeader("Authorization");
        if (authorizationHeaderValue == null || !authorizationHeaderValue.startsWith("Basic ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var plainTextCredentials = new String(Base64.getDecoder().decode(authorizationHeaderValue.substring(6)))
                .split(":", 2);

        if (plainTextCredentials.length != 2) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        var credentials = new SimpleCredentials(plainTextCredentials[0], plainTextCredentials[1]);

        try {
            servletRequest.setAttribute("authentication", authenticator.authenticate(credentials));
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidCredentialsException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
