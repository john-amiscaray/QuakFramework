package io.john.amiscaray.quak.core.di.exception;

/**
 * Thrown when a dependency could not be instantiated.
 */
public class DependencyInstantiationException extends RuntimeException{

    public DependencyInstantiationException(Class<?> clazz, Throwable cause) {
        super("Could not instantiate " + clazz.getTypeName(), cause);
    }
}
