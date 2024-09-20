package io.john.amiscaray.backend.framework.security.test.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authentication;
import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.Credentials;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.backend.framework.security.auth.principal.Principal;
import io.john.amiscaray.backend.framework.security.auth.principal.RoleAttachedPrincipal;
import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;
import io.john.amiscaray.backend.framework.security.config.EndpointMapping;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import io.john.amiscaray.backend.framework.security.di.AuthenticationStrategy;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testHTTPBasicAuthenticationFilterForbidsUserFromAdminEndpoint() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createTokenForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { user() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token, "/secret", "POST");
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());

        httpBasicAuthFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testHTTPBasicAuthenticationFilterAllowsAdminAccessToAdminEndpoint() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createTokenForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { admin() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token, "/secret", "POST");
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());
        var filterChain = mock(FilterChain.class);

        httpBasicAuthFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testHTTPBasicAuthenticationFilterAllowsUserWithUserAndAdminRolesAccessToAdminEndpoint() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createTokenForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { user(), admin() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token, "/secret", "POST");
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());
        var filterChain = mock(FilterChain.class);

        httpBasicAuthFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testHTTPBasicAuthenticationFilterAllowsAdminToAccessEndpointWithAnyRole() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createTokenForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { admin() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token);
        var httpBasicAuthFilter = new HttpBasicAuthFilter(authenticator, simpleSecurityConfig());
        var filterChain = mock(FilterChain.class);

        httpBasicAuthFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }


    private Authenticator mockAuthenticator(Credentials credentials) throws InvalidCredentialsException {
        return mockAuthenticator(credentials, mock(Authentication.class));
    }

    private Authenticator mockAuthenticator(Credentials credentials, Authentication mockAuthentication)  throws InvalidCredentialsException{
        var authenticator = mock(Authenticator.class);
        when(authenticator.authenticate(credentials)).thenReturn(mockAuthentication);
        return authenticator;
    }

    private Authentication mockAuthenticationWithRoles(Role[] roles) {
        return new Authentication() {
            @Override
            public Principal getIssuedTo() {
                var issuedTo = mock(RoleAttachedPrincipal.class);
                when(issuedTo.getRoles()).thenReturn(roles);
                return issuedTo;
            }

            @Override
            public Date getIssueTime() {
                return Date.from(Instant.now());
            }

            @Override
            public Date getExpirationTime() {
                return Date.from(Instant.ofEpochMilli(System.currentTimeMillis() + Duration.ofHours(10).toMillis()));
            }
        };
    }

    private String createTokenForCredentials(Credentials credentials) {
        var credentialsString = credentials.getUsername() + ":" + credentials.getPassword();
        return Base64.getEncoder().encodeToString(credentialsString.getBytes());
    }

    private HttpServletRequest mockHttpServletRequest(String token) {
        return mockHttpServletRequest(token, "/", "GET");
    }

    private HttpServletRequest mockHttpServletRequest(String token, String uri, String method) {
        var result = mock(HttpServletRequest.class);
        when(result.getRequestURI()).thenReturn(uri);
        when(result.getMethod()).thenReturn(method);
        when(result.getHeader("Authorization")).thenReturn("Basic " + token);
        return result;
    }

    private SecurityConfig simpleSecurityConfig() {
        return new SecurityConfig(AuthenticationStrategy.BASIC,
                new HashMap<>(Map.of(
                        new EndpointMapping("/"), List.of(Role.any()),
                        new EndpointMapping("/secret", List.of(EndpointMapping.RequestMethodMatcher.POST)), List.of(admin())
                )),
                "",
                Duration.ofHours(10).toMillis()
                );
    }

    private static Role user() {
        return () -> "user";
    }

    private static Role admin() {
        return () -> "admin";
    }
    
    private HttpServletResponse mockResponse() throws IOException {
        var result = mock(HttpServletResponse.class);
        when(result.getWriter()).thenReturn(mock(PrintWriter.class));
        return result;
    }

}
