package io.john.amiscaray.quak.core.di.exception;

/**
 * Thrown when a ManagedType is declared incorrectly. This can occur if the type declared in the annotation is not a matching the class or a superclass of it.
 */
public class InvalidManagedTypeDeclarationException extends RuntimeException{

    public InvalidManagedTypeDeclarationException(String message) {
        super(message);
    }

}
