package io.john.amiscaray.quak.security.auth.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Applies JWT based authentication.
 */
public class JWTAuthFilter extends AuthenticationFilter {

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
        var securedURLPattern = getMatchingSecuredEndpoint(httpRequest.getRequestURI(), httpRequest.getMethod());

        if (securedURLPattern == null) {
            chain.doFilter(request, response);
            return;
        }
        var authorizationHeaderValue = httpRequest.getHeader("Authorization");

        if (authorizationHeaderValue == null) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

            if (validateUserRoles(httpRequest, httpResponse, authentication)) {
                httpRequest.setAttribute(VERIFIED_JWT_ATTRIBUTE, verifiedToken);
                httpRequest.setAttribute(AUTHENTICATION_ATTRIBUTE, authentication);
                chain.doFilter(request, response);
            }
        } catch (JWTVerificationException | InvalidCredentialsException ex) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Invalid token");
        }
    }
}
