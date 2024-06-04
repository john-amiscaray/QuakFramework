package io.john.amiscaray.backend.framework.core.di.dependency;

public record Dependency<T>(String name, Class<T> type) {
}
