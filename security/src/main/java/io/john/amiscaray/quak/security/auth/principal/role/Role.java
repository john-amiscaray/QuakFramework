package io.john.amiscaray.quak.security.auth.principal.role;

/**
 * Represents a user role for role-based authorization.
 */
public interface Role {

    String ANY_ROLE = "ANY";

    /**
     * Allow any user.
     * @return A role representing any user.
     */
    static Role any() {
        return () -> ANY_ROLE;
    }

    /**
     * @return This role as a String.
     */
    String asText();

}
