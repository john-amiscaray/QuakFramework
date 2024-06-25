package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.ProvidedWith;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

@Provider
public record ApplicationUIDetails(
        @ProvidedWith(dependencyName = "colorScheme") ColorScheme mainColorScheme,
        @ProvidedWith(dependencyName = "font") String font) {

    @Provide
    public ApplicationUIDetails {

    }

}
