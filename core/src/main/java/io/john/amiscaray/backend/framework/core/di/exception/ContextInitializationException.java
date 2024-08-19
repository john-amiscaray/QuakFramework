package io.john.amiscaray.backend.framework.core.di.exception;

public class ContextInitializationException extends RuntimeException{

    public ContextInitializationException(Throwable cause) {
        super("Unable to initialize application context with exception: " + cause.getMessage());
    }

}
