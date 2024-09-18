package io.john.amiscaray.backend.framework.security.auth.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.jwt.JwtUtil;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JWTAuthFilter extends SecurityFilter{

    private final JwtUtil jwtUtil;

    public JWTAuthFilter(Authenticator authenticator,
                         SecurityConfig securityConfig,
                         JwtUtil jwtUtil) {
        super(authenticator, securityConfig);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var httpResponse = (HttpServletResponse) response;
        var authorizationHeaderValue = httpRequest.getHeader("Authorization");

        if (authorizationHeaderValue == null) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        } else if (!authorizationHeaderValue.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write("Malformed authorization header");
            return;
        }

        var rawToken = authorizationHeaderValue.substring("Bearer ".length());
        try {
            var verifiedToken = jwtUtil.validateTokenAndGetDecoded(rawToken);
            var authentication = authenticator.authenticate(verifiedToken.getSubject());

            httpRequest.setAttribute(VERIFIED_JWT_ATTRIBUTE, verifiedToken);
            httpRequest.setAttribute(AUTHENTICATION_ATTRIBUTE, authentication);
        } catch (JWTVerificationException | InvalidCredentialsException ex) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Invalid token");
            return;
        }

        chain.doFilter(request, response);
    }
}
