package io.john.amiscaray.backend.framework.data.di;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.StartupDependencyProvider;
import io.john.amiscaray.backend.framework.data.DatabaseProxy;

public class DatabaseProxyStartupDependencyProvider implements StartupDependencyProvider<DatabaseProxy> {

    @Override
    public ProvidedDependency<DatabaseProxy> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(
                new DependencyID<>("databaseProxy", DatabaseProxy.class),
                new DatabaseProxy(context.getClassScanPackage())
                );
    }

}
