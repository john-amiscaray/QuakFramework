package io.john.amiscaray.quak.security.auth;

import io.john.amiscaray.quak.security.auth.credentials.Credentials;
import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.quak.security.auth.principal.Principal;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
 * Used by the security framework to authenticate credentials.
 */
public interface Authenticator {

    /**
     * Authenticate the given credentials.
     * @param credentials The credentials.
     * @return The authentication.
     * @throws InvalidCredentialsException if the credentials were invalid.
     */
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

    /**
     * Tests that a given securityID exists.
     * @param securityID The securityID.
     * @return An authentication for the user.
     * @throws InvalidCredentialsException if there is no user with the given security ID.
     */
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

    /**
     * Look up the user with the given security ID.
     * @param securityID The security ID.
     * @return The user's principal as an optional.
     */
    Optional<Principal> lookupPrincipal(String securityID);

    /**
     * Look up the user with the given credentials.
     * @param credentials The user's credentials.
     * @return The user's principal as an optional.
     */
    Optional<Principal> lookupPrincipal(Credentials credentials);

    /**
     * @return The duration an authentication is valid.
     */
    default Duration getAuthenticationValidDuration() {
        return Duration.ofHours(12);
    }

}
