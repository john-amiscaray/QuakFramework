package io.john.amiscaray.quak.security.auth;

import io.john.amiscaray.quak.security.auth.principal.Principal;

import java.util.Date;

public interface Authentication {

    Principal getIssuedTo();
    Date getIssueTime();
    Date getExpirationTime();

}
