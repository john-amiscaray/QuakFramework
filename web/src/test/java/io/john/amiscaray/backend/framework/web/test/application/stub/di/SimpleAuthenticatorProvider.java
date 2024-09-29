package io.john.amiscaray.backend.framework.web.test.application.stub.di;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.security.auth.Authenticator;
import io.john.amiscaray.backend.framework.security.di.SecurityDependencyIDs;

import java.util.List;

public class SimpleAuthenticatorProvider implements DependencyProvider<Authenticator> {

    @Override
    public DependencyID<Authenticator> getDependencyID() {
        return SecurityDependencyIDs.AUTHENTICATOR_DEPENDENCY;
    }

    @Override
    public ProvidedDependency<Authenticator> provideDependency(ApplicationContext applicationContext) {
        return new ProvidedDependency<>(getDependencyID(), new SimpleAuthenticator());
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of();
    }

}
