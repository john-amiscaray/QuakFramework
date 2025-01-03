package io.john.amiscaray.quak.core.di.provider;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;

import java.util.List;

/**
 * Provides a dependency to the application context. This can be used with Java's service loader functionality so the core module will load in implementations of this interface to add dependencies to the application context.
 * @param <T> The type of the dependency being provided.
 */
public interface DependencyProvider<T> {

    /**
     * @return An aggregate list to add this dependency to. See the {@link io.john.amiscaray.quak.core.di.provider.annotation.AggregateTo AggregateTo} annotation.
     */
    default String aggregateList() {
        return "";
    }

    /**
     * @return Whether this dependency needs to be loaded for the application to start property.
     */
    default boolean isDependencyOptional() {
        return false;
    }

    /**
     * @return The ID of this dependency.
     */
    DependencyID<T> getDependencyID();

    /**
     * Provide the dependency to the application context.
     * @param context The application context. Used to get any instances needed to instantiate/initialize the dependency.
     * @return The provided dependency.
     */
    ProvidedDependency<T> provideDependency(ApplicationContext context);

    /**
     * @return A list of the dependencies required to instantiate this dependency.
     */
    List<DependencyID<?>> getDependencies();

}
