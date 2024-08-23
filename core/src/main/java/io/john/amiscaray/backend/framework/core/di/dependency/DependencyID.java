package io.john.amiscaray.backend.framework.core.di.dependency;

public record DependencyID<T>(String name, Class<T> type) {

    public DependencyID(Class<T> type) {
        this("", type);
    }

}
