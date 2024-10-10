package io.john.amiscaray.quak.web.test.application.stub.di;

import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.security.auth.credentials.Credentials;
import io.john.amiscaray.quak.security.auth.principal.Principal;
import io.john.amiscaray.quak.security.auth.principal.RoleAttachedPrincipal;
import io.john.amiscaray.quak.security.auth.principal.role.Role;
import io.john.amiscaray.quak.web.test.application.stub.roles.Roles;

import java.time.Duration;
import java.util.Optional;

public class SimpleAuthenticator implements Authenticator {

    private static final RoleAttachedPrincipal JOHN = new RoleAttachedPrincipal() {
        @Override
        public Role[] getRoles() {
            return new Role[] { Roles.user() };
        }

        @Override
        public String getSecurityID() {
            return "Johnny Boy";
        }
    };

    private static final RoleAttachedPrincipal ELLI = new RoleAttachedPrincipal() {
        @Override
        public Role[] getRoles() {
            return new Role[] { Roles.admin() };
        }

        @Override
        public String getSecurityID() {
            return "Elli";
        }
    };

    @Override
    public Optional<Principal> lookupPrincipal(String s) {
        if (s.equals(JOHN.getSecurityID())) {
            return Optional.of(JOHN);
        } else if (s.equals(ELLI.getSecurityID())) {
            return Optional.of(ELLI);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Principal> lookupPrincipal(Credentials credentials) {
        if (credentials.getUsername().equals("John") && credentials.getPassword().equals("password")) {
            return Optional.of(JOHN);
        } else if (credentials.getUsername().equals("Elli") && credentials.getPassword().equals("password")) {
            return Optional.of(ELLI);
        }
        return Optional.empty();
    }

    @Override
    public Duration getAuthenticationValidDuration() {
        return Duration.ofDays(30);
    }

}
