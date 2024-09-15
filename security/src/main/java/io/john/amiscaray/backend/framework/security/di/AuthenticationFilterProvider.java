package io.john.amiscaray.backend.framework.security.di;

import io.john.amiscaray.backend.framework.security.auth.filter.HttpBasicAuthFilter;
import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import jakarta.servlet.Filter;

import java.util.List;

import static io.john.amiscaray.backend.framework.security.di.SecurityDependencyIDs.*;

public class AuthenticationFilterProvider implements DependencyProvider<Filter> {

    @Override
    public DependencyID<Filter> getDependencyID() {
        return SECURITY_FILTER_DEPENDENCY;
    }

    @Override
    public ProvidedDependency<Filter> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(getDependencyID(), new HttpBasicAuthFilter(context.getInstance(AUTHENTICATOR_DEPENDENCY)));
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of(
                SECURITY_STRATEGY_DEPENDENCY,
                AUTHENTICATOR_DEPENDENCY
        );
    }

}
