package io.john.amiscaray.quak.core.di.provider;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
import io.john.amiscaray.quak.core.di.exception.DependencyInstantiationException;
import io.john.amiscaray.quak.core.di.exception.InvalidDeclarationException;
import io.john.amiscaray.quak.core.di.provider.annotation.AggregateTo;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;
import lombok.AllArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;

/**
 * Used to provide an instance to the application context from a constructor.
 * @param <T> The type of the instance.
 */
@AllArgsConstructor
public class ConstructorDependencyProvider<T> implements ReflectiveDependencyProvider<T> {

    private Constructor<T> constructorReturningInstance;

    @Override
    public String aggregateList() {
        if (constructorReturningInstance.isAnnotationPresent(AggregateTo.class)) {
            return constructorReturningInstance.getAnnotation(AggregateTo.class).aggregateList();
        }
        return "";
    }

    @Override
    public DependencyID<T> getDependencyID() {
        var declaringClass = constructorReturningInstance.getDeclaringClass();
        var declaredType = declaringClass;
        var dependencyName = "";
        if (declaringClass.isAnnotationPresent(ManagedType.class)) {
            var annotationDeclaration = declaringClass.getAnnotation(ManagedType.class);
            dependencyName = annotationDeclaration.dependencyName();
            if (annotationDeclaration.dependencyType().isAssignableFrom(declaringClass)) {
                declaredType = (Class<T>) annotationDeclaration.dependencyType();
            } else if (annotationDeclaration.dependencyType() != Void.class) {
                throw new InvalidDeclarationException("Invalid dependency type: " + annotationDeclaration.dependencyType() + " for managed type of class: " + declaringClass);
            }

        } else if (declaringClass.isAnnotationPresent(Provider.class)) {
            dependencyName = declaringClass.getAnnotation(Provider.class).dependencyName();
        }
        if (dependencyName != null && !dependencyName.isEmpty()) {
            return new DependencyID<>(dependencyName, declaredType);
        }
        return new DependencyID<>(declaredType);
    }

    @Override
    public ProvidedDependency<T> provideDependency(ApplicationContext context) {
        try {
            var instance = constructorReturningInstance.newInstance(context.fetchInstancesOfParameters(constructorReturningInstance.getParameters()));
            return new ProvidedDependency<>(getDependencyID(), instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DependencyInstantiationException(constructorReturningInstance.getDeclaringClass(), e);
        }
    }

    @Override
    public Executable getExecutableReturningInstance() {
        return constructorReturningInstance;
    }
}
