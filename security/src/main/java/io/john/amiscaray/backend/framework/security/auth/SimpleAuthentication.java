package io.john.amiscaray.backend.framework.security.auth;

import io.john.amiscaray.backend.framework.security.auth.principal.Principal;
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
