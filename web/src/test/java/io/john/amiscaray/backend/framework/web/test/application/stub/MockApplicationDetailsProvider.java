package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

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