package io.john.amiscaray.quak.data.di;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.data.DatabaseProxy;

import java.util.List;

public class DatabaseProxyStartupDependencyProvider implements DependencyProvider<DatabaseProxy> {

    @Override
    public DependencyID<DatabaseProxy> getDependencyID() {
        return new DependencyID<>("databaseProxy", DatabaseProxy.class);
    }

    @Override
    public ProvidedDependency<DatabaseProxy> provideDependency(ApplicationContext context) {
        var databaseProxy = new DatabaseProxy(context.getClassScanPackage());
        databaseProxy.beginSession();

        return new ProvidedDependency<>(
                new DependencyID<>("databaseProxy", DatabaseProxy.class),
                databaseProxy
                );
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of();
    }

}
