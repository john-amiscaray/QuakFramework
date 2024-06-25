package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.ManagedType;

@ManagedType
public record ColorScheme(Color primary, Color secondary, Color tertiary) {

    @Instantiate
    public ColorScheme {

    }

}
