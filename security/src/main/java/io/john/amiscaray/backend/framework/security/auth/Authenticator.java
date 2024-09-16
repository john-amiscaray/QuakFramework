package io.john.amiscaray.backend.framework.security.auth;

import io.john.amiscaray.backend.framework.security.auth.credentials.Credentials;
import io.john.amiscaray.backend.framework.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.backend.framework.security.auth.principal.Principal;

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

    default Authentication authenticate(String token) throws InvalidCredentialsException {
        var principal = lookupPrincipal(token);
        if (principal.isEmpty()) {
            throw new InvalidCredentialsException();
        }
        return new SimpleAuthentication(
                principal.get(),
                Date.from(Instant.now()),
                Date.from(Instant.now().plus(getAuthenticationValidDuration()))
        );
    }

    default Optional<Principal> lookupPrincipal(String token) {
        return Optional.empty(); // Optional Implementation
    }

    Optional<Principal> lookupPrincipal(Credentials credentials);

    default Duration getAuthenticationValidDuration() {
        return Duration.ofHours(12);
    }

}
