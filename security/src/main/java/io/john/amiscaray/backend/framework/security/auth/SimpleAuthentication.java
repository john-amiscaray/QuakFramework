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
