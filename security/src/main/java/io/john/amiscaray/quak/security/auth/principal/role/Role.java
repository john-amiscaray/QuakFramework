package io.john.amiscaray.quak.security.auth.principal.role;

public interface Role {

    String ANY_ROLE = "ANY";

    static Role any() {
        return () -> ANY_ROLE;
    }

    String asText();

}
