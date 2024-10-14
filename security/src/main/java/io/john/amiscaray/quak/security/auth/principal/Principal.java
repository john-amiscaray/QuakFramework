package io.john.amiscaray.quak.security.auth.principal;

/**
 * The user's security principal.
 */
public interface Principal {

    /**
     * @return The security ID of the user.
     */
    String getSecurityID();

}
