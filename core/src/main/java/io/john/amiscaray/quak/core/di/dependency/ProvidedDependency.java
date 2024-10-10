package io.john.amiscaray.quak.core.di.dependency;

public record ProvidedDependency<T>(DependencyID<T> id, T instance) {
}
