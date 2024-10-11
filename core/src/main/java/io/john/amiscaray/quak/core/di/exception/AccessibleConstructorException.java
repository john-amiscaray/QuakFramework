package io.john.amiscaray.quak.core.di.exception;

/**
 * Thrown when the application context could not instantiate a dependency because it can not access its constructor.
 */
public class AccessibleConstructorException extends RuntimeException{

    public AccessibleConstructorException(Class<?> provider) {
        super("Could not instantiate instance of type:" + provider.getName() + ". " +
                "The class is likely missing an accessible constructor. The class must have a constructor annotated with @Instantiate or an empty constructor");
    }
}
