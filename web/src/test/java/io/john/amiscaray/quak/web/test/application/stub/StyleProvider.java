package io.john.amiscaray.quak.web.test.application.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

@Provider
public class StyleProvider {

    private Color primary;
    private Color secondary;
    private Color tertiary;

    @Instantiate
    public StyleProvider(
            @ProvidedWith(dependencyName = "primary") Color primary,
            @ProvidedWith(dependencyName = "secondary") Color secondary,
            @ProvidedWith(dependencyName = "tertiary") Color tertiary
    ) {
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    @Provide(dependencyName = "colorScheme")
    public ColorScheme colorScheme() {
        return new ColorScheme(primary, secondary, tertiary);
    }

    @Provide(dependencyName = "font")
    public String font() {
        return "Comic Sans";
    }

}
