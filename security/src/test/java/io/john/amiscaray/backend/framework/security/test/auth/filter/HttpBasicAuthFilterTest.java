package io.john.amiscaray.backend.framework.security.test.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.Credentials;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.backend.framework.security.auth.filter.AuthenticationFilter;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import io.john.amiscaray.backend.framework.security.di.AuthenticationStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Base64;

import static org.mockito.Mockito.*;

public class HttpBasicAuthFilterTest extends AuthenticationFilterTest {

    @Override
    public AuthenticationFilter initFilter(Authenticator authenticator, SecurityConfig config) {
        return new HttpBasicAuthFilter(authenticator, config);
    }

    @Override
    protected String malformedCredentials() {
        return "Basic " + Base64.getEncoder().encodeToString("user pass".getBytes());
    }

    @Override
    protected String createAuthorizationHeaderForCredentials(Credentials credentials, Authenticator authenticator) {
        var credentialsString = credentials.getUsername() + ":" + credentials.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(credentialsString.getBytes());
    }

    @Override
    protected AuthenticationStrategy authenticationStrategy() {
        return AuthenticationStrategy.BASIC;
    }

    @Test
    public void testAuthFilterGivenValidCredentialsCallsAuthenticatorAuthenticateOnCredentials() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var authenticator = mockAuthenticator(userCredentials);
        var token = createAuthorizationHeaderForCredentials(userCredentials, authenticator);
        var request = mockHttpServletRequest(token);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());

        authFilter.doFilter(request, mockResponse(), mock(FilterChain.class));
        verify(authenticator, times(1)).authenticate(userCredentials);
    }

    @Test
    public void testAuthFilterGivenValidCredentialsAddsAuthenticationAsRequestAttribute() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var authenticator = mockAuthenticator(userCredentials);
        var token = createAuthorizationHeaderForCredentials(userCredentials, authenticator);
        var request = mockHttpServletRequest(token);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());

        authFilter.doFilter(request, mockResponse(), mock(FilterChain.class));
        verify(request, times(1)).setAttribute(AuthenticationFilter.AUTHENTICATION_ATTRIBUTE, authenticator.authenticate(userCredentials));
    }

    @Test
    public void testAuthFilterGivenInValidCredentialsYieldsUnauthorizedResponse() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var authenticator = mock(Authenticator.class);
        var token = createAuthorizationHeaderForCredentials(userCredentials, authenticator);
        when(authenticator.authenticate(any(Credentials.class))).thenThrow(new InvalidCredentialsException());
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        var response = mockResponse();

        authFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testAuthFilterGivenAuthorizationHeaderWithMalformedCredentialStringYieldBadRequest() throws ServletException, IOException {
        var token = malformedCredentials();
        var authenticator = mock(Authenticator.class);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        when(request.getHeader("Authorization")).thenReturn(token);
        var response = mockResponse();

        authFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
