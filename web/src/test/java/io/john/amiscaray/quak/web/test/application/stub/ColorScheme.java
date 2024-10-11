package io.john.amiscaray.quak.web.test.application.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;

@ManagedType
public record ColorScheme(Color primary, Color secondary, Color tertiary) {

    @Instantiate
    public ColorScheme {

    }

}
