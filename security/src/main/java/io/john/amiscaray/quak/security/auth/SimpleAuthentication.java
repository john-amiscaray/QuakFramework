package io.john.amiscaray.quak.security.auth;

import io.john.amiscaray.quak.security.auth.principal.Principal;
import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * A simple {@link io.john.amiscaray.quak.security.auth.Authentication}.
 */
@AllArgsConstructor
public class SimpleAuthentication implements Authentication{

    private Principal principal;
    private Date issueTime;
    private Date expirationTime;

    @Override
    public Principal getIssuedTo() {
        return principal;
    }

    @Override
    public Date getIssueTime() {
        return issueTime;
    }

    @Override
    public Date getExpirationTime() {
        return expirationTime;
    }

}
