package io.john.amiscaray.backend.framework.core.di.provider;

import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ProvidedWith;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

public interface ReflectiveDependencyProvider<T> extends DependencyProvider<T> {

    Executable getExecutableReturningInstance();

    @Override
    default List<DependencyID<?>> getDependencies() {
        var parameters = getExecutableReturningInstance().getParameters();
        var result = new ArrayList<DependencyID<?>>();

        for(var parameter : parameters) {
            var dependencyName = parameter.isAnnotationPresent(ProvidedWith.class) ? parameter.getAnnotation(ProvidedWith.class).dependencyName() : "";
            result.add(new DependencyID<>(dependencyName, parameter.getType()));
        }

        return result;
    }
}
