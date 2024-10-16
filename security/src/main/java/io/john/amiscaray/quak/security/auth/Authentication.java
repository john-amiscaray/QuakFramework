package io.john.amiscaray.quak.security.auth;

import io.john.amiscaray.quak.security.auth.principal.Principal;

import java.util.Date;

/**
 * The user's authentication.
 */
public interface Authentication {

    /**
     * @return A principal for the user this was issued to.
     */
    Principal getIssuedTo();

    /**
     * @return The time this authentication was issued.
     */
    Date getIssueTime();

    /**
     * @return The time this authentication will expire.
     */
    Date getExpirationTime();

}
