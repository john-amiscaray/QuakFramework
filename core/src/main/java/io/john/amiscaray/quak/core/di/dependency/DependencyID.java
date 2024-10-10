package io.john.amiscaray.quak.core.di.dependency;

public record DependencyID<T>(String name, Class<T> type) {

    public DependencyID(Class<T> type) {
        this("", type);
    }

}
