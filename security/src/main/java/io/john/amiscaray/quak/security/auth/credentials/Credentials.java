package io.john.amiscaray.quak.security.auth.credentials;

/**
 * Represents the user credentials as a username and password.
 */
public interface Credentials {

    /**
     * Gets the username.
     * @return The username.
     */
    String getUsername();

    /**
     * Gets the password.
     * @return The password.
     */
    String getPassword();

}
