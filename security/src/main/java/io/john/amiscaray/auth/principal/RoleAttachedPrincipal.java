package io.john.amiscaray.auth.principal;

public interface RoleAttachedPrincipal extends Principal {

    String[] getRoles();

}
