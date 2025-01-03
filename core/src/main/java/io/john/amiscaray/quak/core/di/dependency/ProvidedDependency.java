package io.john.amiscaray.quak.core.di.dependency;

/**
 * Represents the concrete dependency. Contains its ID and the instance.
 * @param id The id of the provided dependency.
 * @param instance The instance of the dependency.
 * @param <T> A generic argument matching the type of the instance.
 */
public record ProvidedDependency<T>(DependencyID<T> id, T instance) {
}
