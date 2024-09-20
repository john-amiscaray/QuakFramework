package io.john.amiscaray.backend.framework.security.test.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authentication;
import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.Credentials;
import io.john.amiscaray.backend.framework.security.auth.credentials.SimpleCredentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.filter.SecurityFilter;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public abstract class SecurityFilterTest {

    public abstract SecurityFilter initFilter(Authenticator authenticator, SecurityConfig config);

    @Test
    public void testAuthFilterGivenValidCredentialsCallsAuthenticator() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createAuthorizationHeaderForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials);
        var request = mockHttpServletRequest(token);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());

        authFilter.doFilter(request, mockResponse(), mock(FilterChain.class));
        verify(authenticator, times(1)).authenticate(userCredentials);
    }

    @Test
    public void testAuthFilterGivenInValidCredentialsYieldsUnauthorizedResponse() throws InvalidCredentialsException, ServletException, IOException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createAuthorizationHeaderForCredentials(userCredentials);
        var authenticator = mock(Authenticator.class);
        when(authenticator.authenticate(any(Credentials.class))).thenThrow(new InvalidCredentialsException());
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        var response = mockResponse();

        authFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testAuthFilterGivenAuthorizationHeaderMissingPrefixYieldsBadRequest() throws ServletException, IOException {
        var token = "Something";
        var authenticator = mock(Authenticator.class);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var request = mockHttpServletRequest(token);
        when(request.getHeader("Authorization")).thenReturn(token);
        var response = mockResponse();

        authFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

    @Test
    public void testAuthFilterForbidsUserFromAdminEndpoint() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createAuthorizationHeaderForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { user() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token, "/secret", "POST");
        var authFilter = initFilter(authenticator, simpleSecurityConfig());

        authFilter.doFilter(request, response, mock(FilterChain.class));
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testAuthFilterAllowsAdminAccessToAdminEndpoint() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createAuthorizationHeaderForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { admin() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token, "/secret", "POST");
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var filterChain = mock(FilterChain.class);

        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testAuthFilterAllowsUserWithUserAndAdminRolesAccessToAdminEndpoint() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createAuthorizationHeaderForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { user(), admin() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token, "/secret", "POST");
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var filterChain = mock(FilterChain.class);

        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testAuthFilterAllowsAdminToAccessEndpointWithAnyRole() throws ServletException, IOException, InvalidCredentialsException {
        var userCredentials = new SimpleCredentials("user", "pass");
        var token = createAuthorizationHeaderForCredentials(userCredentials);
        var authenticator = mockAuthenticator(userCredentials, mockAuthenticationWithRoles(new Role[] { admin() }));

        var response = mock(HttpServletResponse.class);
        var request = mockHttpServletRequest(token);
        var authFilter = initFilter(authenticator, simpleSecurityConfig());
        var filterChain = mock(FilterChain.class);

        authFilter.doFilter(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    protected abstract String malformedCredentials();

    protected abstract String createAuthorizationHeaderForCredentials(Credentials credentials);

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

    private HttpServletRequest mockHttpServletRequest(String token) {
        return mockHttpServletRequest(token, "/", "GET");
    }

    private HttpServletRequest mockHttpServletRequest(String authorizationHeaderValue, String uri, String method) {
        var result = mock(HttpServletRequest.class);
        when(result.getRequestURI()).thenReturn(uri);
        when(result.getMethod()).thenReturn(method);
        when(result.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
        return result;
    }

    protected abstract AuthenticationStrategy authenticationStrategy();

    private SecurityConfig simpleSecurityConfig() {
        return new SecurityConfig(authenticationStrategy(),
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
