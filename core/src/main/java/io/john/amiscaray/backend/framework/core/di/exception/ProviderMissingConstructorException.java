package io.john.amiscaray.backend.framework.core.di.exception;

public class ProviderMissingConstructorException extends RuntimeException{

    public ProviderMissingConstructorException(Class<?> provider) {
        super("Could not instantiate provider of type:" + provider.getName() + ". " +
                "Provider is likely missing an accessible constructor. A provider must have a constructor annotated with @Provide or an empty constructor");
    }
}