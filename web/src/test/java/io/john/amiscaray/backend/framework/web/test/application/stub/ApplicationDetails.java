package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.ManagedType;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;

@ManagedType
public record ApplicationDetails(
        @ProvidedWith(dependencyName = "applicationName") String name,
        @ProvidedWith(dependencyName = "version") float version) {

    @Instantiate
    public ApplicationDetails {

    }

}
