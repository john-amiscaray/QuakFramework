package io.john.amiscaray.quak.security.auth.exception;

/**
 * Thrown when a user's credentials are invalid.
 */
public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException() {
        super("The given credentials are invalid.");
    }

}
