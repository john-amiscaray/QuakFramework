package io.john.amiscaray.backend.framework.security.auth;

import io.john.amiscaray.backend.framework.security.auth.principal.Principal;

import java.util.Date;

public interface Authentication {

    Principal getIssuedTo();
    Date getIssueTime();
    Date getExpirationTime();

}
