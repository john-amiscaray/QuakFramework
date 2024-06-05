package io.john.amiscaray.backend.framework.core.di.exception;

public class DependencyInstantiationException extends RuntimeException{

    public DependencyInstantiationException(Class<?> clazz, Throwable cause) {
        super("Could not instantiate " + clazz.getTypeName(), cause);
    }
}
