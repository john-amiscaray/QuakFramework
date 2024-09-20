package io.john.amiscaray.backend.framework.security.test.auth.filter;

import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.auth.credentials.Credentials;
import io.john.amiscaray.backend.framework.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.backend.framework.security.auth.filter.SecurityFilter;
import io.john.amiscaray.backend.framework.security.config.SecurityConfig;
import io.john.amiscaray.backend.framework.security.di.AuthenticationStrategy;

import java.util.Base64;

public class HttpBasicAuthFilterTest extends SecurityFilterTest{

    @Override
    public SecurityFilter initFilter(Authenticator authenticator, SecurityConfig config) {
        return new HttpBasicAuthFilter(authenticator, config);
    }

    @Override
    protected String malformedCredentials() {
        return "Basic " + Base64.getEncoder().encodeToString("user pass".getBytes());
    }

    @Override
    protected String createAuthorizationHeaderForCredentials(Credentials credentials) {
        var credentialsString = credentials.getUsername() + ":" + credentials.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(credentialsString.getBytes());
    }

    @Override
    protected AuthenticationStrategy authenticationStrategy() {
        return AuthenticationStrategy.BASIC;
    }


}
