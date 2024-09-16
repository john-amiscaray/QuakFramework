package io.john.amiscaray.backend.framework.security.test.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authentication;
import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.Credentials;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import io.john.amiscaray.backend.framework.security.di.SecurityStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class HttpBasicAuthFilterTest {

    @Test
    public void testHTTPBasicAuthWithValidCredentialsCallsAuthenticator() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createTokenForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials);
        var request = mockHttpServletRequest(token);
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());

        httpBasicAuthFilter.doFilter(request, mockResponse(), mock(FilterChain.class));
        verify(authenticator, times(1)).authenticate(userCredentials);
    }

    @Test
    public void testHTTPBasicAuthWithInValidCredentialsYieldsUnauthorizedResponse() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createTokenForCredentials(userCredentials);
        var authenticator = mock(Authenticator.class);
        when(authenticator.authenticate(any(Credentials.class))).thenThrow(new InvalidCredentialsException());
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        var response = mockResponse();

        httpBasicAuthFilter.doFilter(request, response, mock(FilterChain.class));
        // TODO find a way to make the string in these test not necessarily rely on the implementation
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testHTTPBasicAuthWithAuthorizationHeaderMissingBasicPrefix() throws ServletException, IOException {
        var token = "Something";
        var authenticator = mock(Authenticator.class);
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        when(request.getHeader("Authorization")).thenReturn(token);
        var response = mockResponse();

        httpBasicAuthFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testHTTPBasicAuthWithAuthorizationHeaderWithMalformedCredentialString() throws ServletException, IOException {
        var token = "Basic " + Base64.getEncoder().encodeToString("user pass".getBytes());
        var authenticator = mock(Authenticator.class);
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        when(request.getHeader("Authorization")).thenReturn(token);
        var response = mockResponse();

        httpBasicAuthFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    private Authenticator mockAuthenticator(Credentials credentials) throws InvalidCredentialsException {
        var authenticator = mock(Authenticator.class);
        when(authenticator.authenticate(credentials)).thenReturn(mock(Authentication.class));
        return authenticator;
    }

    private String createTokenForCredentials(Credentials credentials) {
        var credentialsString = credentials.getUsername() + ":" + credentials.getPassword();
        return Base64.getEncoder().encodeToString(credentialsString.getBytes());
    }

    private HttpServletRequest mockHttpServletRequest(String token) {
        var result = mock(HttpServletRequest.class);
        when(result.getHeader("Authorization")).thenReturn("Basic " + token);
        return result;
    }

    private SecurityConfig simpleSecurityConfig() {
        return new SecurityConfig(SecurityStrategy.BASIC, new HashMap<>(Map.of("/", List.of(Role.any()))));
    }
    
    private HttpServletResponse mockResponse() throws IOException {
        var result = mock(HttpServletResponse.class);
        when(result.getWriter()).thenReturn(mock(PrintWriter.class));
        return result;
    }

}
