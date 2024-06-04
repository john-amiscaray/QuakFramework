package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

@Provider
public class MockStringProvider {

    @Provide
    public String username() {
        return "John";
    }

}
