package io.john.amiscaray.quak.core.di.dependency;

/**
 * Represents an ID for a dependency in the application context.
 * @param name The name of the dependency
 * @param type The type of the dependency
 * @param <T> A generic argument matching the type of the dependency
 */
public record DependencyID<T>(String name, Class<T> type) {

    public DependencyID(Class<T> type) {
        this("", type);
    }

}
