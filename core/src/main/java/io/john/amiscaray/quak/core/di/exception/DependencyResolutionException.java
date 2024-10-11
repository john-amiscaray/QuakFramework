package io.john.amiscaray.quak.core.di.exception;

import java.util.Set;

/**
 * Thrown when the application context could not instantiate all the required dependencies.
 */
public class DependencyResolutionException extends RuntimeException{

    public DependencyResolutionException(Set<Class<?>> types) {
        super("Could not build application context. Failed to satisfy the dependencies for following classes in the application context:\n" +
                String.join("\n", types.stream().map(Class::getTypeName).toArray(String[]::new)));
    }

}
