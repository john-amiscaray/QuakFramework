package io.john.amiscaray.config;

import io.john.amiscaray.auth.Authenticator;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ProvidedWith;
import jakarta.servlet.Filter;

@ManagedType
public record SecurityConfiguration(
        @ProvidedWith(dependencyName = AUTHENTICATOR_DEPENDENCY)
        Authenticator authenticator,
        @ProvidedWith(dependencyName = AUTHENTICATION_FILTER_DEPENDENCY)
        Filter authenticationFilter) {

    public static final String AUTHENTICATOR_DEPENDENCY = "applicationAuthenticator";
    public static final String AUTHENTICATION_FILTER_DEPENDENCY = "applicationAuthenticationFilter";

}
