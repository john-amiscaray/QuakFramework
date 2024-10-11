package io.john.amiscaray.quak.core.di.provider;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;

import java.util.List;

/**
 * Provides a dependency upon module load after the initial dependency graph was created using classes/methods annotated
 * with dependency injection markers like @Instantiate, @ManagedType, @Provider, and @ProvidedWith. Using Java's service
 * loading system, the core module will load in implementations of this interface to add dependencies to the application
 * context.
 * @param <T> The types of the dependency being provided
 */
public interface DependencyProvider<T> {

    default String aggregateList() {
        return "";
    }

    default boolean isDependencyOptional() {
        return false;
    }

    DependencyID<T> getDependencyID();

    ProvidedDependency<T> provideDependency(ApplicationContext context);

    List<DependencyID<?>> getDependencies();

}
