package io.john.amiscaray.quak.core.di.exception;

/**
 * Thrown when an error occurs during the initialization of the application context.
 */
public class ContextInitializationException extends RuntimeException{

    public ContextInitializationException(Throwable cause) {
        super("Unable to initialize application context with exception: " + cause.getMessage());
    }

}
