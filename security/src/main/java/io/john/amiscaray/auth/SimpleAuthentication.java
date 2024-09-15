package io.john.amiscaray.auth;

import io.john.amiscaray.auth.principal.Principal;
import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class SimpleAuthentication implements Authentication{

    private Principal principal;
    private Date issueTime;
    private Date expirationTime;

    @Override
    public Principal getIssuedTo() {
        return null;
    }

    @Override
    public Date getIssueTime() {
        return null;
    }

    @Override
    public Date getExpirationTime() {
        return null;
    }

}
