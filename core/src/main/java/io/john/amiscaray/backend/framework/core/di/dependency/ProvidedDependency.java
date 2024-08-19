package io.john.amiscaray.backend.framework.core.di.dependency;

public record ProvidedDependency<T>(DependencyID<T> id, T instance) {
}
