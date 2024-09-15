package io.john.amiscaray.backend.framework.security.auth.principal;

public interface RoleAttachedPrincipal extends Principal {

    String[] getRoles();

}
