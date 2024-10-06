package io.john.amiscaray.backend.framework.security.di;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.security.cors.filter.CORSFilter;

import java.util.List;

public class CORSFilterProvider implements DependencyProvider<CORSFilter> {
    @Override
    public DependencyID<CORSFilter> getDependencyID() {
        return new DependencyID<>(SecurityDependencyIDs.CORS_FILTER_DEPENDENCY_NAME, CORSFilter.class);
    }

    @Override
    public ProvidedDependency<CORSFilter> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(getDependencyID(), new CORSFilter(context.getInstance(SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY)));
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of(SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY);
    }
}
