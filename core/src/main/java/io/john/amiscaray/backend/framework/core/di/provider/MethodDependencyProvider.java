package io.john.amiscaray.backend.framework.core.di.provider;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyInstantiationException;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.AggregateTo;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provide;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@AllArgsConstructor
public class MethodDependencyProvider<T> implements ReflectiveDependencyProvider<T>{

    private final Method methodReturningInstance;

    @Override
    public String aggregateList() {
        if (methodReturningInstance.isAnnotationPresent(AggregateTo.class)) {
            return methodReturningInstance.getAnnotation(AggregateTo.class).aggregateList();
        }
        return "";
    }

    @Override
    public DependencyID<T> getDependencyID() {
        return (DependencyID<T>) new DependencyID<>(methodReturningInstance.getAnnotation(Provide.class).dependencyName(), ClassUtils.primitiveToWrapper(methodReturningInstance.getReturnType()));
    }

    @Override
    public ProvidedDependency<T> provideDependency(ApplicationContext context) {
        try {
            var instance = (T) methodReturningInstance.invoke(
                    context.getInstance(methodReturningInstance.getDeclaringClass()),
                    context.fetchInstancesOfParameters(methodReturningInstance.getParameters()));
            return new ProvidedDependency<>(getDependencyID(), instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DependencyInstantiationException(methodReturningInstance.getReturnType(), e);
        }
    }

    @Override
    public Executable getExecutableReturningInstance() {
        return methodReturningInstance;
    }

    @Override
    public List<DependencyID<?>> getDependencies() {
        var dependencies = ReflectiveDependencyProvider.super.getDependencies();
        dependencies.add(new DependencyID<>(methodReturningInstance.getDeclaringClass()));
        return dependencies;
    }
}
