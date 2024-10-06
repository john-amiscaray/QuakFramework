package io.john.amiscaray.backend.framework.security.auth.principal;

import io.john.amiscaray.backend.framework.security.auth.principal.role.Role;

public interface RoleAttachedPrincipal extends Principal {

    Role[] getRoles();

}
