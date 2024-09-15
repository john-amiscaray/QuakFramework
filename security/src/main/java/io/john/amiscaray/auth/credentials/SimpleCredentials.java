package io.john.amiscaray.auth.credentials;

import lombok.AllArgsConstructor;

@AllArgsConstructor
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
