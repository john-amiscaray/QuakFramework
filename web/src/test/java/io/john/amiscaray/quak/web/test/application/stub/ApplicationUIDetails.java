package io.john.amiscaray.quak.web.test.application.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

@Provider
public record ApplicationUIDetails(
        @ProvidedWith(dependencyName = "colorScheme") ColorScheme mainColorScheme,
        @ProvidedWith(dependencyName = "font") String font) {

    @Instantiate
    public ApplicationUIDetails {

    }

}
