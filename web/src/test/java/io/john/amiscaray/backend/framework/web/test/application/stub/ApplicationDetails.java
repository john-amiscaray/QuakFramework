package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ProvidedWith;

@ManagedType
public record ApplicationDetails(
        @ProvidedWith(dependencyName = "applicationName") String name,
        @ProvidedWith(dependencyName = "version") float version) {

    @Instantiate
    public ApplicationDetails {

    }

}
