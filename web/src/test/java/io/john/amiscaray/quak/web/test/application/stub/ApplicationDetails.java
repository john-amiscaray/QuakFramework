package io.john.amiscaray.quak.web.test.application.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;

@ManagedType
public record ApplicationDetails(
        @ProvidedWith(dependencyName = "applicationName") String name,
        @ProvidedWith(dependencyName = "version") float version) {

    @Instantiate
    public ApplicationDetails {

    }

}
