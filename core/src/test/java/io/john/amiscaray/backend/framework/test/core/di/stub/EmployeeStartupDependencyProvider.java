package io.john.amiscaray.backend.framework.test.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.provider.StartupDependencyProvider;
import io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockEmployee;

import static io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockEmployee.mockEmployee;

public class EmployeeStartupDependencyProvider implements StartupDependencyProvider<MockEmployee> {

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

}
