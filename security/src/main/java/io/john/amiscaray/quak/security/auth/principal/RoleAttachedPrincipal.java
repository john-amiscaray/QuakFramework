package io.john.amiscaray.quak.security.auth.principal;

import io.john.amiscaray.quak.security.auth.principal.role.Role;

/**
 * A user principal with an array of roles.
 */
public interface RoleAttachedPrincipal extends Principal {

    /**
     * @return The user's roles.
     */
    Role[] getRoles(); // TODO make this a set.

}
