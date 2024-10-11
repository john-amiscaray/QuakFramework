package io.john.amiscaray.quak.core.di.exception;

public class AccessibleConstructorException extends RuntimeException{

    public AccessibleConstructorException(Class<?> provider) {
        super("Could not instantiate instance of type:" + provider.getName() + ". " +
                "The class is likely missing an accessible constructor. The class must have a constructor annotated with @Instantiate or an empty constructor");
    }
}
