package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.core.test.di.stub.pojo.Course;
import io.john.amiscaray.quak.core.test.di.stub.pojo.Program;

import java.util.List;

public class MockProgramProvider implements DependencyProvider<Program> {

    @Override
    public boolean isDependencyOptional() {
        return true;
    }

    @Override
    public DependencyID<Program> getDependencyID() {
        return new DependencyID<>(Program.class);
    }

    @Override
    public ProvidedDependency<Program> provideDependency(ApplicationContext context) {
        return new ProvidedDependency<>(getDependencyID(), new Program("Computer Science", List.of(context.getInstance(Course.class))));
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        return List.of(new DependencyID<>(Course.class));
    }
}
