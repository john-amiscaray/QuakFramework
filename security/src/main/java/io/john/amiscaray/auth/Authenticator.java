package io.john.amiscaray.auth;

import io.john.amiscaray.auth.credentials.Credentials;
import io.john.amiscaray.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.auth.principal.Principal;

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

    Optional<Principal> lookupPrincipal(Credentials credentials);

    Duration getAuthenticationValidDuration();

}
