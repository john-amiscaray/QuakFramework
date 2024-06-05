package io.john.amiscaray.backend.framework.web.test.application.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

@Provider
public class MockApplicationNameProvider {

    @Provide(dependencyName = "applicationName")
    public String applicationName() {
        return "My Cool Web App";
    }

}
