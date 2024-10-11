package io.john.amiscaray.quak.security.auth.principal;

import io.john.amiscaray.quak.security.auth.principal.role.Role;

public interface RoleAttachedPrincipal extends Principal {

    Role[] getRoles();

}
