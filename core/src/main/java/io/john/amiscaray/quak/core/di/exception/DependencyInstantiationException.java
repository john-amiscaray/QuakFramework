package io.john.amiscaray.quak.core.di.exception;

public class DependencyInstantiationException extends RuntimeException{

    public DependencyInstantiationException(Class<?> clazz, Throwable cause) {
        super("Could not instantiate " + clazz.getTypeName(), cause);
    }
}
