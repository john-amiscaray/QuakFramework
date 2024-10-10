package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.core.test.di.stub.pojo.MockEmployee;

import java.util.List;

public class EmployeeStartupDependencyProvider implements DependencyProvider<MockEmployee> {

    @Override
    public String aggregateList() {
        return "EmployeeProviders";
    }

    @Override
    public DependencyID<MockEmployee> getDependencyID() {
        return new DependencyID<>(
                "employee",
                MockEmployee.class
        );
    }

    @Override
    public ProvidedDependency<MockEmployee> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(
                new DependencyID<>(
                        "employee",
                        MockEmployee.class
                ),
                MockEmployee.mockEmployee()
        );
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of();
    }

}
