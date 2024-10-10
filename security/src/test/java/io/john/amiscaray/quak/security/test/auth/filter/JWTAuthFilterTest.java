package io.john.amiscaray.quak.security.test.auth.filter;

import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.security.auth.credentials.Credentials;
import io.john.amiscaray.quak.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.quak.security.auth.filter.JWTAuthFilter;
import io.john.amiscaray.quak.security.auth.filter.AuthenticationFilter;
import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import io.john.amiscaray.quak.security.di.AuthenticationStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class JWTAuthFilterTest extends AuthenticationFilterTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void beforeEach() {
        jwtUtil = new JwtUtil(simpleSecurityConfig());
    }

    @Override
    public AuthenticationFilter initFilter(Authenticator authenticator, SecurityConfig config) {
        return new JWTAuthFilter(authenticator, config, new JwtUtil(config));
    }

    @Override
    protected String malformedCredentials() {
        return "Bearer dsfjalkdsjflsdfdsadjs.dsfjldfdsfdsfdsfdsfdsfslfds.daladsfasdfsafdsqwewq";
    }

    @Override
    protected String createAuthorizationHeaderForCredentials(Credentials credentials, Authenticator authenticator) {
        return "Bearer " + jwtUtil.generateToken(authenticator.lookupPrincipal(credentials).orElseThrow());
    }

    @Override
    protected AuthenticationStrategy authenticationStrategy() {
        return AuthenticationStrategy.JWT;
    }

    @Test
    public void testAuthFilterGivenValidCredentialsCallsAuthenticatorAuthenticateOnSecurityID() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var authenticator = mockAuthenticator(userCredentials);
        var token = createAuthorizationHeaderForCredentials(userCredentials, authenticator);
        var request = mockHttpServletRequest(token);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var principal = authenticator.lookupPrincipal(userCredentials).orElseThrow();

        authFilter.doFilter(request, mockResponse(), mock(FilterChain.class));
        verify(authenticator, times(1)).authenticate(principal.getSecurityID());
    }

    @Test
    public void testAuthFilterGivenValidCredentialsAddsAuthenticationAsRequestAttribute() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var authenticator = mockAuthenticator(userCredentials);
        var token = createAuthorizationHeaderForCredentials(userCredentials, authenticator);
        var request = mockHttpServletRequest(token);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var principal = authenticator.lookupPrincipal(userCredentials).orElseThrow();

        authFilter.doFilter(request, mockResponse(), mock(FilterChain.class));
        verify(request, times(1)).setAttribute(AuthenticationFilter.AUTHENTICATION_ATTRIBUTE, authenticator.authenticate(principal.getSecurityID()));
    }

    @Test
    public void testAuthFilterGivenAuthorizationHeaderWithMalformedCredentialStringYieldUnauthorized() throws ServletException, IOException {
        var token = malformedCredentials();
        var authenticator = mock(Authenticator.class);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        when(request.getHeader("Authorization")).thenReturn(token);
        var response = mockResponse();

        authFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
