package io.john.amiscaray.backend.framework.core.di.exception;

import java.util.Set;

public class DependencyResolutionException extends RuntimeException{

    public DependencyResolutionException(Set<Class<?>> types) {
        super("Could not build application context. Failed to satisfy the dependencies for following classes in the application context:\n" +
                String.join("\n", types.stream().map(Class::getTypeName).toArray(String[]::new)));
    }

}
