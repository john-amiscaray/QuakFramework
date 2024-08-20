package io.john.amiscaray.backend.framework.core.di.provider;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyInstantiationException;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.Provide;
import lombok.AllArgsConstructor;

import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@AllArgsConstructor
public class MethodDependencyProvider<T, U> implements ReflectiveDependencyProvider<U>{

    private final Method methodReturningInstance;
    private final T providerInstance;

    @Override
    public DependencyID<U> getDependencyID() {
        return (DependencyID<U>) new DependencyID<>(methodReturningInstance.getAnnotation(Provide.class).dependencyName(), methodReturningInstance.getReturnType());
    }

    @Override
    public ProvidedDependency<U> provideDependency(ApplicationContext context) {
        try {
            var instance = (U) methodReturningInstance.invoke(providerInstance, context.fetchInstancesOfParameters(methodReturningInstance.getParameters()));
            return new ProvidedDependency<>(getDependencyID(), instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DependencyInstantiationException(methodReturningInstance.getReturnType(), e);
        }
    }

    @Override
    public Executable getExecutableReturningInstance() {
        return methodReturningInstance;
    }
}
