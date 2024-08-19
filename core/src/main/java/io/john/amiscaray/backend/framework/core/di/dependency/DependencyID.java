package io.john.amiscaray.backend.framework.core.di.dependency;

public record DependencyID<T>(String name, Class<T> type) {
}
