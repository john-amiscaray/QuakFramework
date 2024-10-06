package io.john.amiscaray.backend.framework.web.test.application.stub.roles;

import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;

public class Roles {

    public static Role user() {
        return () -> "USER";
    }

    public static Role admin() {
        return () -> "ADMIN";
    }

}
