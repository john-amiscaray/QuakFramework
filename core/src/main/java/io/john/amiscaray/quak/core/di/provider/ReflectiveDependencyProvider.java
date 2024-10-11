package io.john.amiscaray.quak.core.di.provider;

import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to reflectively provide an instance to the application context.
 * @param <T> The type of the instance.
 */
public interface ReflectiveDependencyProvider<T> extends DependencyProvider<T> {

    Executable getExecutableReturningInstance();

    @Override
    default List<DependencyID<?>> getDependencies() {
        var parameters = getExecutableReturningInstance().getParameters();
        var result = new ArrayList<DependencyID<?>>();

        for(var parameter : parameters) {
            var dependencyName = parameter.isAnnotationPresent(ProvidedWith.class) ? parameter.getAnnotation(ProvidedWith.class).dependencyName() : "";
            result.add(new DependencyID<>(dependencyName, ClassUtils.primitiveToWrapper(parameter.getType())));
        }

        return result;
    }
}
