package io.john.amiscaray.quak.web.test.stub;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.web.cfg.WebConfig;
import io.john.amiscaray.quak.web.test.stub.exception.DummyException;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class WebConfigProvider implements DependencyProvider<WebConfig> {
    @Override
    public DependencyID<WebConfig> getDependencyID() {
        return WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_ID;
    }

    @Override
    public ProvidedDependency<WebConfig> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(getDependencyID(), WebConfig.builder()
                .mapExceptionToStatusCode(DummyException.class, HttpServletResponse.SC_BAD_REQUEST)
                .build());
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of();
    }
}
