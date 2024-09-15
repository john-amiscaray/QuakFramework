package io.john.amiscaray.auth;

import io.john.amiscaray.auth.principal.Principal;

import java.util.Date;

public interface Authentication {

    Principal getIssuedTo();
    Date getIssueTime();
    Date getExpirationTime();

}
