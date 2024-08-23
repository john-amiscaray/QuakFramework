package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provider;

@Provider
public class ColorProvider {

    @Provide(dependencyName = "primary")
    public Color primary() {
        return Color.RED;
    }

    @Provide(dependencyName = "secondary")
    public Color secondary() {
        return Color.BLUE;
    }

    @Provide(dependencyName = "tertiary")
    public Color tertiary() {
        return Color.GREEN;
    }

}
