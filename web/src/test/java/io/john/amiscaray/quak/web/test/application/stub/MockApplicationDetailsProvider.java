package io.john.amiscaray.quak.web.test.application.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

@Provider
public class MockApplicationDetailsProvider {

    @Provide(dependencyName = "applicationName")
    public String applicationName() {
        return "My Cool Web App";
    }

    @Provide(dependencyName = "version")
    public float version() {
        return 2.1f;
    }

}
