package io.john.amiscaray.quak.security.auth;

import io.john.amiscaray.quak.security.auth.credentials.Credentials;
import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.quak.security.auth.principal.Principal;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public interface Authenticator {

    default Authentication authenticate(Credentials credentials) throws InvalidCredentialsException {
        var principal = lookupPrincipal(credentials);
        if (principal.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        return new SimpleAuthentication(
                principal.get(),
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(getAuthenticationValidDuration()))
        );
    }

    default Authentication authenticate(String securityID) throws InvalidCredentialsException {
        var principal = lookupPrincipal(securityID);
        if (principal.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        return new SimpleAuthentication(
                principal.get(),
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(getAuthenticationValidDuration()))
        );
    }

    Optional<Principal> lookupPrincipal(String securityID);

    Optional<Principal> lookupPrincipal(Credentials credentials);

    default Duration getAuthenticationValidDuration() {
        return Duration.ofHours(12);
    }

}
