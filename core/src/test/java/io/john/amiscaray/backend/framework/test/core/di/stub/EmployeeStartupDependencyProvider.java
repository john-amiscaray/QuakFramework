package io.john.amiscaray.backend.framework.test.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockEmployee;

import java.util.List;

import static io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockEmployee.mockEmployee;

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
                mockEmployee()
        );
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of();
    }

}
