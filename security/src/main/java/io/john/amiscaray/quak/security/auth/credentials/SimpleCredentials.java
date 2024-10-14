package io.john.amiscaray.quak.security.auth.credentials;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

/**
 * Simple user credentials: a username and a password.
 */
@AllArgsConstructor
@EqualsAndHashCode
public class SimpleCredentials implements Credentials{

    private String username;
    private String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
