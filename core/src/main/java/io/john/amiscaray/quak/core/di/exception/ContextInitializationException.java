package io.john.amiscaray.quak.core.di.exception;

public class ContextInitializationException extends RuntimeException{

    public ContextInitializationException(Throwable cause) {
        super("Unable to initialize application context with exception: " + cause.getMessage());
    }

}